package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static WebDriver driver;
    private static ExtentTest test;
    private static ExtentReports extent;
    private static JavascriptExecutor js;
    private static WebDriverWait wait;
    public static void main(String[] args) throws InterruptedException, IOException {
        try {
            //System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/drivers/chromedriver.exe");
            WebDriverManager.chromedriver().clearDriverCache().driverVersion("128.0.6613.114").setup();
            driver = new ChromeDriver();
            setup();
            test = extent.createTest("Selenium Demo Automation", "Login Functionality");
            //driver.close();
            login("testerveritest@test.com", "veritest");
            setDefaultBillingAddress();
            driver.findElement(By.linkText("WOMEN")).click();
            addItemToCart();
            driver.findElement(By.linkText("MEN")).click();
            addItemToCart();
            proceedToCheckout();
            printOrder();
            System.out.println("Test Completed");
        }
        catch(NoSuchElementException e){
            test.fatal("Element not found: " + e.toString(), MediaEntityBuilder
                    .createScreenCaptureFromBase64String(captureScreenshot()).build());
        }
        catch(ElementClickInterceptedException e){
            test.fatal("Element click might have been intercepted by the presence of another element (eg. the bottom information bar) " + e.toString(), MediaEntityBuilder
                    .createScreenCaptureFromBase64String(captureScreenshot()).build());
        }
        catch (AssertionError e){
            test.fail(e.toString(), MediaEntityBuilder
                    .createScreenCaptureFromBase64String(captureScreenshot()).build());
        }
        catch(Exception e){
            test.fatal(e.toString(), MediaEntityBuilder
                    .createScreenCaptureFromBase64String(captureScreenshot()).build());
        }
        finally {
            driver.quit();
            extent.flush();
        }
    }

    public static void setup(){
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        ExtentHtmlReporter reporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\reports\\ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Test", "SWS - Automation Testing");
        reporter.config().setDocumentTitle("Automation Execution Results");
        reporter.config().setReportName("Automation Execution Result");
        reporter.config().setTheme(Theme.STANDARD);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, 10);
    }

    public static String captureScreenshot(){
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        System.out.println("Screenshot Taken succesfully");
        return screenshot.getScreenshotAs(OutputType.BASE64);
    }

    public static void setDefaultBillingAddress() throws IOException {
        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
        driver.findElement(By.linkText("EDIT ADDRESS")).click();
        js.executeScript("window.scrollBy(0, 500);");
        driver.findElement(By.id("telephone")).sendKeys("7777777");
        js.executeScript("window.scrollBy(0, 900);");
        driver.findElement(By.id("street_1")).sendKeys("street1");
        driver.findElement(By.id("city")).sendKeys("Toledo");
        Select dropdown = new Select(driver.findElement(By.id("region_id")));
        dropdown.selectByVisibleText("Ohio");
        driver.findElement(By.id("zip")).sendKeys("55555");
        test.info("Filled in Billing Address Details", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
        driver.findElement(By.xpath("//*[@id=\"form-validate\"]/div[3]/button/span/span")).click();
        String message = driver.findElement(By.xpath("//*[@class = \"success-msg\"]/ul/li/span")).getText();
        Assert.assertTrue("Failed to add Billing Address",message.equalsIgnoreCase("The address has been saved."));
        test.pass("Added Billing Address Details", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
    }

    public static void addItemToCart() throws IOException {
        WebElement viewDetails = driver.findElement(By.linkText("VIEW DETAILS"));
        js.executeScript("window.scrollBy(0, 500);");
        viewDetails.click();
        driver.findElement(By.xpath("//ul[@id = \"configurable_swatch_color\"]/li[1]/a")).click();
        driver.findElement(By.xpath("//ul[@id = \"configurable_swatch_size\"]/li[1]/a")).click();
        js.executeScript("window.scrollBy(0, 200);");
        WebElement addToCartButton = driver.findElement(By.xpath("//*[@id=\"product_addtocart_form\"]/div[3]/div[6]/div[2]/div[2]/button"));
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        addToCartButton.click();
        String message = driver.findElement(By.xpath("//*[@class = \"success-msg\"]/ul/li/span")).getText();
        Assert.assertTrue("Failed to add to cart",message.contains("added to your shopping cart"));
        test.pass("Added item to cart", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
    }

    public static void proceedToCheckout() throws IOException {
        driver.findElement(By.xpath("//button[@title = \"Proceed to Checkout\"]")).click();
        driver.findElement(By.id("billing:use_for_shipping_yes")).click();
        test.pass("Selected billing address as current", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
        driver.findElement(By.xpath("//button[@title = \"Continue\"]")).click();
        driver.findElement(By.id("s_method_flatrate_flatrate")).click();
        js.executeScript("window.scrollBy(0, 200);");
        test.pass("Selected Flat Rate", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
        driver.findElement(By.xpath("//*[@id=\"shipping-method-buttons-container\"]/button")).click();
        driver.findElement(By.xpath("//*[@id=\"payment-buttons-container\"]/button/span/span")).click();
        WebElement placeOrderButton = driver.findElement(By.xpath("//button[@title = \"Place Order\"]"));
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
        placeOrderButton.click();
        String orderCompleteMessage = driver.findElement(By.xpath("//*[@id=\"map-popup\"]/following-sibling::p")).getText();
        Assert.assertTrue("Failed to complete order",orderCompleteMessage.contains("Your order # is:"));
        test.pass("Completed the order", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
    }

    public static void printOrder() throws IOException, InterruptedException {
        String mainWindow = driver.getWindowHandle();
        System.out.println(mainWindow);
        driver.findElement(By.linkText("here to print")).click();
        Thread.sleep(3000);
        Set<String> windowHandles = driver.getWindowHandles();
        for (String childWindow : windowHandles) {
            if (!mainWindow.equalsIgnoreCase(childWindow)) {
                driver.switchTo().window(childWindow);
                System.out.println(childWindow);
                String titleChildWindow = driver.getTitle();
                test.info("Heading of print receipt tab is " + titleChildWindow, MediaEntityBuilder
                        .createScreenCaptureFromBase64String(captureScreenshot()).build());
            }
        }
        driver.switchTo().window(mainWindow);
        test.info("Switched Back to main window", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
    }
    public static void login(String username, String pass) throws InterruptedException, IOException {
        driver.get("https://ecommerce.tealiumdemo.com");
        String title = driver.getTitle();
        System.out.println("Hello " + title);
        driver.manage().window().maximize();
        WebElement account = driver.findElement(By.xpath("//*[@data-target-element = \"#header-account\"]"));
        account.click();
        test.info("Clicked on Account link", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
        System.out.println("Acccount clicked");
        WebElement loginButton = driver.findElement(By.xpath("//*[@title = \"Log In\"]"));
        loginButton.click();
        WebElement emailTextBox = driver.findElement(By.id("email"));
        WebElement passwordTextBox = driver.findElement(By.id("pass"));
        test.info("Entered Credentials", MediaEntityBuilder
                .createScreenCaptureFromBase64String(captureScreenshot()).build());
        WebElement submitButton = driver.findElement(By.id("send2"));

        emailTextBox.sendKeys(username);
        passwordTextBox.sendKeys(pass);
        js.executeScript("arguments[0].scrollIntoView();", submitButton);
        submitButton.click();
        Thread.sleep(3000);
        String welcomeMessage = driver.findElement(By.className("welcome-msg")).getText();
        if (welcomeMessage.equalsIgnoreCase("WELCOME, VERISK NEPAL!")){
            System.out.println("Login Succesful, Test Passed");
            test.info("Login Succesful, Test Passed", MediaEntityBuilder
                    .createScreenCaptureFromBase64String(captureScreenshot()).build());
        }
        else{
            System.out.println("Login Failed, Test Failed");
            test.info("Login Failed, Test Failed", MediaEntityBuilder
                    .createScreenCaptureFromBase64String(captureScreenshot()).build());
        }
    }
}