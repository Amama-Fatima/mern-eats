package com.merneats.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager for test settings
 */
public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        try {
            // Try to load from config.properties file
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            // Use default properties if file not found
            setDefaultProperties();
        }
    }
    
    private void setDefaultProperties() {
        properties.setProperty("baseUrl", "http://localhost:5173");
        properties.setProperty("apiUrl", "http://localhost:7000");
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "true");
        properties.setProperty("implicitWait", "10");
        properties.setProperty("explicitWait", "15");
        properties.setProperty("pageLoadTimeout", "30");
        
        // Test user credentials
        properties.setProperty("testUserEmail", "testuser@example.com");
        properties.setProperty("testUserPassword", "TestPassword123!");
        properties.setProperty("testUserName", "Test User");
        properties.setProperty("testUserAddress", "123 Test Street");
        properties.setProperty("testUserCity", "Test City");
        properties.setProperty("testUserCountry", "Test Country");
    }
    
    public String getBaseUrl() {
        return properties.getProperty("baseUrl");
    }
    
    public String getApiUrl() {
        return properties.getProperty("apiUrl");
    }
    
    public String getBrowser() {
        return properties.getProperty("browser");
    }
    
    public boolean isHeadless() {
        String headless = System.getProperty("headless", properties.getProperty("headless"));
        return Boolean.parseBoolean(headless);
    }
    
    public int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicitWait"));
    }
    
    public int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicitWait"));
    }
    
    public int getPageLoadTimeout() {
        return Integer.parseInt(properties.getProperty("pageLoadTimeout"));
    }
    
    public String getTestUserEmail() {
        return properties.getProperty("testUserEmail");
    }
    
    public String getTestUserPassword() {
        return properties.getProperty("testUserPassword");
    }
    
    public String getTestUserName() {
        return properties.getProperty("testUserName");
    }
    
    public String getTestUserAddress() {
        return properties.getProperty("testUserAddress");
    }
    
    public String getTestUserCity() {
        return properties.getProperty("testUserCity");
    }
    
    public String getTestUserCountry() {
        return properties.getProperty("testUserCountry");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
