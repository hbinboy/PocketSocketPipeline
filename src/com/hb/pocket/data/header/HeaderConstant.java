package com.hb.pocket.data.header;

/**
 * Created by hb on 06/08/2018.
 */
public class HeaderConstant {

    /**
     * protocol header.
     */
    public final static byte HEAD = 0xaa - 256;

    /**
     * package header name.
     */
    public final static char[] NAME = "HAN.ROBIN".toCharArray();

    /**
     * package header version.
     */
    public final static byte VERSION = 0x1;

    /**
     * data type  0x1 : string.  0x2  : file.  0x3  :  string and file.
     */
    public final static byte TYPE_STRING = 0x1;

    public final static byte TYPE_FILE = 0x2;

    public final static byte TYPE_STRING_FILE = 0x3;

}
