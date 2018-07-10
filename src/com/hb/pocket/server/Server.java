package com.hb.pocket.server;

import com.hb.pocket.server.manager.ServerThreadManager;
import com.hb.pocket.server.thread.IServerThreadListener;
import com.hb.utils.config.ServerConfig;
import com.hb.utils.log.MyLog;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hb on 04/07/2018.
 */
public class Server implements Runnable {

    private static String TAG = Server.class.getSimpleName();

    private volatile static Server instance = null;

    private ConcurrentHashMap<ServerThreadManager, ServerThreadManager> socketMap = null;

    private boolean shutDown = false;

    private ServerSocket serverSocket = null;

    private Thread listenerThread = null;

    private Server() {
        socketMap = new ConcurrentHashMap<>();
        try {
            if (ServerConfig.autoGetIp) {
                String address = InetAddress.getLocalHost().getHostAddress().toString();
                if (address != null && !"".equals(address)) {
                    ServerConfig.ip = address;
                    String[] addressTmp = address.split("\\.");
                    byte[] ipByte = new byte[addressTmp.length];
                    for (int i = 0; i < ipByte.length; i++) {
                        ipByte[i] = Integer.valueOf(addressTmp[i]).byteValue();
                    }
                    InetAddress inetAddress = InetAddress.getByAddress(ipByte);
                    serverSocket = new ServerSocket(ServerConfig.port, ServerConfig.backLog, inetAddress);
                } else {
                    MyLog.e(TAG, "Can not get the ip address,could not start the server.");
                }
            } else {
                InetAddress inetAddress = InetAddress.getByAddress(ServerConfig.IpByte);
                serverSocket = new ServerSocket(ServerConfig.port, ServerConfig.backLog, inetAddress);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the server instance.
     * @return
     */
    public synchronized static Server getInstance() {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    instance = new Server();
                }
            }
        }
        return instance;
    }

    /**
     * Close the server, close listener, remove all client socket and read, write threads.
     */
    public void shutDownServer() {
        shutDown = true;
        try {
            // Close the serverSocket and close listen. Exit the thread.
            serverSocket.close();
            // Close all client socket.
            if (socketMap != null && socketMap.size() > 0) {
                for (ConcurrentHashMap.Entry<ServerThreadManager, ServerThreadManager> entry: socketMap.entrySet()
                        ) {
                    entry.getKey().shutDownServerThreadManager();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = null;
        MyLog.i(TAG, "Set shut down.");
    }

    /**
     * Start server.
     */
    public void startServer() {
        if (listenerThread == null) {
            MyLog.i(TAG, "Listen thread is called.");
            listenerThread = new Thread(this);
            // Start the listen thread.
            listenerThread.start();
            MyLog.i(TAG, "Server sarted Ip in " + ServerConfig.ip);
            MyLog.i(TAG, "Server sarted pot in " + ServerConfig.port);
        }
    }

    @Override
    public void run() {
        MyLog.i(TAG, "Server thread run is started.");
        while (!shutDown) {
            MyLog.i(TAG, "Server thread enter while loop.");
            try {
                MyLog.i(TAG, "Server thread start listen.");
                // Listen the client, if some connect then accept.
                ServerThreadManager sThread = new ServerThreadManager(serverSocket.accept(), new IServerThreadListener() {
                    @Override
                    public void clientSocketCloseRemove(boolean isClose, ServerThreadManager serverThread) {
                        if (socketMap != null && socketMap.size() > 0) {
                            socketMap.remove(serverThread);
                            MyLog.i(TAG, "Client is close");
                        }
                    }
                });
                // When client connect the server then create a new socket and start read and write threads.
                sThread.start();
                socketMap.put(sThread,sThread);
                MyLog.i(TAG, "Server thread accept new socket.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MyLog.i(TAG, "Server thread exit while loop");
    }

    // ============================================= Server utils function =============================================

    /**
     * Get the client sockets numbers.
     * @return
     */
    public long getClientCount() {
        if (socketMap == null) {
            return 0;
        }
        return socketMap.size();
    }

    public List<String> getAllList() {
        List<String> res = new ArrayList<>();
        if (socketMap == null) {
            return res;
        }
        for (ConcurrentHashMap.Entry<ServerThreadManager, ServerThreadManager> entry : socketMap.entrySet()) {
            res.add(entry.getKey().toString());
        }
        return res;
    }
}
