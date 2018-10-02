package com.hb.pocket.serverv2;

import com.hb.pocket.data.Data;
import com.hb.pocket.serverv2.thread.*;
import com.hb.pocket.serverv2.thread.callback.IServerSelectorAcceptCallback;
import com.hb.pocket.serverv2.thread.callback.IServerSelectorReadCallback;
import com.hb.pocket.serverv2.thread.callback.IServerSelectorWriteCallback;
import com.hb.utils.config.ServerConfig;
import com.hb.utils.config.XMLConfig;
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
import java.util.concurrent.*;

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

    private Map<SocketChannel, Map<String, Data>> socketChannelMap;

    private SelectionKey selectionKey;

    private boolean isShutDown = false;

    private ThreadPoolExecutor threadReadPoolExecutor = null;

    private ThreadPoolExecutor threadWritePoolExecutor = null;

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
        ServerConfig.initByXML();
        threadReadPoolExecutor = new ThreadPoolExecutor(ServerConfig.readCorePoolSize, ServerConfig.readMaximumPoolSize, ServerConfig.readKeepAliveTime,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        if (threadReadPoolExecutor == null) {
            return false;
        }
        threadWritePoolExecutor = new ThreadPoolExecutor(ServerConfig.writeCorePoolSize , ServerConfig.writeMaximumPoolSize, ServerConfig.writeKeepAliveTime,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        if (threadWritePoolExecutor == null) {
            return false;
        }
        selectorProvider = SelectorProvider.provider();
        if (selectorProvider == null) {
            return false;
        }
        socketChannelMap = new ConcurrentHashMap<>();
        if (socketChannelMap == null) {
            return false;
        }
        try {
            selector = selectorProvider.openSelector();
            if (selector == null) {
                return false;
            }
            serverSocketChannel = selectorProvider.openServerSocketChannel();
            if (serverSocketChannel == null) {
                return false;
            }
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean startServer() throws IOException {
        try {
            selectionKey = serverSocketChannel.register(selector,0,null);
            if (selectionKey == null) {
                return false;
            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
            return false;
        }

        if (ServerConfig.autoGetIp) {
            String address = InetAddress.getLocalHost().getHostAddress().toString();
            if (address != null && !"".equals(address)) {
                ServerConfig.ip = address;

                if (serverSocketChannel.bind(new InetSocketAddress(address,ServerConfig.port),ServerConfig.backLog).socket().isBound()) { // Bind success.
                    selectionKey.interestOps(SelectionKey.OP_ACCEPT); // Listen the connection request.
                }

                MyLog.i(TAG, "Server sarted Ip in " + address);
                MyLog.i(TAG, "Server sarted port in " + ServerConfig.port);
            } else {
                MyLog.e(TAG, "Can not get the ip address,could not start the server.");
                return false;
            }
        } else {
            if (serverSocketChannel.bind(new InetSocketAddress(ServerConfig.ip, ServerConfig.port),ServerConfig.backLog).socket().isBound()) { // Bind success.
                selectionKey.interestOps(SelectionKey.OP_ACCEPT); // Listen the connection request.
            } else {
                MyLog.e(TAG, "Can not get the ip address,could not start the server.");
                return false;
            }
            MyLog.i(TAG, "Server sarted Ip in " + ServerConfig.ip);
            MyLog.i(TAG, "Server sarted port in " + ServerConfig.port);
        }
        startLoop();
        return true;
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
                        accept();
                    } else {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        if (selectionKey.isValid() && selectionKey.isWritable()) {  // If can write.
                            threadWritePoolExecutor.submit(new ServerSelectorWriteTask(channel, "" + "\n", new IServerSelectorWriteCallback() {
                                @Override
                                public void onStartWrite() {
                                }

                                @Override
                                public void onEndWrite(boolean isSuccess) {
                                    MyLog.d(TAG, "Write end...");
                                }
                            }));
                        }
                        //
                        if (selectionKey.isValid() && selectionKey.isReadable()) {
                            threadReadPoolExecutor.submit(new ServerSelectorReadTask(channel, socketChannelMap.get(channel), new IServerSelectorReadCallback() {
                                @Override
                                public void onStartRead() {

                                }

                                @Override
                                public void onEndRead(String data, int length) {
                                    if (length >= 0) {
                                        MyLog.i(TAG, "The whole data display: "+ data);
                                        threadReadPoolExecutor.submit(new ServerSelectorWriteTask(channel, data + "\n", new IServerSelectorWriteCallback() {
                                            @Override
                                            public void onStartWrite() {
                                            }

                                            @Override
                                            public void onEndWrite(boolean isSuccess) {
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
        threadReadPoolExecutor.prestartAllCoreThreads();
        threadWritePoolExecutor.prestartAllCoreThreads();
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
            Map<String, Data> stringDataMap = new ConcurrentHashMap<>();
            socketChannelMap.put(channel, stringDataMap);
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
            for (ConcurrentHashMap.Entry<SocketChannel, Map<String, Data>> entry : socketChannelMap.entrySet()) {
                try {
                    entry.getKey().close();
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
            for (ConcurrentHashMap.Entry<SocketChannel, Map<String, Data>> entry : socketChannelMap.entrySet()) {
                threadWritePoolExecutor.execute(new ServerSelectorWriteTask(entry.getKey(), msg + "\n", new IServerSelectorWriteCallback() {
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
