package QKART_SANITY_LOGIN.Module1;

import java.sql.Driver;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
//import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResult {
    WebElement parentElement;
    //RemoteWebDriver driver;

    public SearchResult(WebElement SearchResultElement) {
        this.parentElement = SearchResultElement;
    }

    /*
     * Return title of the parentElement denoting the card content section of a
     * search result
     */
    public String getTitleofResult() throws InterruptedException {
        String titleOfSearchResult = "";
        // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
        // Find the element containing the title (product name) of the search result and
        WebElement elem = parentElement.findElement(By.xpath("//*[@id='root']/div/div/div[3]/div/div[2]/div/div/div[1]/p[1]"));
        titleOfSearchResult = elem.getText();
        Thread.sleep(5000);
        // assign the extract title text to titleOfSearchResult
        System.out.println(elem.getText());
        return titleOfSearchResult;
    }

    /*
     * Return Boolean denoting if the open size chart operation was successful
     */
    public Boolean openSizechart() {
        try {

            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // Find the link of size chart in the parentElement and click on it
            parentElement.findElement(By.xpath("//button[contains(text(),'Size chart')]")).click();
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the close size chart operation was successful
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Thread.sleep(2000);
            Actions action = new Actions(driver);

            // Clicking on "ESC" key closes the size chart modal
            action.sendKeys(Keys.ESCAPE);
            action.perform();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean based on if the size chart exists
     */
    public Boolean verifySizeChartExists() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            /*
             * Check if the size chart element exists. If it exists, check if the text of
             * the element is "SIZE CHART". If the text "SIZE CHART" matches for the
             * element, set status = true , else set to false
             */
            //System.out.println(parentElement);
            WebElement size = parentElement.findElement(By.tagName("Button"));
                //System.out.println("Checking 1 :-"+size.getText());
            if (size.isDisplayed()) {
                if (size.getText().equals("SIZE CHART")) {
                    status = true;
                }
                else {
                    status = false;
                }
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if the table headers and body of the size chart matches the
     * expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody,
            WebDriver driver) {
        Boolean status = true;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            /*
             * Locate the table element when the size chart modal is open             * 
             * 
             * Validate that the contents of expectedTableHeaders is present as the table
             * header in the same order
             * 
             * Validate that the contents of expectedTableBody are present in the table body
             * in the same order
             */
            WebElement parentTableElem = driver.findElement(By.tagName("table"));
            List<WebElement> tableHead = parentTableElem.findElement(By.tagName("thead")).findElement(By.tagName("tr")).findElements(By.tagName("th"));
            Thread.sleep(2000);
            String headerValues;
            //System.out.println(tableHead.get(2).getText());
            for (int i=0;i<expectedTableHeaders.size();i++) {
                headerValues = tableHead.get(i).getText();
                Thread.sleep(2000);
                if(!expectedTableHeaders.get(i).equals(headerValues)) {
                    Thread.sleep(2000);
                    System.out.println("Failure in Header comparison: Expected: " + expectedTableHeaders.get(i) + " Actuall: " + headerValues);

                    status = false;
                }
            }

            List<WebElement> tableBody = parentTableElem.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
            Thread.sleep(2000);
            for (int i=0;i<expectedTableBody.size();i++) {
                for (int j=1;j<=expectedTableBody.get(i).size();j++) {
                    Thread.sleep(2000);
                    if (!tableBody.get(i).findElement(By.xpath("td[" + j + "]"))
                    .getText().equals(expectedTableBody.get(i).get(j-1))) {
                        Thread.sleep(2000);
                        status = false;
                    }
                }
            }
            return status;

        } catch (Exception e) {
            System.out.println("Error while validating chart contents");
            return false;
        }
    }

    /*
     * Return Boolean based on if the Size drop down exists
     */
    public Boolean verifyExistenceofSizeDropdown(WebDriver driver) {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // If the size dropdown exists and is displayed return true, else return false
            if (parentElement.findElement(By.className("css-1gtikml")).isDisplayed()) {
                status = true;
            }
            else {
                status = false;
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }
}