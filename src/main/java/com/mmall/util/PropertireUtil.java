package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertireUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertireUtil.class);

    private static Properties props;

    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertireUtil.class.getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取错误", e);
        }
    }

    public static String getPropertire(String key){
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value.trim())){
            return null;
        }
        return value;
    }

    public static String getPropertire(String key, String defaultValue){
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value.trim())){
            value = defaultValue;
        }
        return value;
    }
}
