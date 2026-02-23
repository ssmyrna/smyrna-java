package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();
    ;
    private static final String CONFIG_FILE_PATH = "src/test/config.properties";

    static {
        try {
            FileInputStream file = new FileInputStream(CONFIG_FILE_PATH);
            properties = new Properties();
            properties.load(file);
            file.close();
        } catch (IOException e) {
            throw new RuntimeException("Properties file could not be loaded!");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}