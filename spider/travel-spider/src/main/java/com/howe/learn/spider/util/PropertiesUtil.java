package com.howe.learn.spider.util;

import com.howe.learn.spider.basic.cache.base.PoolUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author Karl
 * @Date 2017/1/10 17:46
 */
public class PropertiesUtil {
    private static final String CONFIG_FILE = "spider.properties";

    public static String get(String key) {
        Properties prop = new Properties();
        try {
            prop.load(PoolUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
            return prop.getProperty(key);
        } catch (IOException e) {
            return null;
        }
    }

}
