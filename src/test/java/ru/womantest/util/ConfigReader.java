package ru.womantest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private final Properties props = new Properties();

    public ConfigReader() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test.properties", e);
        }
    }

    public boolean hasCredentials() {
        return getEmail() != null && !getEmail().isBlank()
            && getPassword() != null && !getPassword().isBlank();
    }

    public String getEmail() {
        return props.getProperty("test.email");
    }

    public String getPassword() {
        return props.getProperty("test.password");
    }
}
