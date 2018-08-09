package com.hb.pocket.serverv2.commandline.command;

import com.hb.pocket.parser.Getopt;
import com.hb.pocket.parser.LongOpt;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by hb on 31/07/2018.
 */
public abstract class Command {

    protected String commandName;

    protected List<LongOpt> longOptList;

    protected String commandLine;

    protected Getopt getopt;

    protected String optString;

    public Command(String commandName, List<LongOpt> longOptList, String commandLine, String optString) {
        this.commandName = commandName;
        this.longOptList = longOptList;
        this.commandLine = commandLine;
        this.optString = optString;
        final LongOpt[] optArray = longOptList.toArray(new LongOpt[longOptList.size()]);
        getopt = new Getopt(commandName, string2Array(commandLine), optString, optArray);
//        getopt.setOpterr(false);
    }

    public abstract boolean excute();

    private String[] string2Array(String commandLine) {
        if (commandLine == null || commandLine.trim().equals("")) {
            return null;
        }
        String[] tmpResult = commandLine.trim().split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 1; i < tmpResult.length; i++) {
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
}
