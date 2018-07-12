package com.hb.utils.log;

import com.hb.utils.config.LogConfig;

/**
 * Log file.
 * Created by hb on 04/07/2018.
 */
public class MyLog {
    public static int v(String tag, String msg) {
        if (LogConfig.isVerbose) {
            System.out.println(tag + ": " + msg);
        }
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (LogConfig.isVerbose) {
            System.out.println(tag + ": " + msg);
            tr.printStackTrace();
        }
        return 0;
    }

    public static int d(String tag, String msg) {
        if (LogConfig.isDebug) {
            System.out.println(tag + ": " + msg);
        }
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (LogConfig.isDebug) {
            System.out.println(tag + ": " + msg);
            tr.printStackTrace();
        }
        return 0;
    }

    public static int i(String tag, String msg) {
        if (LogConfig.isInfo) {
            System.out.println(tag + ": " + msg);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (LogConfig.isDebug) {
            System.out.println(tag + ": " + msg);
            tr.printStackTrace();
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (LogConfig.isWarning) {
            System.out.println(tag + ": " + msg);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (LogConfig.isWarning) {
            System.out.println(tag + ": " + msg);
            tr.printStackTrace();
        }
        return 0;
    }

    public static boolean isLoggable(String var0, int var1) {
        System.out.println(var0 + ": " + var1);
        return true;
    }

    public static int w(String tag, Throwable tr) {
        if (LogConfig.isWarning) {
            System.out.println(tag);
            tr.printStackTrace();
        }
        return 0;
    }

    public static int e(String tag, String msg) {
        if (LogConfig.isError) {
            System.out.println(tag + ": " + msg);
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (LogConfig.isError) {
            System.out.println(tag + ": " + msg);
            tr.printStackTrace();
        }
        return 0;
    }

    public static int wtf(String tag, String msg) {
        if (LogConfig.isWtf) {
            System.out.println(tag + ": " + msg);
        }
        return 0;
    }

    public static int wtf(String tag, Throwable tr) {
        if (LogConfig.isWtf) {
            System.out.println(tag);
            tr.printStackTrace();
        }
        return 0;
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        if (LogConfig.isWtf) {
            System.out.println(tag + ": " + msg);
            tr.printStackTrace();
        }
        return 0;
    }

    public static String getStackTraceString(Throwable tr) {
        tr.printStackTrace();
        return tr.toString();
    }

    public static int println(int priority, String tag, String msg) {
        System.out.println(priority + " " + tag + ": " + msg);
        return priority;
    }
}
