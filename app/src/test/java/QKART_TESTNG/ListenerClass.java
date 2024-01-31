package QKART_TESTNG;
import java.io.File;
import java.sql.Driver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass extends QKART_Tests implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // System.out.println(driver);
        takeScreenshot(driver, "StartTestCase ", result.getName());

    }

    @Override
    public void onTestFailure(ITestResult result) {
        takeScreenshot(driver, "FailureScenario", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        takeScreenshot(driver, "SuccessScenario", result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        takeScreenshot(driver, "TestFinish", context.getName());
    }

}