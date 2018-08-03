package com.hb.pocket.commandline;

import com.hb.pocket.commandline.command.BroadMessageCommand;
import com.hb.pocket.commandline.parser.LongOpt;
import com.hb.pocket.serverv2.Server;
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

    private static String TAG = Server.class.getSimpleName();

    private com.hb.pocket.serverv2.Server server;

    private Map<String, List<LongOpt>> mainCommandMap = new HashMap<>();

/*    private List<LongOpt> mainLongOptList = null;

    private List<LongOpt> subLongOptList = null;

    private StringBuffer flag;*/

    public CommandLine(com.hb.pocket.serverv2.Server server) {
        this.server = server;

        /*mainLongOptList = new ArrayList<>();
        mainLongOptList.add(new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'));
        mainLongOptList.add(new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v'));
        mainLongOptList.add(new LongOpt("exit", LongOpt.NO_ARGUMENT, null, 'e'));
        mainLongOptList.add(new LongOpt("test", LongOpt.OPTIONAL_ARGUMENT, null, 100));*/

        initMainCommand();
    }

    private void initMainCommand() {
        mainCommandMap = new HashMap<>();
        // Exit command params, no params.
        mainCommandMap.put("exit", null);
        // ClientCount command params, no  params.
        mainCommandMap.put("clientcount", null);
        // SendBroadMessage command parama, require a string.
        List<LongOpt> sendBroadMessageList = new ArrayList<>();
        sendBroadMessageList.add(new LongOpt("message", LongOpt.REQUIRED_ARGUMENT, 'M',"Send a message to all the clients."));
        mainCommandMap.put("broadmessage", sendBroadMessageList);
        // Clear all clients.
        mainCommandMap.put("clearclient", null);
        mainCommandMap.put("help", null);
        mainCommandMap.put("version", null);
    }

    public String[] string2Array(String commandLine) {
        if (commandLine == null || commandLine.trim().equals("")) {
            return null;
        }
        String[] tmp = commandLine.trim().split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != null && !tmp[i].trim().equals("")) {
                list.add(tmp[i].trim());
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
        }
        if (args[0].toLowerCase().startsWith("Exit".toLowerCase())) {
            try {
                server.shutDownServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (args[0].toLowerCase().startsWith("ClientCount".toLowerCase())) {
            MyLog.i(TAG, "" + server.getClientCount());
        }
        if (args[0].toLowerCase().startsWith("ClearClient".toLowerCase())) {
            MyLog.i(TAG, "Close and remove all the clients.");
        }
        if (args[0].toLowerCase().startsWith("version".toLowerCase())) {
            MyLog.i(TAG, "2.0.0 version.");
        }
        return false;
    }
}
