package com.hb.pocket.server.thread;

import com.hb.pocket.server.io.MyBufferedReader;
import com.hb.pocket.server.manager.IServerThreadManagerListener;
import com.hb.utils.log.MyLog;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by hb on 04/07/2018.
 */
public class ServerReadThread extends ServerThreadParent {

    private static String TAG = ServerReadThread.class.getSimpleName();

    /**
     * Read data form client socket.
     */
    private InputStream inputStream = null;

    /**
     * Write data to client socket.
     */
    private InputStreamReader inputStreamReader = null;

    /**
     * Reader.
     */
    private MyBufferedReader myBufferedReader = null;

    protected boolean shutDown = false;

    private IServerThreadManagerListener iServerThreadManagerListener;

    /**
     * The construction.
     * @param socket
     * @param iServerThreadManagerListener
     */
    public ServerReadThread(Socket socket, IServerThreadManagerListener iServerThreadManagerListener) {
        super(socket);
        this.iServerThreadManagerListener = iServerThreadManagerListener;
    }

    /**
     * Close current thread.
     */
    @Override
    public void setShutDown() {
        this.shutDown = true;
    }

    @Override
    public void run() {
        String info = null;
        while (!shutDown) {
            MyLog.i(TAG, "ServerReadThread enter.");
            // If client socket is closed. The server socket client to close.
            if (isServerClose(socket)) {
                if (iServerThreadManagerListener != null) {
                    iServerThreadManagerListener.shutDown();
                }
                shutDown = true;

                MyLog.i(TAG, "Client is close");
            }
            // Init the inputstream objet.
            if (inputStream == null) {
                try {
                    inputStream = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Init the inputStreamReader object.
            if (inputStreamReader == null && inputStream != null) {
                inputStreamReader = new InputStreamReader(inputStream);
            }
            // Init the myBufferedReader object.
            if (myBufferedReader == null && inputStreamReader != null) {
                myBufferedReader = new MyBufferedReader(inputStreamReader);
            }
            // Read the data from client socket, if empty, wait.
            try {
                info = myBufferedReader.readLine1(true);
                iServerThreadManagerListener.onRead(info);
                while(info !=null){
                    MyLog.i(TAG, info);
                    info = myBufferedReader.readLine1(true);
                    iServerThreadManagerListener.onRead(info);
                }
                shutDown = true;
            } catch (IOException e) {
                e.printStackTrace();
                shutDown = true;
            } catch (Exception e) {
                e.printStackTrace();
                shutDown = true;
            }
        }
        MyLog.i(TAG, "ServerReadThread exit.");
    }
}
