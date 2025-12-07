package com.merneats.utils;

import com.merneats.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base test class that all test classes should extend
 */
public class BaseTest {
    protected WebDriver driver;
    protected ConfigManager config;
    
    @BeforeClass
    public void setUpClass() {
        config = ConfigManager.getInstance();
        System.out.println("========================================");
        System.out.println("Starting Test Suite");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Headless Mode: " + config.isHeadless());
        System.out.println("========================================");
    }
    
    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String testName = result.getMethod().getMethodName();
            System.out.println("Test FAILED: " + testName);
            DriverManager.takeScreenshot("FAILURE_" + testName);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            System.out.println("✓ Test PASSED: " + result.getMethod().getMethodName());
        }
        
        DriverManager.quitDriver();
    }
    
    @AfterClass
    public void tearDownClass() {
        System.out.println("========================================");
        System.out.println("Test Suite Completed");
        System.out.println("========================================");
    }
    
    protected void navigateTo(String path) {
        String url = config.getBaseUrl() + path;
        DriverManager.navigateTo(url);
    }
    
    protected void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
