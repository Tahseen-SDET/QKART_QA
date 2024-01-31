package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import okhttp3.Address;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

// @Listeners(ListenerClass.class)
public class QKART_Tests {

    public static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
    * Testcase01: Verify a new user can successfully register
    */
    @Test(description = "Verify registration happens correctly", priority = 1, groups = {"Sanity_test"})
    @Parameters({"user", "pass"})
    public void TestCase01(String user, String pass) throws InterruptedException {
    Boolean status;
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser(user, pass, true);
    Assert.assertTrue(status, "Failed to register new user");

    // Save the last generated username
    lastGeneratedUserName = registration.lastGeneratedUsername;

    // Visit the login page and login with the previuosly registered user
    Login login = new Login(driver);
    login.navigateToLoginPage();
    status = login.PerformLogin(lastGeneratedUserName, pass);
    Assert.assertTrue(status, "Failed to login with registered user");

    // Visit the home page and log out the logged in user
    Home home = new Home(driver);
    status = home.PerformLogout();

    }

    /*
    * Verify that an existing user is not allowed to re-register on QKart
    */
    @Test(description = "Verify re-registering an already registered user fails", priority = 2, groups = {"Sanity_test"})
    @Parameters({"user", "pass"})
    public void TestCase02(String user, String pass) throws InterruptedException {
    Boolean status;

    // Visit the Registration page and register a new user
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();
    status = registration.registerUser(user, pass, true);
    Assert.assertTrue(status, "Failed to register new user");

    // Save the last generated username
    lastGeneratedUserName = registration.lastGeneratedUsername;

    // Visit the Registration page and try to register using the previously
    // registered user's credentials
    registration.navigateToRegisterPage();
    status = registration.registerUser(lastGeneratedUserName, pass, false);
    Assert.assertFalse(status, "Already registered user able to Re-register");
    Home home = new Home(driver);
    status = home.PerformLogout();

    }

    /*
    * Verify the functinality of the search text box
    */
    @Test(description = "Verify the functionality of search text box", priority = 3,groups = {"Sanity_test"})
    @Parameters({"productName1", "productName2"})
    public void TestCase03(String productName1, String productName2) throws InterruptedException {
    boolean status;

    // Visit the home page
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    // Search for the "yonex" product
    status = homePage.searchForProduct(productName1);
    SoftAssert assert1 = new SoftAssert();
    assert1.assertTrue(status, "Unable to search for given product");

    // Fetch the search results
    List<WebElement> searchResults = homePage.getSearchResults();
    assert1.assertFalse(searchResults.size() == 0, "There were no results for the given search string");

    // Verify the search results are available

    for (WebElement webElement : searchResults) {
    // Create a SearchResult object from the parent element
        SearchResult resultelement = new SearchResult(webElement);

    // Verify that all results contain the searched text
        String elementText = resultelement.getTitleofResult();
        assert1.assertTrue(elementText.toUpperCase().contains(productName1), "Test Results contains un-expected values: " + elementText);

    }
    // Search for product
    status = homePage.searchForProduct(productName2);
    assert1.assertFalse(status, "Invalid keywords also returned results");

    // Verify no search results are found
    assert1.assertTrue(homePage.isNoResultFound(), "Expected: no results , actual: Results were available");
    assert1.assertAll();
    Home home = new Home(driver);
    home.PerformLogout();

    }

