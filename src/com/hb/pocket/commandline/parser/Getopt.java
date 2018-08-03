package com.hb.pocket.commandline.parser;

import com.hb.pocket.serverv2.Server;
import com.hb.utils.log.MyLog;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by hb on 01/08/2018.
 */
public class Getopt {

    private static String TAG = Server.class.getSimpleName();

    protected String commandName;

    protected String[] argv;

    protected String optstring;

    protected LongOpt[] longOptions;

    protected boolean long_only;

    private List<String> optionList = new ArrayList<>();

    private int currentI = 0;

    private int currentJ = 0;

    public Getopt(String commandName, String[] argv, String optstring) {
        this(commandName, argv, optstring, null, false);
    }

    public Getopt(String commandName, String[] argv, String optstring, LongOpt[] longOptions) {
        this(commandName, argv, optstring, longOptions, false);
    }

    public Getopt(String commandName, String[] argv, String optstring, LongOpt[] longOptions, boolean long_only) {
        if (optstring.length() == 0) {
            optstring = " ";
        }
        this.commandName = commandName;
        this.argv = argv;
        this.optstring = optstring;
        this.longOptions = longOptions;
        this.long_only = long_only;
    }

    /**
     * Parse the option eg: "-:M:N:abc"  M == message, N == NXXX, a == abs , and so on.
     */
    private void parseOption() {
        if (optstring == null || optstring.equals("") || optstring.charAt(0) != '-') {
            return;
        }
        String[] tmp = optstring.split(":");
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                optionList.add(tmp[i]);
            }
        }
    }

    private String value;

    public String getOptarg() {
        String temp = value;
        value = null;
        return temp;
    }

    public int getopt() {
        if (argv == null || argv.length == 0) {
            return -1;
        }
        for (int i = currentI; i < argv.length; i++) {
            if (argv[i].charAt(0) == '-') {  // Start with '-', this is a short option tag or a long option or combin options.
                if (argv[i].length() > 1) {  // If the length value is lager than zero, so parser the after params.
                    if (argv[i].charAt(1) == '-') {  // if first char is '-', the second char is '-', the after string is a long params.
                        if (argv[i].length() > 2) {  // If start with '-' and seconde character is '-', the after string is a long params.
                            // Get the option
                            String option = argv[i].substring(2, argv[i].length());  // Get the long option and check it is valided or not.
                            // Check the option is valid or not.
                            char tempC = ':';
                            if (longOptions != null) {
                                for (int n = 0; n < longOptions.length; n++) {
                                    if (longOptions[n].getParamLongName() != null && longOptions[n].getParamLongName().equals(option)) {
                                        tempC = (char)longOptions[n].getParamShortName();
                                        currentI = i + 1;
                                        currentJ = 0;
                                        // Check the param is need a value or not.
                                        if (longOptions[n].getRequireValue() == com.hb.pocket.commandline.parser.LongOpt.REQUIRED_ARGUMENT) {
                                            if (argv.length > i + 1 && argv[i + 1] != null && !argv[i + 1].startsWith("-")) {
                                                value = argv[i + 1];
                                                currentI = i + 2;
                                            } else {
                                                // Lack a param value then return error.
                                                return ':';
                                            }
                                        } else if (longOptions[n].getRequireValue() == com.hb.pocket.commandline.parser.LongOpt.NO_ARGUMENT) {
                                            value = null;
                                        } else if (longOptions[n].getRequireValue() == com.hb.pocket.commandline.parser.LongOpt.OPTIONAL_ARGUMENT) {
                                            if (argv.length > i + 1 && argv[i + 1] != null) {
                                                if (!argv[i + 1].startsWith("-")) {
                                                    value = argv[i + 1];
                                                    currentI = i + 2;
                                                } else {
                                                    value = null;
                                                }
                                            } else {
                                                value = null;
                                            }
                                        }
                                        return tempC;
                                    }
                                }
                            }
                            currentI = i + 1;
                            currentJ = 0;
                            return tempC;
                        } else { // If start with '-' and after string is null, this case is invalid and rturn error.
                            currentI = i + 1;
                            currentJ = 0;
                            return ':';
                        }
                    } else { // If the second character is not '-', so we need splite the substring and parser it. eg -abc
                        String option = argv[i].substring(1, argv[i].length());
                        char tempC = ':';
                        // Parser the option string and check every character and return the valid character.
                        for (int j = currentJ; j < option.length(); j++) {
                            for (int n = 0; n < longOptions.length; n++) {
                                if (longOptions[n].getParamShortName() == option.charAt(j)) {
                                    tempC = (char)longOptions[n].getParamShortName();
                                    currentI = i + 1;
                                    currentJ = j;
                                    // Check the param is need a value or not.
                                    if (longOptions[n].getRequireValue() == com.hb.pocket.commandline.parser.LongOpt.REQUIRED_ARGUMENT) {
                                        if (argv.length > i + 1 && argv[i + 1] != null && !argv[i + 1].startsWith("-")) {
                                            value = argv[i + 1];
                                            currentI = i + 2;
                                        } else {
                                            // Lack a param value then return error.
                                            return ':';
                                        }
                                    } else if (longOptions[n].getRequireValue() == com.hb.pocket.commandline.parser.LongOpt.NO_ARGUMENT) {
                                        value = null;
                                    } else if (longOptions[n].getRequireValue() == com.hb.pocket.commandline.parser.LongOpt.OPTIONAL_ARGUMENT) {
                                        if (argv.length > i + 1 && argv[i + 1] != null) {
                                            if (!argv[i + 1].startsWith("-")) {
                                                value = argv[i + 1];
                                                currentI = i + 2;
                                            } else {
                                                value = null;
                                            }
                                        } else {
                                            value = null;
                                        }
                                    }
                                    return tempC;
                                }
                            }
                            // Save the current status. So we need save the i and j values.
                            currentI = i;
                            currentJ = j + 1;
                            return tempC;
                            }
                        }
                        // Reset the currentJ.
                        currentJ = 0;
                    }
                } else { // If start with '-' and after string is null, this case is invalid and rturn error.
                    currentI = i +1;
                    currentJ = 0;
                    return ':';
                }
            }
        return -1;
    }
}
