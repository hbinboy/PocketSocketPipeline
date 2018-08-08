package com.hb.pocket.serverv2.thread;

import com.hb.pocket.serverv2.thread.callback.IServerSelectorWriteCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by hb on 16/07/2018.
 */
public class ServerSelectorWriteTask implements Runnable {

    /**
     * Save the data.
     */
    private SocketChannel socketChannel;

    private String data;

    private IServerSelectorWriteCallback iServerSelectorWriteCallback;

    public ServerSelectorWriteTask(SocketChannel socketChannel, String data, IServerSelectorWriteCallback iServerSelectorWriteCallback) {
        this.socketChannel = socketChannel;
        this.data = data;
        this.iServerSelectorWriteCallback = iServerSelectorWriteCallback;
    }

    @Override
    public void run() {
        try {
            if (socketChannel.isConnected() && socketChannel.isOpen()) {
                if (iServerSelectorWriteCallback != null) {
                    iServerSelectorWriteCallback.onStartWrite();
                }
                if (write(socketChannel, data)) {
                    iServerSelectorWriteCallback.onEndWrite(true);
                } else {
                    iServerSelectorWriteCallback.onEndWrite(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the write.
     * @param channel
     * @param msg
     * @throws IOException
     */
    private boolean write(SocketChannel channel, String msg) throws IOException {
        try {
            String[] tmpArray = msg.split("\n");
            ByteBuffer[] bufferArray = new ByteBuffer[tmpArray.length];
            for (int i = 0; i < tmpArray.length; i++) {
                byte[] bytes = (tmpArray[i] + "\n").getBytes(Charset.forName("UTF-8"));
                ByteBuffer buffer = ByteBuffer.allocate(bytes.length); // Alloc heap buffer.
                buffer.put(bytes);
                buffer.flip();// Switch the read model.
                bufferArray[i] = buffer;
            }
            channel.write(bufferArray, 0, bufferArray.length);
            return true;
        } catch (IOException e) {
            if (channel != null) {
                channel.close();
            }
        }
        return false;
    }
}