    /*
    * Verify the presence of size chart and check if the size chart content is as
    * expected
    */
    @Test(description = "Verify the existence of size chart for certain items and validate contents of size chart", priority = 4, groups = {"Regression_Test"})
    @Parameters({"productName3"})
    public void TestCase04(String productName3) throws InterruptedException {
    boolean status = false;

    // Visit home page
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    // Search for product and get card content element of search results
    status = homePage.searchForProduct(productName3);
    List<WebElement> searchResults = homePage.getSearchResults();

    // Create expected values
    List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
    List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
    Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
    Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
    Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

    // Verify size chart presence and content matching for each search result
    for (WebElement webElement : searchResults) {
    SearchResult result = new SearchResult(webElement);

    // Verify if the size chart exists for the search result
    Assert.assertTrue(result.verifySizeChartExists(), "Size Chart link does not exist");
    Assert.assertTrue(result.verifyExistenceofSizeDropdown(driver), "Size DropDown is not Present");
    Assert.assertTrue(result.openSizechart(), "Test Case Fail. Failure to open Size Chart");
    Assert.assertTrue(result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
    driver), "Failure while validating contents of Size Chart Link");
    result.closeSizeChart(driver);
    }
    
    }

    /*
    * Verify the complete flow of checking out and placing order for products is working
    correctly
    */
    @Test(description = "Verify that a new user can add multiple products in to the cart and Checkout", priority = 5, groups = {"Sanity_test"})
    @Parameters({"user", "pass", "productName8", "productName4", "address"})
    public void TestCase05(String user, String pass, String productName8, String productName4, String address) throws InterruptedException {
    Boolean status;

    // Go to the Register page
    Register registration = new Register(driver);
    registration.navigateToRegisterPage();

    // Register a new user
    status = registration.registerUser(user, pass, true);
    Assert.assertTrue(status, "Failure to register new user");


    // Save the username of the newly registered user
    lastGeneratedUserName = registration.lastGeneratedUsername;

    // Go to the login page
    Login login = new Login(driver);
    login.navigateToLoginPage();

    // Login with the newly registered user's credentials
    status = login.PerformLogin(lastGeneratedUserName, pass);
    Assert.assertTrue(status, "User Perform Login Failed");


    // Go to the home page
    Home homePage = new Home(driver);
    homePage.navigateToHome();

    // Find required products by searching and add them to the user's cart
    status = homePage.searchForProduct("YONEX");
    homePage.addProductToCart(productName8);
    status = homePage.searchForProduct("Tan");
    homePage.addProductToCart(productName4);

    // Click on the checkout button
    homePage.clickCheckout();

    // Add a new address on the Checkout page and select it
    Checkout checkoutPage = new Checkout(driver);
    checkoutPage.addNewAddress(address);
    checkoutPage.selectAddress(address);

    // Place the order
    checkoutPage.placeOrder();

    WebDriverWait wait = new WebDriverWait(driver, 30);
    wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

    // Check if placing order redirected to the Thansk page
    status = driver.getCurrentUrl().endsWith("/thanks");
    Assert.assertTrue(status, "Failed to redirect to thanks page");

    // Go to the home page
    homePage.navigateToHome();

    // Log out the user
    homePage.PerformLogout();

    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test(description = "Verify that the contents of the cart can be edited", priority = 6, groups = {"Regression_Test"})
    @Parameters({"productName5", "productName6"})
    public void TestCase06(String productName5, String productName6) throws InterruptedException {
        Boolean status;
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User Perform Register Failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");


        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart("Xtend Smart Watch");

        status = homePage.searchForProduct("Yarine");
        homePage.addProductToCart("Yarine Floor Lamp");

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(productName5, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(productName6, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(productName5, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());

        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "Failed to redirect to thanks page");

        homePage.navigateToHome();
        homePage.PerformLogout();

    }

    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough", priority = 7, groups = {"Sanity_test"})
    @Parameters({"productName7", "Quantity"})
    public void TestCase07(String productName7, int Quantity) throws InterruptedException {
        Boolean status;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User Perform Registration Failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");


        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        homePage.addProductToCart(productName7);

        homePage.changeProductQuantityinCart(productName7, Quantity);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        Assert.assertTrue(status, "insufficient balance error message not thrown");
        homePage.navigateToHome();
        homePage.PerformLogout();

    }

    @Test(description = "Verify that a product added to a cart is available when a new tab is added", priority = 8, groups = {"Regression_Test"})
    @Parameters({""})
    public void TestCase08() throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User Perform Registration Failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");


        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        Assert.assertTrue(status, "Products added to cart not available in new Tab");

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        homePage.navigateToHome();
        homePage.PerformLogout();

    }

    @Test(description = "Verify that privacy policy and about us links are working fine", priority = 9, groups = {"Regression_Test"})
    @Parameters({"privacy", "TOS"})
    public void TestCase09(String privacy, String TOS) throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        SoftAssert assert2 = new SoftAssert();
        assert2.assertTrue(status, "User Perform Registration Failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assert2.assertTrue(status, "User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText(privacy)).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        assert2.assertTrue(status, "parent page url changed on clicking privacy policy link");


        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        assert2.assertTrue(status, "Verifying new tab opened has Privacy Policy page heading failed");

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText(TOS)).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals(TOS);
        assert2.assertTrue(status, "Verifying new tab opened has Terms Of Service page heading failed");

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        assert2.assertAll();
        homePage.PerformLogout();

    }

    @Test(description = "Verify that the contact us dialog works fine", priority = 10, groups = {"Sanity_test"})
    @Parameters({"name1", "email1", "message1"})
    public void TestCase10(String name1, String email1, String message1) throws InterruptedException {

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(name1);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(email1);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(message1);

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        SoftAssert assert3 = new SoftAssert();
        assert3.assertTrue(wait.until(ExpectedConditions.invisibilityOf(contactUs))
        , "contact us option is not working correctly");

    }

    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable", priority = 11, groups = {"Sanity_test", "Regression_Test"})
    @Parameters({"productName8", "address"})
    public void TestCase11(String productName8, String address) throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User Perform Registration Failed");
 
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(productName8);
        homePage.addProductToCart(productName8);
        homePage.changeProductQuantityinCart(productName8, 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        Assert.assertTrue(status, "All the 3 Advertisements are not available on webpage");

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        Assert.assertTrue(status, "Advertisement 1 is not clickable");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        Assert.assertTrue(status, "Advertisement 2 is not clickable");

    }


    @AfterSuite()
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());

            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

