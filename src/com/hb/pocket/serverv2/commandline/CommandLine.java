package com.hb.pocket.serverv2.commandline;

import com.hb.pocket.serverv2.commandline.command.BroadMessageCommand;
import com.hb.pocket.parser.LongOpt;
import com.hb.utils.log.MyLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hb on 30/07/2018.
 */
public class CommandLine {

    private static String TAG = CommandLine.class.getSimpleName();

    private com.hb.pocket.serverv2.Server server;

    private Map<String, List<LongOpt>> mainCommandMap = new HashMap<>();

    public CommandLine(com.hb.pocket.serverv2.Server server) {
        this.server = server;
        initMainCommand();
    }

    private void initMainCommand() {
        mainCommandMap = new HashMap<>();
        // Exit command params, no params.
        mainCommandMap.put("Exit".toLowerCase(), null);
        // ClientCount command params, no  params.
        mainCommandMap.put("ClientCount".toLowerCase(), null);
        // SendBroadMessage command parama, require a string.
        List<LongOpt> sendBroadMessageList = new ArrayList<>();
        sendBroadMessageList.add(new LongOpt("message", LongOpt.REQUIRED_ARGUMENT, 'M',"Send a message to all the clients."));
        mainCommandMap.put("BroadMessage".toLowerCase(), sendBroadMessageList);
        // Clear all clients.
        mainCommandMap.put("ClearClient".toLowerCase(), null);
        mainCommandMap.put("Help".toLowerCase(), null);
        mainCommandMap.put("Version".toLowerCase(), null);
        mainCommandMap.put("Selector".toLowerCase(), null);
    }

    private String[] string2Array(String commandLine) {
        if (commandLine == null || commandLine.trim().equals("")) {
            return null;
        }
        String[] tmpResult = commandLine.trim().split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tmpResult.length; i++) {
            if (tmpResult[i] != null && !tmpResult[i].trim().equals("")) {
                if (tmpResult[i].startsWith("-") || tmpResult[i].startsWith("--")) {
                    String[] tmp = tmpResult[i].split("=");
                    if (tmp != null) {
                        for (int j = 0; j < tmp.length; j++) {
                            list.add(tmp[j]);
                        }
                    }
                } else {
                    list.add(tmpResult[i].trim());
                }
            }
        }
        String[] result = list.toArray(new String[list.size()]);
        return result;
    }

    public boolean excute(String commandLine) {
        String[] args = string2Array(commandLine);
        if (args == null || args.length == 0) {
            return false;
        }
        if (args[0].toLowerCase().startsWith("BroadMessage".toLowerCase())) {
            BroadMessageCommand broadMessageCommand = new BroadMessageCommand("BroadMessage", mainCommandMap.get(args[0].toLowerCase()),commandLine, "-:M", server);
            broadMessageCommand.excute();
        } else if (args[0].toLowerCase().startsWith("Exit".toLowerCase())) {
            try {
                server.shutDownServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else if (args[0].toLowerCase().startsWith("ClientCount".toLowerCase())) {
            MyLog.i(TAG, "" + server.getClientCount());
        } else if (args[0].toLowerCase().startsWith("ClearClient".toLowerCase())) {
            server.clearAllClients();
            MyLog.i(TAG, "Close and remove all the clients.");
        } else if (args[0].toLowerCase().startsWith("version".toLowerCase())) {
            MyLog.i(TAG, "2.0.0 version.");
        } else if (args[0].toLowerCase().startsWith("Selector".toLowerCase())) {
            server.getSelector();
        }
        return false;
    }
}
