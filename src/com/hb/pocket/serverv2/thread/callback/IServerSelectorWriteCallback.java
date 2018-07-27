package com.hb.pocket.serverv2.thread.callback;

/**
 * Created by hb on 27/07/2018.
 */
public interface IServerSelectorWriteCallback {
    public void onStartWrite();

    public void onEndWrite(boolean isSuccess);

}
