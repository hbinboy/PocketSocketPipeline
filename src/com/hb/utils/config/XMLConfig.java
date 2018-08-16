package com.hb.utils.config;

import com.hb.utils.log.MyLog;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hb on 16/08/2018.
 */
public class XMLConfig {

    private static String TAG = XMLConfig.class.getSimpleName();

    private volatile static XMLConfig instance = null;

    private Map<String, String> confMap;

    private XMLConfig() {
        confMap = new HashMap<>();
    }

    /**
     * Get the XMLConfig instance.
     * @return
     */
    public synchronized static XMLConfig getInstance() {
        if (instance == null) {
            synchronized (XMLConfig.class) {
                if (instance == null) {
                    try {
                        instance = new XMLConfig();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Read the config.xml.
     * @param xmlFileName
     * @throws Exception
     */
    public void initConfig(String xmlFileName) throws Exception {
        InputStream inputStream = XMLConfig.class.getClassLoader().getResourceAsStream(xmlFileName);
        if (inputStream == null) {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
        }
        if (inputStream == null) {
            throw new Exception(xmlFileName + " not found!");
        }
        try {
            // Use DOM parse the xml file and save the value to the confMap.
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(inputStream));
            Document document = parser.getDocument();
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("config");

            int confNum = nodeList.getLength();
            confMap = new HashMap<>();
            for (int i = 0; i < confNum; i++) {
                Element element = (Element) nodeList.item(i);
                if (!element.getAttribute("key").equals("")) {
                    confMap.put(element.getAttribute("key"), element.getAttribute("value"));
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "Init config.xml failure!");
        } finally {
            inputStream.close();
        }
    }

    /**
     * Get the value form confMap by key.
     * @param key
     * @param defaultValue
     * @return
     */
    public String getConfigValue(String key, String defaultValue) {
        if (confMap == null) {
            return defaultValue;
        } else {
            String result = confMap.get(key);
            if (result != null && !result.equals("")) {
                return result;
            }
        }
        return defaultValue;
    }

    /**
     * Get the value form confMap by key.
     * @param key
     * @param defaultValue
     * @return
     */
    public int getConfigValue(String key, int defaultValue) {
        if (confMap == null) {
            return defaultValue;
        } else {
            int result = Integer.parseInt(confMap.get(key));
            return result;
        }
    }

    /**
     * Get the value form confMap by key.
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getConfigValue(String key, boolean defaultValue) {
        if (confMap == null) {
            return defaultValue;
        } else {
            boolean result = Boolean.parseBoolean(confMap.get(key));
            return result;
        }
    }

}
