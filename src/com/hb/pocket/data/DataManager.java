package com.hb.pocket.data;

import com.hb.pocket.data.body.Body;
import com.hb.pocket.data.header.Header;
import com.hb.pocket.data.header.HeaderConstant;
import com.hb.utils.log.MyLog;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hb on 06/08/2018.
 */
public class DataManager {

    private static String TAG = DataManager.class.getSimpleName();

    private Header header;

    private Body body;

    public static void main(String[] args) {
        DataManager dataManager = new DataManager();
        String str = "Hello word 222222222 \n 111111";
        String wholeMessageMD5 = dataManager.md5(str);
        String[] result = dataManager.spliteString(str, 7);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            byte[] data = dataManager.genSendDataPackage(result[i], i, result.length, wholeMessageMD5);
            if (dataManager.getReceiveDataPackageData(data) != null) {
                sb.append(dataManager.getBody().getData());
            }
        }
        MyLog.d(TAG, sb.toString());
    }

    public DataManager() {
        header = new Header();
        body = new Body();
    }

    public String getReceiveDataPackageData(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        if (checkHeaderStruct(data)) {
            return body.getData();
        } else {
            return null;
        }
    }

    public String[] spliteString(String msg, int frameSize) {
        if (msg == null || msg.length() == 0 || frameSize <= 0) {
            return null;
        }
        int len = msg.length();
        int frameCount;
        if (len % frameSize == 0) {
            frameCount = len / frameSize;
        } else {
            frameCount = len / frameSize + 1;
        }
        String[] result = new String[frameCount];
        for (int i = 0; i < frameCount; i++) {
            if (i * frameSize + frameSize < len) {
                String tmp = msg.substring(i * frameSize, i * frameSize + frameSize);
                result[i] = tmp;
            } else {
                String tmp = msg.substring(i * frameSize, len);
                result[i] = tmp;
            }

        }
        return result;
    }

    /**
     * Generic data by string.
     * @param sliceMessage
     * @return
     */
    public byte[] genSendDataPackage(String sliceMessage, int index, int count, String wholeMD5) {
        if (sliceMessage == null || "".equals(sliceMessage)) {
            return null;
        }

        List<Byte> sendList = new ArrayList<>();
        // protocol header.
        sendList.add(HeaderConstant.HEAD);
        for (int i = 0; i < HeaderConstant.NAME.length; i++) {
            byte[] b = charToByte(HeaderConstant.NAME[i]);
            for (int j = 0; j < b.length; j++) {
                sendList.add(b[j]);
            }
        }
        sendList.add(HeaderConstant.VERSION);
        sendList.add(HeaderConstant.TYPE_STRING);
        // index
        byte[] indexArr = intToByteArray(index);
        for (int i = 0; i < indexArr.length; i++) {
            sendList.add(indexArr[i]);
        }
        // count
        byte[] countArr = intToByteArray(count);
        for (int i = 0; i < countArr.length; i++) {
            sendList.add(countArr[i]);
        }
        // data length
        int dataLength = sliceMessage.getBytes().length;
        byte[] dataLen = intToByteArray(dataLength);
        for (int i = 0; i < dataLen.length; i++) {
            sendList.add(dataLen[i]);
        }
        // the whole message md5 value.
        byte[] wholeMD5Value = wholeMD5.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < wholeMD5Value.length; i++) {
            sendList.add(wholeMD5Value[i]);
        }
        // the slice message md5 value.
        byte[] sliceMD5Value = md5(sliceMessage).getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < sliceMD5Value.length; i++) {
            sendList.add(sliceMD5Value[i]);
        }

        // header length
        byte[] headLen = intToByteArray(sendList.size() + 4);
        for (int i = 0; i < headLen.length; i++) {
            sendList.add(headLen[i]);
        }
        // data.
        byte[] data = sliceMessage.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < data.length; i++) {
            sendList.add(data[i]);
        }
        Object[] b = (Object[]) sendList.toArray();
        byte[] bb = new byte[b.length];
        for (int i = 0; i < b.length; ++i) {
            bb[i] = ( (Byte) b[i]).byteValue();
        }
        return bb;
    }

    /**
     * Check the package header struct is valid or not.
     * @param data
     * @return
     */
    private boolean checkHeaderStruct(byte[] data) {
        if (data == null || data.length == 0) {
            return false;
        }

        int index = 0;
        // check head.
        if (data.length >= index + 1) {
            if (data[index] != HeaderConstant.HEAD) {
                return false;
            } else {
                index++;
            }
        } else {
            return false;
        }
        // check name.
        if (data.length >= index + 1) {
            byte[] name = charArrayToByte(HeaderConstant.NAME);
            if (name == null || name.length == 0) {
                return false;
            } else {
                for (int i = 0; i < name.length; i++) {
                    if (name[i] == data[index++]) {
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        // check version.
        if (data.length >= index + 1) {
            if (HeaderConstant.VERSION == data[index]) {
                header.setVersion(data[index++]);
            } else {
                return false;
            }
        }
        // check type
        if (data.length >= index + 1) {
            if (data[index] == 0x01 || data[index] == 0x02 || data[index] == 0x03) {
                header.setType(data[index++]);
            } else {
                return false;
            }
        }
        // index
        if (data.length >= index + 1) {
            byte[] indexLen = new byte[4];
            for (int i = 0; i < indexLen.length; i++) {
                if (data.length >= index + 1) {
                    indexLen[i] = data[index++];
                } else {
                    return false;
                }
            }
            header.setIndexData(byteArrayToInt(indexLen));
            if (header.getIndexData() < 0) {
                return false;
            }
        }
        // count
        if (data.length >= index + 1) {
            byte[] countByte = new byte[4];
            for (int i = 0; i < countByte.length; i++) {
                if (data.length >= index + 1) {
                    countByte[i] = data[index++];
                } else {
                    return false;
                }
            }
            header.setCount(byteArrayToInt(countByte));
            if (header.getCount() <= 0) {
                return false;
            }
        }
        // data length
        if (data.length >= index + 1) {
            byte[] dataLengthByte = new byte[4];
            for (int i = 0; i < dataLengthByte.length; i++) {
                if (data.length >= index + 1) {
                    dataLengthByte[i] = data[index++];
                } else {
                    return false;
                }
            }
            header.setDataLen(byteArrayToInt(dataLengthByte));
            if (header.getDataLen() < 0) {
                return false;
            }
        }
        // the whole message value.
        if (data.length >= index + 1) {
            byte[] wholeMessageMD5Value = new byte[32];
            for (int i = 0; i < wholeMessageMD5Value.length; i++) {
                if (data.length >= index + 1) {
                    wholeMessageMD5Value[i] = data[index++];
                } else {
                    return false;
                }
            }
            header.setWholeMD5(new String(wholeMessageMD5Value).toCharArray());
        }
        // the selice message value.
        if (data.length >= index + 1) {
            byte[] sliceMessageMD5Value = new byte[32];
            for (int i = 0; i < sliceMessageMD5Value.length; i++) {
                if (data.length >= index + 1) {
                    sliceMessageMD5Value[i] = data[index++];
                } else {
                    return false;
                }
            }
            header.setSliceMD5(new String(sliceMessageMD5Value).toCharArray());
        }
        // header length value
        if (data.length >= index + 1) {
            byte[] headerLengthByte = new byte[4];
            for (int i = 0; i < headerLengthByte.length; i++) {
                if (data.length >= index + 1) {
                    headerLengthByte[i] = data[index++];
                } else {
                    return false;
                }
            }
            header.setHeadLen(byteArrayToInt(headerLengthByte));
            if (header.getHeadLen() < 0) {
                return false;
            }
        }

        // data value
        if (header.getDataLen() > 0 && data.length >= index + 1) {
            byte[] dataByte = new byte[header.getDataLen()];
            for (int i = 0; i < dataByte.length; i++) {
                if (data.length >= index + 1) {
                    dataByte[i] = data[index++];
                } else {
                    return false;
                }
            }
            body.setData(new String(dataByte));
        }

        return true;
    }

    /**
     * Get 32 bits md5 value.
     * @param value
     * @return
     */
    public String md5(String value) {

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(value.getBytes());
            StringBuffer buffer = new StringBuffer();
            for (byte b : result) {
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * Change the int to byte array, the low byte at after and the high byte at before.
     b[0] = 11111111(0xff) & 01100001
     b[1] = 11111111(0xff) & 00000000
     b[2] = 11111111(0xff) & 00000000
     b[3] = 11111111(0xff) & 00000000
     * @param value
     * @return
     */
    private byte[] intToByteArray(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * Change the byte array(the low byte at after and the high byte at before)
     * @param bArr
     * @return
     */
    private int byteArrayToInt(byte[] bArr) {
        if (bArr.length != 4) {
            return -1;
        }
        return (int) ((((bArr[0] & 0xff) << 24)
                | ((bArr[1] & 0xff) << 16)
                | ((bArr[2] & 0xff) << 8)
                | ((bArr[3] & 0xff) << 0)));
    }

    /**
     * Generic byte array by char array.
     * @param arr
     * @return
     */
    private byte[] charArrayToByte(char[] arr) {
        List<Byte> sendList = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            byte[] b = charToByte(arr[i]);
            for (int j = 0; j < b.length; j++) {
                sendList.add(b[j]);
            }
        }
        byte[] result = new byte[sendList.size()];
        for (int i = 0; i < sendList.size(); i++) {
            result[i] = sendList.get(i).byteValue();
        }
        return result;
    }

    /**
     * Generic byte array by char.
     * @param c
     * @return
     */
    private byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }


    public Header getHeader(byte[] data) {
        return header;
    }

    public Body getBody(byte[] data) {
        return body;
    }

    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }
}
