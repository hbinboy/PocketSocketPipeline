package com.hb.pocket.server.manager;

import com.hb.pocket.server.thread.IServerThreadListener;
import com.hb.pocket.server.thread.ServerReadThread;
import com.hb.pocket.server.thread.ServerWriteThread;

import java.net.Socket;

/**
 * The socket server manager.
 * Created by hb on 04/07/2018.
 */
public class ServerThreadManager {

    /**
     * The read thread.
     */
    private ServerReadThread serverReadThread = null;

    /**
     * The write thread.
     */
    private ServerWriteThread serverWriteThread = null;

    /**
     * When the client socket is closed then close the server's client socket.
     */
    private IServerThreadListener iServerThreadListener = null;

    /**
     * Every client socket has a ServerThreadManager.
     * @param socket
     * @param iServerThreadListener
     */
    public ServerThreadManager(Socket socket, IServerThreadListener iServerThreadListener ) {
        this.iServerThreadListener = iServerThreadListener;
        serverReadThread = new ServerReadThread(socket, iServerThreadManagerListener);
        serverWriteThread = new ServerWriteThread(socket);
    }

    /**
     * If the client socket is closed, so , close the server's client socket.
     */
    private IServerThreadManagerListener iServerThreadManagerListener = new IServerThreadManagerListener() {
        @Override
        public void shutDown() {
            serverWriteThread.setShutDown();
            if (iServerThreadListener != null) {
                iServerThreadListener.clientSocketCloseRemove(true,ServerThreadManager.this);
            }
        }

        @Override
        public void onRead(String msg) {
            if (msg != null && !msg.equals("")) {
                serverWriteThread.sendMessage("Server receive your msg!");
            }
        }
    };

    /**
     * Start the socket's read and write thread.
     */
    public void start() {
        serverReadThread.start();
        serverWriteThread.start();
    }

    /**
     * When the server is closed. Close the read and write threads.(This function just be called when the server is closed.)
     */
    public void shutDownServerThreadManager() {
        serverReadThread.setShutDown();
        serverWriteThread.setShutDown();
        // By the read thread call the parent to close the socket.
        serverReadThread.shutDownParentSocket();
        serverReadThread = null;
        serverWriteThread = null;
        if (iServerThreadListener != null) {
            iServerThreadListener.clientSocketCloseRemove(true,ServerThreadManager.this);
        }

    }

}
