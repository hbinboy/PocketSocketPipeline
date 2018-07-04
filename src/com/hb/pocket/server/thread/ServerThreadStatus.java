package com.hb.pocket.server.thread;

/**
 * Created by hb on 04/07/2018.
 */
public enum ServerThreadStatus {


    IDEL(0, "IDEL"),

    CONNECT(1, "CONNECT"),

    SENDMSG(2, "SENDMSG"),

    CLOSE(3, "CLOSE");


    private int code;
    private String name;

    private ServerThreadStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public int getCode() {
        return code;
    }

}
