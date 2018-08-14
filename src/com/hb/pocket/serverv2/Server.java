package com.hb.pocket.serverv2;

import com.hb.pocket.serverv2.thread.*;
import com.hb.pocket.serverv2.thread.callback.IServerSelectorReadCallback;
import com.hb.pocket.serverv2.thread.callback.IServerSelectorWriteCallback;
import com.hb.utils.config.ServerConfig;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hb on 12/07/2018.
 */
public class Server implements Runnable{

    private static String TAG = Server.class.getSimpleName();

    private Thread thread;

    private volatile static Server instance = null;

    private SelectorProvider selectorProvider = null;

    private Selector selector = null;

    private ServerSocketChannel serverSocketChannel = null;

    private Map<SocketChannel, SocketChannel> socketChannelMap;

    private SelectionKey selectionKey;

    private boolean isShutDown = false;

    ThreadPoolExecutor threadReadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 10, Integer.MAX_VALUE, 5,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    ThreadPoolExecutor threadWritePoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 10, Integer.MAX_VALUE, 5,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    private Server() {
    }

    /**
     * Get the server instance.
     * @return
     */
    public synchronized static Server getInstance() {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    try {
                        instance = new Server();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Init the vars.
     * @return
     */
    public boolean init() {
        selectorProvider = SelectorProvider.provider();
        socketChannelMap = new ConcurrentHashMap<>();
        try {
            selector = selectorProvider.openSelector();
            serverSocketChannel = selectorProvider.openServerSocketChannel();
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void startServer() throws IOException {
        try {
            selectionKey = serverSocketChannel.register(selector,0,null);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        if (ServerConfig.autoGetIp) {
            String address = InetAddress.getLocalHost().getHostAddress().toString();
            if (address != null && !"".equals(address)) {
                ServerConfig.ip = address;

                if (serverSocketChannel.bind(new InetSocketAddress(address,ServerConfig.port),Integer.MAX_VALUE).socket().isBound()) { // Bind success.
                    selectionKey.interestOps(SelectionKey.OP_ACCEPT); // Listen the connection request.
                }

                MyLog.i(TAG, "Server sarted Ip in " + ServerConfig.ip);
                MyLog.i(TAG, "Server sarted port in " + ServerConfig.port);
            } else {
                MyLog.e(TAG, "Can not get the ip address,could not start the server.");
            }
        } else {
//            if (serverSocketChannel.bind(new InetSocketAddress(ServerConfig.)))
        }
        startLoop();
    }

    /**
     * Close the server, close the listener.
     * @throws IOException
     */
    public void shutDownServer() throws IOException {
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
            serverSocketChannel = null;
        }
        if (selector != null) {
            selector.close();
            selector = null;
        }
        if (threadReadPoolExecutor != null) {
            threadReadPoolExecutor.shutdown();
        }
        if (threadWritePoolExecutor != null) {
            threadWritePoolExecutor.shutdown();
        }
    }

    @Override
    public void run() {
        try {
            while (!isShutDown) {
                // Start listen to the events.
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    selectionKey = it.next();
                    it.remove();
                    if (selectionKey.isValid() && selectionKey.isAcceptable()) { // The connection reach.
                        MyLog.i(TAG, "Accept start...");
                        accept();
                    } else {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        if (selectionKey.isValid() && selectionKey.isWritable()) {  // If can write.
//                            MyLog.i(TAG, "Write start...");
                            threadWritePoolExecutor.execute(new ServerSelectorWriteTask(channel, "" + "\n", new IServerSelectorWriteCallback() {
                                @Override
                                public void onStartWrite() {
                                    MyLog.i(TAG, "Write start...");
                                }

                                @Override
                                public void onEndWrite(boolean isSuccess) {
                                    MyLog.i(TAG, "Write end...");
                                }
                            }));
                            // Cancel the write model, otherwise , the selector notice the write is already reaptly.
//                            selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
                        }
                        //
                        if (selectionKey.isValid() && selectionKey.isReadable()) {
//                            MyLog.i(TAG, "Read start...");
                            threadReadPoolExecutor.execute(new ServerSelectorReadTask(channel, new IServerSelectorReadCallback() {
                                @Override
                                public void onStartRead() {

                                }

                                @Override
                                public void onEndRead(String data, int length) {
                                    if (length > 0) {
//                                            MyLog.i(TAG, "Write start...");
                                            threadWritePoolExecutor.execute(new ServerSelectorWriteTask(channel, data + "\n", new IServerSelectorWriteCallback() {
                                                @Override
                                                public void onStartWrite() {
                                                    MyLog.i(TAG, "Read start...");
                                                }

                                                @Override
                                                public void onEndWrite(boolean isSuccess) {
                                                    MyLog.i(TAG, "Read end...");
                                                }
                                            }));
                                    } else if (length < 0) { // The client is closed.
                                        socketChannelMap.remove(channel);
                                        try {
                                            channel.close();  // Close the channel(the key is invalid.)
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        MyLog.i(TAG, "Close...");
                                    }
                                }
                            }));
                        }
                    }
                }
            }
            isShutDown = true;
        }catch (Exception e) {
        } finally {
            try {
                shutDownServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the server thread.
     * @throws IOException
     */
    private void startLoop() throws IOException {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Process the connection request.
     * @throws IOException
     */
    private void accept() throws IOException {
        SocketChannel channel = null;
        try {
            channel = serverSocketChannel.accept(); // Accept the connection
            channel.configureBlocking(false); // Not block model.
            channel.register(selector, SelectionKey.OP_READ, null); // Listener read.
            socketChannelMap.put(channel, channel);
        } catch (IOException e) {
            if (channel != null) {
                socketChannelMap.remove(channel);
                channel.close();
            }
        }
    }

    /**
     * Get the client number.
     * @return
     */
    public int getClientCount() {
        if (socketChannelMap != null) {
            return socketChannelMap.size();
        } else {
            return 0;
        }
    }

    public void clearAllClients() {
        if (socketChannelMap != null) {
            for (ConcurrentHashMap.Entry<SocketChannel, SocketChannel> entry : socketChannelMap.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socketChannelMap.clear();
        }
    }
    /**
     * Send a message to the all clients.
     * @param msg
     */
    public void sendBroadMessage(String msg) {
        if (socketChannelMap != null) {
            for (ConcurrentHashMap.Entry<SocketChannel, SocketChannel> entry : socketChannelMap.entrySet()) {
                threadWritePoolExecutor.execute(new ServerSelectorWriteTask(entry.getValue(), msg + "\n", new IServerSelectorWriteCallback() {
                    @Override
                    public void onStartWrite() {

                    }

                    @Override
                    public void onEndWrite(boolean isSuccess) {

                    }
                }));
            }
        }
    }
    public Selector getSelector() {
        return selector;
    }
}
