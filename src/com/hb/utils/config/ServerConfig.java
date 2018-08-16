package com.hb.utils.config;

/**
 * Created by hb on 10/07/2018.
 */
public class ServerConfig {

    /**
     * Auto get the server ip.
     */
    public static boolean autoGetIp = true;

    /**
     * If auto get the server ip, save it.
     */
    public static String ip = "";
    /**
     * The server ip.
     */
    public static byte[] IpByte = new byte[] {(byte)10,(byte)250,(byte)11,(byte)43};

    /**
     * The server listening port.
     */
    public static int port = 7909;

    /**
     * The socket accept backlog count.
     */
    public static int backLog = Integer.MAX_VALUE;

    /**
     * The number of threads to keep in the pool
     */
    public static int readCorePoolSize = 20;

    /**
     * The maximum number of threads to allow in the pool
     */
    public static int readMaximumPoolSize = Integer.MAX_VALUE;

    /**
     * When the number of threads is greater than
     * the core, this is the maximum time that excess idle threads
     * will wait for new tasks before terminating.
     */
    public static int readKeepAliveTime = 300;

    /**
     * The number of threads to keep in the pool.
     */
    public static int writeCorePoolSize = 20;

    /**
     * The maximum number of threads to allow in the pool
     */
    public static int writeMaximumPoolSize = Integer.MAX_VALUE;

    /**
     * When the number of threads is greater than
     * the core, this is the maximum time that excess idle threads
     * will wait for new tasks before terminating.
     */
    public static int writeKeepAliveTime = 300;


}
