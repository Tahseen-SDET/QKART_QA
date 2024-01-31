package QKART_SANITY_LOGIN.Module1;

import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Home {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    public Home(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHome() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    public Boolean PerformLogout() throws InterruptedException {
        try {
            // Find and click on the Logout Button
            WebElement logout_button = driver.findElement(By.className("MuiButton-text"));
            logout_button.click();

            // Wait for Logout to Complete
            Thread.sleep(3000);

            return true;
        } catch (Exception e) {
            // Error while logout
            return false;
        }
    }

    /*
     * Returns Boolean if searching for the given product name occurs without any errors
     */
    public Boolean searchForProduct(String product) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Clear the contents of the search box and Enter the product name in the search
            // box
            WebElement searchBox = driver.findElement(By.className("css-b52kj1"));
            searchBox.clear();
            searchBox.sendKeys(product);
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("Error while searching for a product: " + e.getMessage());
            return false;
        }
    }

    /*
     * Returns Array of Web Elements that are search results and return the same
     */
    public List<WebElement> getSearchResults() {
        List<WebElement> searchResults = new ArrayList<WebElement>() {};
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Find all webelements corresponding to the card content section of each of
            // search results
            searchResults = driver.findElements(By.className("css-s18byi"));
            return searchResults;
        } catch (Exception e) {
            System.out.println("There were no search results: " + e.getMessage());
            return searchResults;

        }
    }

    /*
     * Returns Boolean based on if the "No products found" text is displayed
     */
    public Boolean isNoResultFound() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
            // Check the presence of "No products found" text in the web page. Assign status
            // = true if the element is *displayed* else set status = false
            WebElement messg = driver
                    .findElement(By.xpath("//*[@id='root']/div/div/div[3]/div/div[2]/div/h4"));
            if (messg.getText().contains("No products found")) {
                status = true;
            } else {
                status = false;
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if add product to cart is successful
     */
    public Boolean addProductToCart(String productName) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            /*
             * Iterate through each product on the page to find the WebElement corresponding to the
             * matching productName
             * 
             * Click on the "ADD TO CART" button for that element
             * 
             * Return true if these operations succeeds
             */
            List<WebElement> wholeTile = driver.findElements(By.xpath(
                    "//div[@class='MuiPaper-root MuiPaper-elevation MuiPaper-rounded MuiPaper-elevation1 MuiCard-root card css-s18byi']"));
            for (WebElement elem : wholeTile) {
                if (elem.findElement(By.xpath(".//div//p[1]")).getText().contains(productName)) {
                    Thread.sleep(2000);
                    elem.findElement(By.tagName("Button")).click();
                    Thread.sleep(2000);
                    return false;
                }
            }
            System.out.println("Unable to find the given product");
            return false;
        } catch (Exception e) {
            System.out.println("Exception while performing add to cart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting the status of clicking on the checkout button
     */
    public Boolean clickCheckout() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find and click on the the Checkout button
            WebElement checkOut = driver.findElement(By.className("css-177pwqq"));
            checkOut.click();
            return status;
        } catch (Exception e) {
            System.out.println("Exception while clicking on Checkout: " + e.getMessage());
            return status;
        }
    }

    /*
     * Return Boolean denoting the status of change quantity of product in cart operation
     */
    public Boolean changeProductQuantityinCart(String productName, int quantity) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 06: MILESTONE 5

            // Find the item on the cart with the matching productName

            List<WebElement> cartItems =
                    driver.findElements(By.xpath("//div[@class='MuiBox-root css-1gjj37g']"));
            for (int i = 0; i < cartItems.size(); i++) {
                WebElement cartItem = cartItems.get(i);
                WebElement titleElement = cartItem.findElement(By.xpath("./div[1]"));
                String title = titleElement.getText();
                System.out.println(title + "*-*-*-*-*-*" + productName);
                if (title.contains(productName)) {
                    while (true) {
                        WebElement actuallQuantityWebelement =
                                cartItem.findElement(By.xpath(".//div[@data-testid='item-qty']"));
                        String actuallQty = actuallQuantityWebelement.getText();
                        int actuallQuantity = Integer.parseInt(actuallQty);
                        if (quantity == actuallQuantity) {
                            break;
                        }
                        if (quantity > actuallQuantity) {
                            WebElement plusElement = cartItem
                                    .findElement(By.xpath(".//*[@data-testid='AddOutlinedIcon']"));
                            plusElement.click();
                        } else if (quantity < actuallQuantity) {
                            WebElement minusElement = cartItem.findElement(
                                    By.xpath(".//*[@data-testid='RemoveOutlinedIcon']"));
                            minusElement.click();
                        } else if (quantity == actuallQuantity) {
                            break;
                        }
                        Thread.sleep(2000);


                    }
                }
            }
            // WebElement clickOnMinus =
            // elem.findElement(By.xpath(".//*[@data-testid='RemoveOutlinedIcon']"));
            // int currentQty = Integer.parseInt(
            // elem.findElement(By.xpath(".//div[@data-testid='item-qty']")).getText());
            // WebElement clickOnPlus =
            // elem.findElement(By.xpath(".//*[@data-testid='AddOutlinedIcon']"));
            // WebElement cartProducts = elem.findElement(By.xpath("./div[1]"));
            // String product = cartProducts.getText();
            // if (product.contains(productName)) {
            // Thread.sleep(2000);
            // if (currentQty < quantity) {
            // for (int i = 0; i < (quantity - currentQty); i++) {
            // clickOnPlus.click();
            // Thread.sleep(2000);
            // }
            // } else if (currentQty > quantity) {
            // for (int j = 0; j < (currentQty - quantity); j++) {
            // clickOnMinus.click();
            // Thread.sleep(2000);
            // }
            // } else {
            // break;
            // }
            // }
            // }
            //
            //

            // Increment or decrement the quantity of the matching product until the current
            // quantity is reached (Note: Keep a look out when then input quantity is 0,
            // here we need to remove the item completely from the cart)


            return true;
        } catch (Exception e) {
            if (quantity == 0)
                return true;
            System.out.println("exception occurred when updating cart: " + e.getMessage());
            return false;
        }
    }

    public Boolean verifyProductAtCheckOut(String productName) {
        try {
            WebElement itemAtCheckout = driver.findElement(
                    By.xpath("//*[@id='root']/div/div[2]/div[2]/div[1]/div[1]/div/div[2]/div[1]"));

            if (itemAtCheckout.getText().contains(productName)) {
                System.out.println("herre");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out
                    .println("Exception while verifying items on Checkout page: " + e.getMessage());
            return false;
        }


    }

    public Boolean orderConfirmation(String confirmed) {
        try {
            if (driver.getCurrentUrl().contains(confirmed)) {

            }
            return true;
        } catch (Exception e) {
            System.out.println(
                    "Exception while verifying the confirmed order url: " + e.getMessage());
            return false;
        }



    }



    /*
     * Return Boolean denoting if the cart contains items as expected
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));

            ArrayList<String> actualCartContents = new ArrayList<String>() {};
            for (WebElement cartItem : cartContents) {
                actualCartContents.add(
                        cartItem.findElement(By.className("css-1gjj37g")).getText().split("\n")[0]);
            }

            for (String expected : expectedCartContents) {
                if (!actualCartContents.contains(expected)) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("Exception while verifying cart contents: " + e.getMessage());
            return false;
        }
    }
}
