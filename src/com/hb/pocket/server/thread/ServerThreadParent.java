package com.hb.pocket.server.thread;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by hb on 04/07/2018.
 */
public abstract class ServerThreadParent extends Thread {

    private static String TAG = ServerThreadParent.class.getSimpleName();

    /**
     * The client socket.
     */
    protected Socket socket = null;

    /**
     * The construction.
     * @param socket
     */
    public ServerThreadParent(Socket socket) {
        this.socket = socket;
        try {
            this.socket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the client socket.
     */
    public void shutDownParentSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closet the current thread.
     */
    public abstract void setShutDown();

    /**
     * Test the client socket is colosed or not. Closed is true, otherwise is false.
     * @param socket
     * @return
     */
    public boolean isServerClose(Socket socket){
        try{
            // Send one byte data, the default case is not affect the server.
            socket.sendUrgentData(12);
            return false;
        }catch(Exception se){
            setShutDown();
            return true;
        }
    }
}
