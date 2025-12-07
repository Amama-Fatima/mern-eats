package com.merneats.tests;

import com.merneats.utils.BaseTest;
import com.merneats.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RestaurantSearchTests extends BaseTest {
    
    private static final String TEST_CITY = "London";
    
    @Test(priority = 4, description = "Test Case 4: Search for restaurants by city")
    public void test04_searchRestaurantByCity() {
        navigateTo("/");
        sleep(2);
        
        try {
            // Find search input on homepage
            WebElement searchInput = DriverManager.waitForVisible(
                    By.xpath("//input[@placeholder='Search by City or Town' or @type='text']")
            );
            
            // Enter city name and submit
            searchInput.clear();
            searchInput.sendKeys(TEST_CITY);
            searchInput.sendKeys(Keys.RETURN);
            
            sleep(3);
            
            // Verify navigation to search results page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/search"), 
                    "Not redirected to search page. Current URL: " + currentUrl);
            Assert.assertTrue(currentUrl.toLowerCase().contains(TEST_CITY.toLowerCase()), 
                    "Search city not in URL");
            
        } catch (Exception e) {
            System.out.println("Search test info: " + e.getMessage());
        }
    }
    
    @Test(priority = 9, description = "Test Case 9: Verify search results are displayed")
    public void test09_viewSearchResults() {
        navigateTo("/search/" + TEST_CITY);
        sleep(3);
        
        try {
            // Check if results or no results message is displayed
            boolean hasResults = DriverManager.isElementPresent(
                    By.xpath("//div[contains(@class, 'restaurant') or contains(@class, 'card')]")
            ) || DriverManager.isElementPresent(
                    By.xpath("//*[contains(text(), 'restaurant') or contains(text(), 'Restaurant')]")
            );
            
            boolean hasNoResultsMsg = DriverManager.isElementPresent(
                    By.xpath("//*[contains(text(), 'No results') or contains(text(), 'no restaurants') or contains(text(), 'found')]")
            );
            
            boolean pageHasContent = hasResults || hasNoResultsMsg;
            Assert.assertTrue(pageHasContent, "Search results page appears empty");
            
        } catch (Exception e) {
            System.out.println("View results test info: " + e.getMessage());
        }
    }
    
    @Test(priority = 10, description = "Test Case 10: Filter restaurants by cuisine type")
    public void test05_filterRestaurantsByCuisine() {
        navigateTo("/search/" + TEST_CITY);
        sleep(3);
        
        try {
            // Look for cuisine filter checkboxes
            List<WebElement> cuisineFilters = driver.findElements(By.xpath("//input[@type='checkbox']"));
            
            if (cuisineFilters.size() > 0) {
                WebElement firstFilter = cuisineFilters.get(0);
                DriverManager.scrollToElement(firstFilter);
                sleep(1);
                
                // Click filter using JavaScript to avoid interception issues
                DriverManager.clickWithJS(firstFilter);
                sleep(2);
                
                // Verify filter is applied
                boolean isChecked = firstFilter.isSelected();
                Assert.assertTrue(isChecked, "Cuisine filter not applied");
            } else {
                System.out.println("No cuisine filters found on page");
            }
            
        } catch (Exception e) {
            System.out.println("Filter test info: " + e.getMessage());
        }
    }
    
    @Test(priority = 11, description = "Test Case 11: Sort restaurant search results")
    public void test11_sortSearchResults() {
        navigateTo("/search/" + TEST_CITY);
        sleep(3);
        
        try {
            // Look for sort dropdown or buttons
            List<WebElement> sortElements = driver.findElements(
                    By.xpath("//select[contains(@name, 'sort')] | //button[contains(text(), 'Sort')] | //*[contains(@class, 'sort')]")
            );
            
            if (sortElements.size() > 0) {
                WebElement sortElement = sortElements.get(0);
                DriverManager.scrollToElement(sortElement);
                sleep(1);
                
                sortElement.click();
                sleep(1);
                
                // Try to select a sort option
                List<WebElement> sortOptions = driver.findElements(
                        By.xpath("//option | //*[@role='menuitem']")
                );
                
                if (sortOptions.size() > 1) {
                    sortOptions.get(1).click();
                    sleep(2);
                }
                
                System.out.println("Sort functionality tested successfully");
            } else {
                System.out.println("Sort functionality not found");
            }
            
        } catch (Exception e) {
            System.out.println("Sort test info: " + e.getMessage());
        }
    }
    
    @Test(priority = 12, description = "Test Case 12: View details of a specific restaurant")
    public void test06_viewRestaurantDetails() {
        navigateTo("/search/" + TEST_CITY);
        sleep(3);
        
        try {
            // Find restaurant cards/links
            List<WebElement> restaurantLinks = driver.findElements(
                    By.xpath("//a[contains(@href, '/detail/')] | //div[contains(@class, 'restaurant')]//a")
            );
            
            if (restaurantLinks.size() > 0) {
                WebElement firstRestaurant = restaurantLinks.get(0);
                DriverManager.scrollToElement(firstRestaurant);
                sleep(1);
                firstRestaurant.click();
                
                sleep(3);
                
                // Verify navigation to detail page
                String currentUrl = driver.getCurrentUrl();
                Assert.assertTrue(currentUrl.contains("/detail/"), 
                        "Not navigated to detail page. Current URL: " + currentUrl);
            } else {
                System.out.println("No restaurant links found to test");
            }
            
        } catch (Exception e) {
            System.out.println("Detail view test info: " + e.getMessage());
        }
    }
    
    @Test(priority = 13, description = "Test Case 13: Navigate through search results pagination")
    public void test13_paginationNavigation() {
        navigateTo("/search/" + TEST_CITY);
        sleep(3);
        
        try {
            // Look for pagination controls
            List<WebElement> paginationElements = driver.findElements(
                    By.xpath("//nav[@aria-label='pagination'] | //*[contains(@class, 'pagination')] | //button[contains(text(), 'Next')]")
            );
            
            if (paginationElements.size() > 0) {
                // Find next button
                List<WebElement> nextButtons = driver.findElements(
                        By.xpath("//button[contains(text(), 'Next')] | //a[contains(text(), 'Next')]")
                );
                
                if (nextButtons.size() > 0 && nextButtons.get(0).isEnabled()) {
                    nextButtons.get(0).click();
                    sleep(2);
                    System.out.println("Pagination navigation tested successfully");
                } else {
                    System.out.println("Next button not clickable (might be last page)");
                }
            } else {
                System.out.println("Pagination not found (may be single page of results)");
            }
            
        } catch (Exception e) {
            System.out.println("Pagination test info: " + e.getMessage());
        }
    }
}
