package com.hb.pocket.serverv2.thread;

import com.hb.pocket.serverv2.thread.callback.IServerSelectorReadCallback;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by hb on 16/07/2018.
 */
public class ServerSelectorReadTask implements Runnable {

    private static String TAG = ServerSelectorReadTask.class.getSimpleName();

    /**
     * Save the data.
     */
    private SocketChannel socketChannel;

    private IServerSelectorReadCallback iServerSelectorReadCallback;

    /**
     * Save the received data.
     */
    private ByteBuffer data = ByteBuffer.allocate(1024);

    public ServerSelectorReadTask(SocketChannel socketChannel, IServerSelectorReadCallback iServerSelectorReadCallback) {
        this.socketChannel = socketChannel;
        this.iServerSelectorReadCallback = iServerSelectorReadCallback;
    }
    @Override
    public void run() {
        try {
            if (socketChannel.isConnected() && socketChannel.isOpen()) {
                if (iServerSelectorReadCallback != null) {
                    iServerSelectorReadCallback.onStartRead();
                }
                read(socketChannel, iServerSelectorReadCallback);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the read.
     * @param channel
     * @return
     * @throws IOException
     */
    private static int read(SocketChannel channel,IServerSelectorReadCallback iServerSelectorReadCallback) throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024); // Alloc heapByteBuffer
            int len = channel.read(buffer); // Unitil no data or the buffer is full.
            if (len > 0) {
                MyLog.i(TAG, new String(buffer.array(), 0, len, Charset.forName("UTF-8"))); // buffer.array()：get the HeapByteFuffer raw data.
            }
            if (iServerSelectorReadCallback != null) {
                iServerSelectorReadCallback.onEndRead(new String(buffer.array(), 0, len, Charset.forName("UTF-8")), len);
            }
            return len;
        } catch (IOException e) {
            if (channel != null) {
                channel.close();
            }
            if (iServerSelectorReadCallback != null) {
                iServerSelectorReadCallback.onEndRead(null, -1);
            }
            return -1;
        }
    }
}
