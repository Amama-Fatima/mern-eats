package com.merneats.utils;

import com.merneats.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * WebDriver manager utility class
 */
public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<String> userDataDir = new ThreadLocal<>();
    private static ConfigManager config = ConfigManager.getInstance();
    
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }
    
    private static void initializeDriver() {
        WebDriver webDriver = null;
        
        try {
            String browser = config.getBrowser().toLowerCase();
            
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    
                    // Create TRULY unique user data directory using UUID
                    String baseDir = System.getProperty("chrome.userDataDir", 
                                    System.getProperty("java.io.tmpdir"));
                    String uniqueId = UUID.randomUUID().toString();
                    String dirName = String.format("chrome-%s-%d", 
                                    uniqueId, Thread.currentThread().getId());
                    String userDataDirPath = baseDir + "/" + dirName;
                    
                    // Store the directory path for cleanup
                    userDataDir.set(userDataDirPath);
                    
                    options.addArguments("--user-data-dir=" + userDataDirPath);
                    
                    if (config.isHeadless()) {
                        options.addArguments("--headless=new");
                    }
                    
                    // Essential flags for Docker/EC2 environments
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--disable-software-rasterizer");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-extensions");
                    options.addArguments("--disable-popup-blocking");
                    options.addArguments("--remote-allow-origins=*");
                    
                    // Remove problematic flags
                    // options.addArguments("--start-maximized"); // Can cause issues in headless
                    
                    options.addArguments("--no-first-run");
                    options.addArguments("--no-default-browser-check");
                    options.addArguments("--disable-background-networking");
                    
                    // Generate unique remote debugging port
                    int remotePort = 9000 + (int)(Math.random() * 1000);
                    options.addArguments("--remote-debugging-port=" + remotePort);
                    
                    // Disable user metrics reporting
                    options.addArguments("--disable-background-timer-throttling");
                    options.addArguments("--disable-backgrounding-occluded-windows");
                    options.addArguments("--disable-renderer-backgrounding");
                    
                    System.out.println("[" + Thread.currentThread().getId() + "] Starting Chrome with profile: " + 
                                     userDataDirPath + " on port: " + remotePort);
                    
                    webDriver = new ChromeDriver(options);
                    break;
                    
                default:
                    throw new RuntimeException("Unsupported browser: " + browser);
            }
            
            // Set timeouts
            webDriver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
            
            // Delete cookies for fresh session
            webDriver.manage().deleteAllCookies();
            
            driver.set(webDriver);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage(), e);
        }
    }
    
    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                System.out.println("[" + Thread.currentThread().getId() + "] Quitting driver...");
                driver.get().quit();
            } catch (Exception e) {
                System.err.println("[" + Thread.currentThread().getId() + "] Error quitting driver: " + e.getMessage());
            } finally {
                driver.remove();
                
                // Clean up user data directory
                try {
                    String dirPath = userDataDir.get();
                    if (dirPath != null) {
                        File dir = new File(dirPath);
                        if (dir.exists()) {
                            System.out.println("[" + Thread.currentThread().getId() + "] Cleaning up Chrome profile: " + dirPath);
                            FileUtils.deleteDirectory(dir);
                        }
                        userDataDir.remove();
                    }
                } catch (IOException e) {
                    System.err.println("[" + Thread.currentThread().getId() + "] Failed to clean up Chrome profile: " + e.getMessage());
                }
                
                // Kill any remaining Chrome processes
                try {
                    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                        Runtime.getRuntime().exec("pkill -f chrome").waitFor();
                        Runtime.getRuntime().exec("pkill -f chromedriver").waitFor();
                    }
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
            
            // Give Chrome time to fully release resources
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // ... rest of your methods remain the same ...
}