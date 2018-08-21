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

    /**
     * Receive the data with Header {@link com.hb.pocket.data.header.Header}
     */
    public static boolean readDataWithHeader = true;

    /**
     * Send the data with Header {@link com.hb.pocket.data.header.Header}
     */
    public static boolean writeDataWithHeader = true;

    /**
     * Init the server configuration by the Config.xml
     */
    public static void initByXML() {
        try {
            XMLConfig.getInstance().initConfig("./Config.xml");
            ServerConfig.autoGetIp = XMLConfig.getInstance().getConfigValue("autoGetIp",true);
            ServerConfig.ip = XMLConfig.getInstance().getConfigValue("ip","");
            if (ip != null && !ip.equals("")) {
                String[] ipStr = ip.split("\\.");
                byte[] tmp = new byte[ipStr.length];
                for (int i = 0; i < ipStr.length; i++) {
                    tmp[i] = (byte)Integer.parseInt(ipStr[i]);
                }
                ServerConfig.IpByte = tmp;
            }

            ServerConfig.port = XMLConfig.getInstance().getConfigValue("port",7909);
            ServerConfig.backLog = XMLConfig.getInstance().getConfigValue("backLog", Integer.MAX_VALUE);

            ServerConfig.readCorePoolSize = XMLConfig.getInstance().getConfigValue("readCorePoolSize", Runtime.getRuntime().availableProcessors() + 10);
            ServerConfig.readMaximumPoolSize = XMLConfig.getInstance().getConfigValue("readMaximumPoolSize", Integer.MAX_VALUE);
            ServerConfig.readKeepAliveTime = XMLConfig.getInstance().getConfigValue("readKeepAliveTime", 300);

            ServerConfig.writeCorePoolSize = XMLConfig.getInstance().getConfigValue("writeCorePoolSize", Runtime.getRuntime().availableProcessors() + 10);
            ServerConfig.writeMaximumPoolSize = XMLConfig.getInstance().getConfigValue("writeMaximumPoolSize", Integer.MAX_VALUE);
            ServerConfig.writeKeepAliveTime = XMLConfig.getInstance().getConfigValue("writeKeepAliveTime", 300);

            ServerConfig.readDataWithHeader = XMLConfig.getInstance().getConfigValue("readDataWithHeader", true);
            ServerConfig.writeDataWithHeader = XMLConfig.getInstance().getConfigValue("writeDataWithHeader", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
