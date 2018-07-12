package com.hb.pocket.server.thread;

import com.hb.pocket.server.manager.ServerThreadManager;

/**
 * Created by hb on 04/07/2018.
 */
public interface IServerThreadListener {

    /**
     * Remove the closed socket from the server's list.
     * @param isClose
     * @param serverThread
     */
    public void clientSocketCloseRemove(boolean isClose, ServerThreadManager serverThread);
}
