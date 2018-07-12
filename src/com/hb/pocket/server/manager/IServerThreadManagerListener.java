package com.hb.pocket.server.manager;

/**
 * Created by hb on 04/07/2018.
 */
public interface IServerThreadManagerListener {

    public void shutDown();

    public void onRead(String msg);
}
