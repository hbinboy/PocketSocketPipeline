package com.hb.pocket.data.header;

import com.hb.pocket.encryption.EncryptionConst;

/**
 * Created by hb on 22/08/2018.
 */
public class HeaderConfig {
    /**
     * The data encryption type 0: the data is raw 1: md5.
     */
    public static int encryptionType = EncryptionConst.MD5_TYPE;
}
