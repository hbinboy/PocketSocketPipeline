package com.hb.pocket.data;

import com.hb.pocket.data.body.Body;
import com.hb.pocket.data.header.Header;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hb on 02/10/2018.
 */
public class Data {

    /**
     * The whole message md5 value.
     */
    private String wholeMD5 = "";

    private Map<Integer, Header> headerMap = new ConcurrentHashMap<>();

    private Map<Integer, Body> bodyMap = new ConcurrentHashMap<>();

    public String getWholeMD5() {
        return wholeMD5;
    }

    public Map<Integer, Header> getHeaderMap() {
        return headerMap;
    }

    public Map<Integer, Body> getBodyMap() {
        return bodyMap;
    }

    public void setWholeMD5(String wholeMD5) {
        this.wholeMD5 = wholeMD5;
    }

    public void setHeaderMap(Map<Integer, Header> headerMap) {
        this.headerMap = headerMap;
    }

    public void setBodyMap(Map<Integer, Body> bodyMap) {
        this.bodyMap = bodyMap;
    }

    /**
     * Merger the package to a whole package and get the all data.
     * @return
     */
    public String getWholeData() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bodyMap.size(); i++) {
            sb.append(bodyMap.get(i).getData().substring(0, bodyMap.get(i).getData().length() - 1));
        }
        return sb.toString();
    }

    /**
     * Clear the data.
     */
    public void clear() {
        wholeMD5 = "";
        headerMap.clear();
        bodyMap.clear();
    }
}
