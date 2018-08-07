package com.hb.pocket.parser;

/**
 * Created by hb on 01/08/2018.
 */
public class LongOpt {

    public static final int NO_ARGUMENT = 0;

    public static final int REQUIRED_ARGUMENT = 1;

    public static final int OPTIONAL_ARGUMENT = 2;

    private String paramLongName;

    private int requireValue;

    private int paramShortName;

    private String description;

    public LongOpt(String paramLongName, int requireValue, int paramShortName, String description) {
        this.paramLongName = paramLongName;
        this.requireValue = requireValue;
        this.paramShortName = paramShortName;
        this.description = description;
    }

    public String getParamLongName() {
        return paramLongName;
    }

    public int getRequireValue() {
        return requireValue;
    }

    public int getParamShortName() {
        return paramShortName;
    }

    public String getDescription() {
        return description;
    }
}
