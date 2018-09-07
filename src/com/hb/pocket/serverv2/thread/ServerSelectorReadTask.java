package com.hb.pocket.serverv2.thread;

import com.hb.pocket.data.DataManager;
import com.hb.pocket.serverv2.thread.callback.IServerSelectorReadCallback;
import com.hb.utils.config.ServerConfig;
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
    private int read(SocketChannel channel,IServerSelectorReadCallback iServerSelectorReadCallback) throws IOException {
        if (ServerConfig.readDataWithHeader) {
            return readDataWithHeader(channel, iServerSelectorReadCallback);
        } else {
            return readRawData(channel, iServerSelectorReadCallback);
        }
    }

    /**
     * Process the read message without Header {@link com.hb.pocket.data.header.Header}
     * @param channel
     * @param iServerSelectorReadCallback
     * @return
     * @throws IOException
     */
    private int readRawData(SocketChannel channel, IServerSelectorReadCallback iServerSelectorReadCallback) throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024); // Alloc heapByteBuffer
            int len = channel.read(buffer); // Unitil no data or the buffer is full.
            if (len > 0) {
                MyLog.d(TAG, new String(buffer.array(), 0, len, Charset.forName("UTF-8"))); // buffer.array()：get the HeapByteFuffer raw data.
            }
            if (iServerSelectorReadCallback != null && len > 0) {
                iServerSelectorReadCallback.onEndRead(new String(buffer.array(), 0, len, Charset.forName("UTF-8")), len);
            } else {
                iServerSelectorReadCallback.onEndRead(null,len);
            }
            return len;
        } catch (IOException e) {
            if (iServerSelectorReadCallback != null) {
                iServerSelectorReadCallback.onEndRead(null, -1);
            }
            return -1;
        }
    }

    /**
     * Process the read message with Header {@link com.hb.pocket.data.header.Header}
     * @param channel
     * @param iServerSelectorReadCallback
     * @return
     * @throws IOException
     */
    private int readDataWithHeader(SocketChannel channel, IServerSelectorReadCallback iServerSelectorReadCallback) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] bytes = buffer.array();
        try {
            int len = channel.read(buffer);
            int remainLen = len;
            int bodyLen = 0;
            int offset = 0;
            buffer.position(0);
            while (remainLen > 0) {
                byte[] bufferBak = new byte[len - offset];
                System.arraycopy(bytes, offset, bufferBak, 0, len - offset);
                MyLog.d(TAG, "" + len);
                DataManager dataManager = new DataManager();
                if (len > 0) {
                    if (dataManager.getReceiveDataPackageData(bufferBak) != null) {
                        dataManager.getBody().getData();
                        offset += dataManager.getHeader().getHeadLen() + dataManager.getHeader().getDataLen();

                        remainLen = len - offset;
                        MyLog.i(TAG, dataManager.getBody().getData()); // buffer.array()：get the HeapByteFuffer raw data.
                    }
                }
                if (iServerSelectorReadCallback != null && len > 0) {
                    iServerSelectorReadCallback.onEndRead(dataManager.getBody().getData(), dataManager.getBody().getData().length());
                } else {
                    iServerSelectorReadCallback.onEndRead(null, bodyLen);
                }
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
