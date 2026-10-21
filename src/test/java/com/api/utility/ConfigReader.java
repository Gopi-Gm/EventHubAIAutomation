package com.api.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility to read test configuration properties from test-config.properties,
 * with overrides from System properties (-Dkey=value) and Environment variables.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    private static final String CONFIG_FILE = "test-config.properties";
    private static final String DEFAULT_BASE_URL = "https://api.eventhub.rahulshettyacademy.com/api";

    static {
        loadProperties();
    }

    private ConfigReader() {
        // Utility class
    }

    private static void loadProperties() {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                properties.load(is);
                logger.info("Loaded configuration properties from {}", CONFIG_FILE);
            } else {
                logger.warn("Configuration file {} not found on classpath", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Failed to load {} properties file: {}", CONFIG_FILE, e.getMessage());
        }
    }

    /**
     * Gets a property value by key, checking System properties first, then test-config.properties.
     */
    public static String getProperty(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.trim();
        }
        return properties.getProperty(key);
    }

    /**
     * Gets a property with a default fallback.
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    /**
     * Returns the base API URL (ensures '/api' suffix is present).
     */
    public static String getBaseUrl() {
        String baseUrl = getProperty("base.url", DEFAULT_BASE_URL);
        if (!baseUrl.endsWith("/api")) {
            baseUrl = baseUrl.replaceAll("/+$", "") + "/api";
        }
        return baseUrl;
    }

    /**
     * Returns default test user email.
     */
    public static String getEmail() {
        return getProperty("eventhub.email", "prem@yopmail.com");
    }

    /**
     * Returns default test user password.
     */
    public static String getPassword() {
        return getProperty("eventhub.password", "Automation@2026");
    }
}
