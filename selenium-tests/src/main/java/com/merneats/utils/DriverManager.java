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

/**
 * WebDriver manager utility class
 */
public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
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
                    options.addArguments("--start-maximized");
                    options.addArguments("--remote-allow-origins=*");
                    
                    // Critical: Single session mode - don't use profiles at all
                    options.addArguments("--single-process");
                    options.addArguments("--no-first-run");
                    options.addArguments("--no-default-browser-check");
                    options.addArguments("--disable-background-networking");
                    options.addArguments("--disable-background-timer-throttling");
                    options.addArguments("--disable-backgrounding-occluded-windows");
                    options.addArguments("--disable-breakpad");
                    options.addArguments("--disable-component-extensions-with-background-pages");
                    options.addArguments("--disable-features=TranslateUI,BlinkGenPropertyTrees");
                    options.addArguments("--disable-ipc-flooding-protection");
                    options.addArguments("--disable-renderer-backgrounding");
                    
                    // Generate unique remote debugging port
                    int remotePort = 9000 + (int)(Math.random() * 1000);
                    options.addArguments("--remote-debugging-port=" + remotePort);
                    
                    System.out.println("Starting Chrome on port: " + remotePort);
                    
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
            
            driver.set(webDriver);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize WebDriver: " + e.getMessage(), e);
        }
    }
    
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
    
    public static void navigateTo(String url) {
        getDriver().get(url);
        waitForPageLoad();
    }
    
    public static void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(config.getExplicitWait()));
        wait.until(webDriver -> 
            ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
        );
    }
    
    public static WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(config.getExplicitWait()));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    public static WebElement waitForClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(config.getExplicitWait()));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    public static WebElement waitForVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(config.getExplicitWait()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public static boolean isElementPresent(By locator) {
        try {
            getDriver().findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public static void scrollToElement(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    public static void clickWithJS(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
    }
    
    public static String takeScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) getDriver();
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String screenshotPath = "screenshots/" + testName + "_" + timestamp + ".png";
            File destination = new File(screenshotPath);
            
            FileUtils.copyFile(source, destination);
            System.out.println("Screenshot saved: " + screenshotPath);
            
            return screenshotPath;
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}