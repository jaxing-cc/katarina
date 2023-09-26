package com.github.jaxing.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author cjxin
 * @date 2023/06/12
 */
public class ConfigUtils {

    public static Properties properties;

    public static void init() throws IOException {
        Resource resource = new ClassPathResource("config.properties");
        properties = PropertiesLoaderUtils.loadProperties(resource);
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Integer getAsInteger(String key) {
        return getAsInteger(key, null);
    }

    public static Integer getAsInteger(String key, Integer defaultValue) {
        String property = properties.getProperty(key);
        Integer res;
        try {
            res = Integer.parseInt(property);
        } catch (Exception e) {
            res = defaultValue;
        }
        return res;
    }
}
