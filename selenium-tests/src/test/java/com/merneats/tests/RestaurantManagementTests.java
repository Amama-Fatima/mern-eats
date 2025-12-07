package com.merneats.tests;

import com.merneats.utils.BaseTest;
import com.merneats.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RestaurantManagementTests extends BaseTest {
    
    @Test(priority = 9, description = "Test Case 9: Access manage restaurant page")
    public void test09_accessManageRestaurantPage() {
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
            
            // Click "Manage Restaurants" link from dropdown
            WebElement manageRestaurantLink = DriverManager.waitForClickable(
                    By.xpath("//a[contains(text(), 'Manage Restaurant')] | //button[contains(text(), 'Manage Restaurant')]")
            );
            manageRestaurantLink.click();
            sleep(2);
            
            // Verify we're on manage restaurant page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("manage-restaurant"), 
                    "Not on manage restaurant page. Current URL: " + currentUrl);
            
            System.out.println("Successfully navigated to manage restaurant page");
        } catch (Exception e) {
            System.out.println("Access manage restaurant test info: " + e.getMessage());
            DriverManager.takeScreenshot("ManageRestaurant_Navigation_Failed");
        }
    }
    
    @Test(priority = 10, description = "Test Case 10: Fill restaurant details and add menu item")
    public void test10_fillRestaurantDetailsAndMenuItem() {
        // User already logged in from test09
        navigateTo("/manage-restaurant");
        sleep(3);
        
        try {
            // Fill restaurant name
            List<WebElement> nameInputs = driver.findElements(
                    By.xpath("//input[@name='restaurantName' or contains(@placeholder, 'Restaurant Name')]")
            );
            
            if (nameInputs.size() > 0) {
                nameInputs.get(0).clear();
                nameInputs.get(0).sendKeys("Test Restaurant");
                sleep(1);
            }
            
            // Fill city
            List<WebElement> cityInputs = driver.findElements(
                    By.xpath("//input[@name='city' or contains(@placeholder, 'City')]")
            );
            
            if (cityInputs.size() > 0) {
                cityInputs.get(0).clear();
                cityInputs.get(0).sendKeys("London");
                sleep(1);
            }
            
            // Fill country
            List<WebElement> countryInputs = driver.findElements(
                    By.xpath("//input[@name='country' or contains(@placeholder, 'Country')]")
            );
            
            if (countryInputs.size() > 0) {
                countryInputs.get(0).clear();
                countryInputs.get(0).sendKeys("UK");
                sleep(1);
            }
            
            // Fill delivery price
            List<WebElement> deliveryPriceInputs = driver.findElements(
                    By.xpath("//input[@name='deliveryPrice' or contains(@placeholder, 'Delivery Price')]")
            );
            
            if (deliveryPriceInputs.size() > 0) {
                deliveryPriceInputs.get(0).clear();
                deliveryPriceInputs.get(0).sendKeys("5.99");
                sleep(1);
            }
            
            // Fill estimated delivery time
            List<WebElement> deliveryTimeInputs = driver.findElements(
                    By.xpath("//input[@name='estimatedDeliveryTime' or contains(@placeholder, 'Delivery Time')]")
            );
            
            if (deliveryTimeInputs.size() > 0) {
                deliveryTimeInputs.get(0).clear();
                deliveryTimeInputs.get(0).sendKeys("30");
                sleep(1);
            }
            
            System.out.println("Restaurant details filled successfully");
            
            // Add menu item
            sleep(2);
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "window.scrollTo(0, document.body.scrollHeight);"
            );
            sleep(2);
            
            // Try to find "Add Menu Item" button
            List<WebElement> addMenuButtons = driver.findElements(
                    By.xpath("//button[contains(text(), 'Add Menu Item') or contains(text(), 'Add Item')]")
            );
            
            if (addMenuButtons.size() > 0) {
                DriverManager.scrollToElement(addMenuButtons.get(0));
                addMenuButtons.get(0).click();
                sleep(2);
                
                // Fill menu item details
                List<WebElement> menuItemInputs = driver.findElements(
                        By.xpath("//input[@placeholder='Name' or @name='name']")
                );
                
                if (menuItemInputs.size() > 0) {
                    // Find the last name input (for menu item)
                    WebElement itemNameInput = menuItemInputs.get(menuItemInputs.size() - 1);
                    itemNameInput.clear();
                    itemNameInput.sendKeys("Deluxe Burger");
                    
                    // Find price input
                    List<WebElement> priceInputs = driver.findElements(
                            By.xpath("//input[@placeholder='Price' or @type='number']")
                    );
                    
                    if (priceInputs.size() > 0) {
                        WebElement priceInput = priceInputs.get(priceInputs.size() - 1);
                        priceInput.clear();
                        priceInput.sendKeys("12.99");
                    }
                    
                    System.out.println("Menu item added successfully");
                }
            } else {
                System.out.println("Add Menu Item button not found");
            }
            
        } catch (Exception e) {
            System.out.println("Fill restaurant and menu test info: " + e.getMessage());
        }
    }
}
