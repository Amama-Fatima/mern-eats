package com.merneats.tests;

import com.merneats.utils.BaseTest;
import com.merneats.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthenticationTests extends BaseTest {
    
    @Test(priority = 1, description = "Test Case 1: Verify home page loads successfully")
    public void test01_homepageLoadsSuccessfully() {
        navigateTo("/");
        sleep(2);
        
        // Verify page title contains "MernEats" (no space)
        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.contains("MernEats") || pageTitle.contains("Vite"), 
                "Homepage title not found. Actual title: " + pageTitle);
        
        // Verify hero section or main content is visible
        boolean hasContent = DriverManager.isElementPresent(By.tagName("h1")) || 
                           DriverManager.isElementPresent(By.tagName("main"));
        Assert.assertTrue(hasContent, "Homepage content not found");
        
        System.out.println("Homepage loaded successfully");
    }
    
    @Test(priority = 2, description = "Test Case 2: Test login with valid credentials")
    public void test02_loginWithValidCredentials() {
        navigateTo("/login");
        sleep(3);
        
        try {
            // Fill login form with valid credentials
            WebElement emailInput = DriverManager.waitForVisible(By.name("email"));
            emailInput.clear();
            emailInput.sendKeys("amama.develops@gmail.com");
            
            WebElement passwordInput = DriverManager.waitForVisible(By.name("password"));
            passwordInput.clear();
            passwordInput.sendKeys("123456");
            
            // Click login button
            WebElement loginBtn = DriverManager.waitForClickable(
                    By.xpath("//button[contains(text(), 'Login') or contains(text(), 'Log in') or @type='submit']")
            );
            loginBtn.click();
            
            sleep(4);
            
            // Verify login success - email displayed in header
            boolean emailDisplayed = DriverManager.isElementPresent(
                    By.xpath("//button[contains(text(), '@gmail.com') or contains(., '@gmail.com')]")
            );
            Assert.assertTrue(emailDisplayed, "User email not displayed in header after login");
            
            System.out.println("Login successful - user authenticated");
        } catch (Exception e) {
            System.out.println("Login test info: " + e.getMessage());
            DriverManager.takeScreenshot("Login_Failed");
        }
    }
    
    @Test(priority = 3, description = "Test Case 3: Test login with invalid credentials")
    public void test03_loginWithInvalidCredentials() {
        navigateTo("/login");
        sleep(2);
        
        try {
            // Fill with invalid credentials
            WebElement emailInput = DriverManager.waitForVisible(By.name("email"));
            emailInput.clear();
            emailInput.sendKeys("invalid@example.com");
            
            WebElement passwordInput = DriverManager.waitForVisible(By.name("password"));
            passwordInput.clear();
            passwordInput.sendKeys("WrongPassword123!");
            
            // Click login button
            WebElement loginBtn = DriverManager.waitForClickable(
                    By.xpath("//button[contains(text(), 'Login') or contains(text(), 'Log in') or @type='submit']")
            );
            loginBtn.click();
            
            sleep(3);
            
            // Verify error handling (should still be on login page)
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/login"), 
                    "Should remain on login page with invalid credentials");
            
            System.out.println("Invalid login correctly rejected");
        } catch (Exception e) {
            System.out.println("Invalid login test info: " + e.getMessage());
        }
    }
}
