package com.hb.pocket.serverv2.commandline.command;

import com.hb.pocket.parser.LongOpt;
import com.hb.pocket.serverv2.Server;
import com.hb.utils.log.MyLog;

import java.util.List;

/**
 * Created by hb on 31/07/2018.
 */
public class BroadMessageCommand extends Command{

    private static String TAG = BroadMessageCommand.class.getSimpleName();

    private Server server;

    public BroadMessageCommand(String commandName, List<LongOpt> longOptList, String commandLine, String optString, Server server) {
        super(commandName, longOptList, commandLine, optString);
        this.server = server;
    }

    @Override
    public boolean excute() {
        String str = "";
        int ch;
        while ((ch = getopt.getopt()) != -1) {
            switch (ch) {
                case 'M' :
                    str = getopt.getOptarg();
                    if (str != null && !str.equals("")) {
                        server.sendBroadMessage(str);
                    }
                    break;
                case ':':
                    MyLog.i(TAG, "Need a paramer.");
                    return false;
                case '?':
                    MyLog.i(TAG,"Please help.");
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }
}
