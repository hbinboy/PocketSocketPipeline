package com.hb.utils.config;

/**
 * Created by hb on 10/07/2018.
 */
public class ServerConfig {

    /**
     * Auto get the server ip.
     */
    public static final boolean autoGetIp = true;

    /**
     * If auto get the server ip, save it.
     */
    public static String ip = "";
    /**
     * The server Ip.
     */
    public static final byte[] IpByte = new byte[] {(byte)10,(byte)250,(byte)11,(byte)43};

    /**
     * The server listening port.
     */
    public static final int port = 7909;

    /**
     * The socket accept backlog count.
     */
    public static final int backLog = 1000;

}
