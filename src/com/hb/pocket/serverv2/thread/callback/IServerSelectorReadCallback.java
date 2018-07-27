package com.hb.pocket.serverv2.thread.callback;

/**
 * Created by hb on 27/07/2018.
 */
public interface IServerSelectorReadCallback {
    public void onStartRead();

    public void onEndRead(String data, int length);
}
