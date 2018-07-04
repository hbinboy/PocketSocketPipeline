package com.hb.pocket.server.thread;

import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hb on 04/07/2018.
 */
public class ServerWriteThread extends ServerThreadParent {

    private static String TAG = ServerReadThread.class.getSimpleName();

    private boolean shutDown = false;

    private OutputStream outputStream = null;

    private PrintWriter printWriter = null;

    /**
     * Server current order.
     */
    private volatile ServerThreadStatus order;

    /**
     * Object lock.
     */
    protected AtomicBoolean object = new AtomicBoolean(false);

    /**
     * The construction.
     *
     * @param socket
     */
    public ServerWriteThread(Socket socket) {
        super(socket);
    }

    /**
     * Close current thread.
     */
    @Override
    public void setShutDown() {
        synchronized (object) {
            this.shutDown = true;
            object.notify();
        }
    }

    /**
     * Send a message to the client.
     * @param msg
     * @return
     */
    public boolean sendMsg(String msg) {
        if (outputStream == null) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (printWriter == null) {
            if (outputStream != null) {
                printWriter = new PrintWriter(outputStream);
                printWriter.write(msg);
                printWriter.flush();

                return true;
            }
        } else {
            printWriter.write(msg);
            printWriter.flush();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while (!shutDown) {
            MyLog.i(TAG, "ServerWriteThread enter.");
            synchronized (object) {
                if (order == ServerThreadStatus.SENDMSG) {
                    sendMsg("Received\nmessage");
                    MyLog.i(TAG, "Send");
                    order = ServerThreadStatus.IDEL;
                }
                if (order == ServerThreadStatus.CLOSE) {
                    order = ServerThreadStatus.IDEL;
                    break;
                }
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            MyLog.i(TAG, "ServerWriteThread exit.");
        }
    }
}
