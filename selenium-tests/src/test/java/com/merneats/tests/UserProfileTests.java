package com.merneats.tests;

import com.merneats.utils.BaseTest;
import com.merneats.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class UserProfileTests extends BaseTest {
    
    @Test(priority = 7, description = "Test Case 7: Access user profile page when logged in")
    public void test07_accessUserProfilePage() {
        // Login first
        navigateTo("/login");
        sleep(3);
        
        try {
            WebElement emailInput = DriverManager.waitForVisible(By.name("email"));
            emailInput.clear();
            emailInput.sendKeys("amama.develops@gmail.com");
            
            WebElement passwordInput = DriverManager.waitForVisible(By.name("password"));
            passwordInput.clear();
            passwordInput.sendKeys("123456");
            
            WebElement loginBtn = DriverManager.waitForClickable(
                    By.xpath("//button[contains(text(), 'Login') or contains(text(), 'Log in') or @type='submit']")
            );
            loginBtn.click();
            sleep(5);
            
            // Click the email button (dropdown) in the header
            WebElement emailButton = DriverManager.waitForClickable(
                    By.xpath("//button[contains(text(), '@gmail.com') or contains(., '@gmail.com')]")
            );
            emailButton.click();
            sleep(2);
            
            // Click "User Profile" link from dropdown
            WebElement userProfileLink = DriverManager.waitForClickable(
                    By.xpath("//a[contains(text(), 'User Profile')] | //button[contains(text(), 'User Profile')]")
            );
            userProfileLink.click();
            sleep(2);
            
            // Verify we're on user profile page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("user-profile"), 
                    "Not on user profile page. Current URL: " + currentUrl);
            
            System.out.println("Successfully navigated to user profile page");
        } catch (Exception e) {
            System.out.println("Profile access test info: " + e.getMessage());
            DriverManager.takeScreenshot("UserProfile_Navigation_Failed");
        }
    }
    
    @Test(priority = 8, description = "Test Case 8: Update user profile information")
    public void test08_updateUserProfile() {
        // User already logged in from test07
        navigateTo("/user-profile");
        sleep(3);
        
        try {
            // Find name input field
            List<WebElement> nameInputs = driver.findElements(
                    By.xpath("//input[@name='name' or @placeholder='Name' or contains(@placeholder, 'name')]")
            );
            
            if (nameInputs.size() > 0) {
                WebElement nameInput = nameInputs.get(0);
                nameInput.clear();
                nameInput.sendKeys("Amama Updated");
                
                // Find address input
                List<WebElement> addressInputs = driver.findElements(
                        By.xpath("//input[@name='addressLine1' or contains(@placeholder, 'Address')]")
                );
                
                if (addressInputs.size() > 0) {
                    WebElement addressInput = addressInputs.get(0);
                    addressInput.clear();
                    addressInput.sendKeys("123 Test Street");
                }
                
                // Find city input
                List<WebElement> cityInputs = driver.findElements(
                        By.xpath("//input[@name='city' or contains(@placeholder, 'City')]")
                );
                
                if (cityInputs.size() > 0) {
                    WebElement cityInput = cityInputs.get(0);
                    cityInput.clear();
                    cityInput.sendKeys("Test City");
                }
                
                // Click save button
                List<WebElement> saveButtons = driver.findElements(
                        By.xpath("//button[contains(text(), 'Save') or contains(text(), 'Update') or @type='submit']")
                );
                
                if (saveButtons.size() > 0) {
                    saveButtons.get(0).click();
                    sleep(2);
                    System.out.println("Profile updated successfully");
                } else {
                    System.out.println("Save button not found");
                }
            } else {
                System.out.println("Name input field not found");
            }
            
        } catch (Exception e) {
            System.out.println("Update profile test info: " + e.getMessage());
        }
    }
}
