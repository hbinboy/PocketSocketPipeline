package com.hb.pocket.data.header;

import com.hb.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hb on 06/08/2018.
 */
public class Header {

    private static String TAG = Header.class.getSimpleName();

    /**
     * protocol header.
     */
    private byte head = HeaderConstant.HEAD;
    /**
     * package header name.
     */
    private char[] name = HeaderConstant.NAME;

    /**
     * package header version.
     */
    private byte version = HeaderConstant.VERSION;

    /**
     * data type  0x1 : string.  0x2  : file.  0x3  :  string and file.
     */
    private byte type = HeaderConstant.TYPE_STRING;

    /**
     * data package index.
     */
    private int indexData = 0;

    /**
     * data package count number.
     */
    private int count = 1;

    /**
     * file type.
     */
    private char[] ext = new char[5];

    /**
     * data length.
     */
    private int dataLen = 0;

    /**
     * header length.
     */
    private int headLen = 0;

    public Header() {

    }

    public byte getHead() {
        return head;
    }

    public char[] getName() {
        return name;
    }

    public byte getVersion() {
        return version;
    }

    public byte getType() {
        return type;
    }

    public int getIndexData() {
        return indexData;
    }

    public int getCount() {
        return count;
    }

    public char[] getExt() {
        return ext;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public void setName(char[] name) {
        this.name = name;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setIndexData(int indexData) {
        this.indexData = indexData;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setExt(char[] ext) {
        this.ext = ext;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    public int getHeadLen() {
        return headLen;
    }

    public void setHeadLen(int headLen) {
        this.headLen = headLen;
    }
}
