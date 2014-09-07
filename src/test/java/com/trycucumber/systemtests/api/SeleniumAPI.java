package com.trycucumber.systemtests.api;


import com.opera.core.systems.OperaDriver;
import com.trycucumber.systemtests.framework.BrowserSize;
import com.trycucumber.systemtests.framework.DriverFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.log4testng.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Selenium Utility class provides all the common usable functionality in Selenium testing.
 * User : Atish Narlawar
 */
@Component
public class SeleniumAPI {

    /**
     * The Path to screen shot.
     */
    @Value("${server.screenshot.path}")
    private String pathToScreenshot;

    /**
     * The Relative screen shot path.
     */
    @Value("${server.screenshot.relative.path}")
    private String relativeScreenshotPath;

    /**
     * The Driver.
     */
    protected WebDriver driver;

    /**
     * The Web driver wait.
     */
    protected WebDriverWait webDriverWait;

    /**
     * The Driver factory.
     */
    @Autowired
    private DriverFactory driverFactory;

    /**
     * The constant logger.
     */
    private static final Logger logger = Logger.getLogger(SeleniumAPI.class);

    /**
     * Resize browser window.
     *
     * @param driver        the driver
     * @param browserWidth  the browser width
     * @param browserHeight the browser height
     */
    public void resizeBrowserWindow(WebDriver driver, Integer browserWidth, Integer browserHeight) {

        driver.manage().window().setSize(new Dimension(browserWidth, browserHeight));


    }


    /**
     * Resize browser.
     *
     * @param driver      the driver
     * @param browserSize the browser size
     */
    public void resizeBrowser(WebDriver driver, BrowserSize browserSize) {
        driver.manage().window().setSize(browserSize.getDimension());


    }


    /**
     * Implicit timeouts
     */
    public void implicit() {
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
    }

    /**
     * Gets browser width.
     *
     * @param driver the driver
     *
     * @return the browser width
     */
    public int getBrowserWidth(WebDriver driver) {

        Dimension d = driver.manage().window().getSize();

        return d.getWidth();

    }


    /**
     * Create screen shot dir.
     *
     * @throws IOException the iO exception
     */
    private void createScreenShotDir() throws IOException {
        FileUtils.forceMkdir(new File(pathToScreenshot + "/target/screenshots/"));
    }

    /**
     * Gets screen shot.
     *
     * @param driver   the driver
     * @param filename the filename
     *
     * @return the screen shot
     *
     * @throws IOException the iO exception
     */
    public File getScreenShot(WebDriver driver, String filename) throws IOException {

        File imageFile = null;
        try {
            createScreenShotDir();

            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            String path = pathToScreenshot + relativeScreenshotPath + filename + scrFile.getName();
            imageFile = new File(path);
            FileUtils.copyFile(scrFile, imageFile);
            logger.debug("We are taking screenshots!");
        } catch (Exception ex) {
            // Any exception in Screen shot should be eaten here. It should not hamper selenium tests.
            logger.debug("Something went wrong getting a screenshots");
        }

        return imageFile;
    }

    /**
     * Gets attribute value.
     *
     * @param pageElement   the page element
     * @param attributeName the attribute name
     *
     * @return the attribute value
     */
    public Object getAttributeValue(WebElement pageElement, String attributeName) {
        if (attributeName.equals("isVisible")) {
            return pageElement.isDisplayed();
        } else if (attributeName.equals("width")) {
            return pageElement.getSize().getWidth();
        } else if (attributeName.equals("color")) {
            return pageElement.getCssValue("color");
        } else if (attributeName.equals("height")) {
            return pageElement.getSize().getHeight();
        } else if (attributeName.equals("width")) {
            return pageElement.getSize().getWidth();
        } else if (attributeName.equals("posX")) {
            return pageElement.getLocation().getX();
        } else if (attributeName.equals("posY")) {
            return pageElement.getLocation().getY();
        } else if (attributeName.equals("opacity")) {
            return pageElement.getCssValue("opacity");
        } else if (attributeName.equals("borderWidth")) {
            return pageElement.getCssValue("border-width");
        } else if (attributeName.equals("borderColor")) {
            return pageElement.getCssValue("border-color");
        } else if (attributeName.equals("src")) {
            return pageElement.getAttribute("src");
        } else if (attributeName.equals("alt")) {
            return pageElement.getAttribute("alt");
        } else if (attributeName.equals("liCount")) {
            return pageElement.findElements(By.tagName("li")).size();
        } else if (attributeName.equals("href")) {
            return pageElement.getAttribute("href");
        } else {
            return StringUtils.EMPTY;
        }

    }

    /**
     * Gets tangible elements.
     *
     * @return the tangible elements
     */
    public List<WebElement> getTangibleElements() {
        List<WebElement> visibleElements = getVisibleElements();

        List<WebElement> tangibleElements = new LinkedList<WebElement>();

        Iterator<WebElement> visibleElementsIt = visibleElements.iterator();

        while (visibleElementsIt.hasNext()) {

            WebElement pageElement = visibleElementsIt.next();

            if (!pageElement.getTagName().equals("html") && !pageElement.getTagName().equals("body") && !pageElement.getTagName().equals("nav") && !pageElement.getTagName().equals("ul")) {
                if (pageElement.getSize().getWidth() > 0 && pageElement.getSize().getHeight() > 0) {
                    tangibleElements.add(pageElement);
                }
            }

        }

        return tangibleElements;
    }

    /**
     * Gets visible elements.
     *
     * @return the visible elements
     */
    public List<WebElement> getVisibleElements() {
        List<WebElement> pageElements = driver.findElements(By.xpath("//*"));

        List<WebElement> visibleElements = new LinkedList<WebElement>();

        Iterator<WebElement> pageElementsIt = pageElements.iterator();

        while (pageElementsIt.hasNext()) {

            WebElement pageElement = pageElementsIt.next();

            if (pageElement.isDisplayed()) {
                visibleElements.add(pageElement);
            }

        }

        return visibleElements;
    }

    /**
     * Gets element x path.
     *
     * @param element the element
     *
     * @return the element x path
     */
    public String getElementXPath(WebElement element) {
        String elementClass = element.getAttribute("class");
        String elementId = element.getAttribute("id");
        String elementText = element.getText();
        String elementTag = element.getTagName();

        String elementXPath;
        Boolean elementXPathComma = false;

        if (elementClass.contains("section")) {
            elementXPath = "//";
        } else {
            elementXPath = getElementXPath(element.findElement(By.xpath("/parent::*"))) + "/";
        }
        elementXPath = elementXPath + elementTag;

        if (!elementClass.equals("") || !elementId.equals("") || !elementText.equals("")) {
            elementXPath = elementXPath + "[";

            if (!elementClass.equals("")) {
                elementXPath = elementXPath + "@class='" + elementClass + "'";
                elementXPathComma = true;
            }

            if (!elementId.equals("")) {
                if (elementXPathComma) {
                    elementXPath = elementXPath + " and ";
                }

                elementXPath = elementXPath + "@id='" + elementClass + "'";
                elementXPathComma = true;
            }

            if (!elementText.equals("") && !elementXPathComma) {
                //                if (elementXPathComma) {
                //                    elementXPath = elementXPath + " and ";
                //                }

                elementXPath = elementXPath + "text()='" + elementText + "'";
                elementXPathComma = true;
            }

            elementXPath = elementXPath + "]";
        }

        return elementXPath;

    }

    /**
     * Find broken images.
     *
     * @return the set
     */
    public Set<String> findBrokenImages() {
        List<WebElement> imagesList = driver.findElements(By.tagName("img"));

        Set<String> brokenImages = new LinkedHashSet<String>();

        for (WebElement image : imagesList) {
            try {

                HttpClient httpclient = HttpClientBuilder.create().build();
                HttpResponse response = httpclient.execute(new HttpGet(image.getAttribute("src")));
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    brokenImages.add(image.getAttribute("src"));
                }
            } catch (IOException ioe) {
                logger.warn("Bad image " + image);
            }
        }

        return brokenImages;

    }

    /**
     * Log element state for all resolutions.
     *
     * @param pageElements the page elements
     * @param testName     the test name
     * @param testState    the test state
     */
    public void logElementStateForAllResolutions(Set<String> pageElements, String testName, String testState) {
        //todo configure resolutions somewhere else

        for (int i = 400; i <= 1200; i += 200) { //todo make these not hard-coded
            resizeBrowserWindow(driver, i, 640);
            logElementState(driver, pageElements, testName, testState);

            resizeBrowserWindow(driver, i, 1200);
            logElementState(driver, pageElements, testName, testState);
        }
    }

    /**
     * Log element state.
     *
     * @param driver       the driver
     * @param pageElements the page elements
     * @param testName     the test name
     * @param testState    the test state
     */
    public void logElementState(WebDriver driver, Set<String> pageElements, String testName, String testState) {
        int browserWidth = getBrowserWidth(driver);

        //Connection connection = null;

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);

        Iterator<String> pageElementsIt = pageElements.iterator();

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sqlTimestamp = sdf.format(now);

        String sqlOutput = new String();
        String javaOutput = new String();

        Integer elementCounter = 0;

        while (pageElementsIt.hasNext()) {

            String pageElementXPath = pageElementsIt.next();

            elementCounter = elementCounter + 1;

            HashMap peAttributes = new HashMap();
            HashMap peLogFields = new HashMap();

            //todo uncomment this and add handling for elements that don't exist.
            //this snippet is currently only used for generating a list of working elements
            if (driver.findElements(By.xpath(pageElementXPath)).size() == 0) {
                peAttributes.put("isPresent", "false");
                peLogFields.put("element_visible", "false");

            } else {
                WebElement pageElement = driver.findElement(By.xpath(pageElementXPath));

                //String pageElementXPath = pageElementsIt.next();


                //            javaOutput = javaOutput + "            WebElement autoElement" + elementCounter.toString() + " = driver.findElement(By.xpath(\"" + pageElementXPath + "\"));\n";
                //
                //            javaOutput = javaOutput + "               if (seleniumUtil.getBrowserWidth(driver) == 400 && seleniumUtil.getBrowserHeight(driver) == 600) {\n";


                peAttributes.put("isPresent", "true");

                peAttributes.put("isVisible", pageElement.isDisplayed());
                peAttributes.put("color", pageElement.getCssValue("color"));
                peAttributes.put("height", pageElement.getSize().getHeight());
                peAttributes.put("width", pageElement.getSize().getWidth());
                peAttributes.put("posX", pageElement.getLocation().getX());
                peAttributes.put("posY", pageElement.getLocation().getY());
                peAttributes.put("opacity", pageElement.getCssValue("opacity"));

                peAttributes.put("borderWidth", pageElement.getCssValue("border-width"));

                peLogFields.put("element_tag", pageElement.getTagName());

                // element's visibility is added to every row for easier reporting exclusion
                peLogFields.put("element_visible", pageElement.isDisplayed());

                // don't log the border color if the border width is 0
                if (pageElement.getCssValue("border-width") != "0") {
                    peAttributes.put("borderColor", pageElement.getCssValue("border-color"));
                }

                if (pageElement.getTagName().equals("img")) {
                    peAttributes.put("src", pageElement.getAttribute("src"));
                    peAttributes.put("alt", pageElement.getAttribute("alt"));
                }

                if (pageElement.getTagName().equals("li") || pageElement.getTagName().equals("ol")) {
                    peAttributes.put("liCount", pageElement.findElements(By.tagName("li")).size());
                }

                if (pageElement.getTagName().equals("a")) {
                    peAttributes.put("href", pageElement.getAttribute("href"));
                }
            }


            peLogFields.put("browser_width", driver.manage().window().getSize().getWidth());
            peLogFields.put("browser_height", driver.manage().window().getSize().getHeight());
            peLogFields.put("element_xpath", pageElementXPath);
            peLogFields.put("test_name", testName); //todo get test name from class
            peLogFields.put("test_state", testState); //todo sanitize string?
            peLogFields.put("test_run_id", "12345"); //todo generate test run id

            peLogFields.put("log_time", sqlTimestamp);

            // todo replace this with currentBrowser
            if (driver instanceof ChromeDriver) {
                peLogFields.put("browser", "chrome");
            } else if (driver instanceof FirefoxDriver) {
                peLogFields.put("browser", "firefox");
            } else if (driver instanceof SafariDriver) {
                peLogFields.put("browser", "safari");
            } else if (driver instanceof InternetExplorerDriver) {
                peLogFields.put("browser", "internetexplorer"); //todo get the version too
            } else if (driver instanceof OperaDriver) {
                peLogFields.put("browser", "opera"); //todo get the version too
            } else if (driver instanceof RemoteWebDriver) {
                peLogFields.put("browser", "chrome"); //todo somehow get the browser name using getCapabilities even though only RemoteWebDriver supports it -ig
            } else {
                logger.debug("this shouldn't happen"); //todo make this not happen
            }

            Iterator peAttributesIt = peAttributes.entrySet().iterator();

            while (peAttributesIt.hasNext()) {
                Map.Entry peAttribute = (Map.Entry) peAttributesIt.next();
                HashMap insertFields = new HashMap();

                insertFields.putAll(peLogFields);

                insertFields.put("attribute_name", peAttribute.getKey());
                insertFields.put("attribute_value", peAttribute.getValue());

                Iterator insertFieldsIt = insertFields.entrySet().iterator();

                String sqlFieldNames = new String();
                String sqlFieldValues = new String();

                while (insertFieldsIt.hasNext()) {
                    Map.Entry insertField = (Map.Entry) insertFieldsIt.next();

                    if (sqlFieldNames.equals("")) {
                        sqlFieldNames += insertField.getKey();
                        sqlFieldValues += "\"" + insertField.getValue() + "\"";
                    } else {
                        sqlFieldNames += "," + insertField.getKey();
                        sqlFieldValues += ",\"" + insertField.getValue() + "\"";
                    }
                }

                sqlOutput = sqlOutput +
                        "INSERT INTO testlog (" +
                        sqlFieldNames +
                        ") VALUES (" +
                        sqlFieldValues +
                        ");\n";

                //////////////////////////////////////////////////////////

                String xpathHash = DigestUtils.md5Hex(pageElementXPath);
                String crumbLogPath = "crumbdb/" + peLogFields.get("browser") + "/" + testName + "/" + testState + "/" + peLogFields.get("browser_width") + "x" + peLogFields.get("browser_height") + "/" + xpathHash;
                File crumbLogFile = new File(crumbLogPath);

                if (crumbLogFile.mkdirs()) {
                }


                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(new File(crumbLogFile + "/" + peAttribute.getKey() + ".txt")))));

                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        line = StringUtils.strip(line, "\n");


                        if (StringUtils.trim(peAttribute.getValue().toString()).equals(StringUtils.trim(line))) {
                            //System.err.println("OK: " + pageElementXPath + "/" + peAttribute.getKey() + " = " + peAttribute.getValue());
                        } else {
                            System.err.println("WARNING: " + pageElementXPath + "/" + peAttribute.getKey() + " = " + peAttribute.getValue() + "(was " + line + ")");
                        }

                        break;
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("NEW: " + pageElementXPath + "/" + peAttribute.getKey() + " = " + peAttribute.getValue());

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                //System.err.println(crumbLogPath + "/" + peAttribute.getKey());


                try {
                    PrintWriter out = new PrintWriter(new FileOutputStream(crumbLogFile + "/" + peAttribute.getKey() + ".txt", true));

                    out.println(peAttribute.getValue());
                    out.close();
                    out = null;

                } catch (Exception e) {
                    System.err.println("oops, couldn't write crumblog file!");
                }

            }
        }

        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(new File("log-sql.txt"), true));

            out.println(sqlOutput);
            out.close();
            out = null;

            out = new PrintWriter(new FileOutputStream(new File("log-java.txt"), true));

            out.println(javaOutput);
            out.close();
            out = null;

        } catch (Exception e) {
            System.err.println("oops, couldn't write log file!");
        }
    }


    /**
     * Open page.
     *
     * @param pagePath the page path
     */
    public void openPage(String pagePath) {
        driver.get(pagePath);

    }


    /**
     * Find element by css.
     *
     * @param cssString the css string
     *
     * @return the web element
     */
    public WebElement findElementByCss(String cssString) {

        return driver.findElement(By.cssSelector(cssString));

    }

    /**
     * Find element by tag name.
     *
     * @param tagName the tag name
     *
     * @return the web element
     */
    public WebElement findElementByTagName(String tagName) {

        return driver.findElement(By.tagName(tagName));

    }

    /**
     * Find element by class name.
     *
     * @param className the class name
     *
     * @return the web element
     */
    public WebElement findElementByClassName(String className) {

        return driver.findElement(By.className(className));

    }


    /**
     * Find element by xpath.
     *
     * @param xpathExpression the xpath expression
     *
     * @return the web element
     */
    public WebElement findElementByXpath(String xpathExpression) {

        return driver.findElement(By.xpath(xpathExpression));

    }

    /**
     * Find element by link text.
     *
     * @param linkText the link text
     *
     * @return the web element
     */
    public WebElement findElementByLinkText(String linkText) {

        return driver.findElement(By.linkText(linkText));

    }


    /**
     * Find element by id.
     *
     * @param id the id
     *
     * @return the web element
     */
    public WebElement findElementById(String id) {

        return driver.findElement(By.id(id));

    }


    /**
     * Find element by css.
     *
     * @param cssString  the css string
     * @param webElement the web element
     *
     * @return the web element
     */
    public WebElement findElementByCss(String cssString, WebElement webElement) {

        if (webElement != null) {
            return webElement.findElement(By.cssSelector(cssString));
        }
        return null;
    }

    /**
     * Find element by class name.
     *
     * @param className  the class name
     * @param webElement the web element
     *
     * @return the web element
     */
    public WebElement findElementByClassName(String className, WebElement webElement) {

        if (webElement != null) {
            return webElement.findElement(By.className(className));
        }
        return null;
    }

    /**
     * Find element by id.
     *
     * @param id         the id
     * @param webElement the web element
     *
     * @return the web element
     */
    public WebElement findElementById(String id, WebElement webElement) {

        if (webElement != null) {
            return webElement.findElement(By.id(id));
        }
        return null;
    }


    /**
     * Find elements by css.
     *
     * @param cssString the css string
     *
     * @return the list
     */
    public List<WebElement> findElementsByCss(String cssString) {

        return driver.findElements(By.cssSelector(cssString));

    }


    /**
     * Find elements by class name.
     *
     * @param className the class name
     *
     * @return the list
     */
    public List<WebElement> findElementsByClassName(String className) {

        return driver.findElements(By.className(className));

    }

    /**
     * Find elements by id.
     *
     * @param id the id
     *
     * @return the list
     */
    public List<WebElement> findElementsById(String id) {

        return driver.findElements(By.id(id));


    }

    /**
     * Find elements by xpath.
     *
     * @param xpathExpression the xpath expression
     *
     * @return the list
     */
    public List<WebElement> findElementsByXpath(String xpathExpression) {

        return driver.findElements(By.xpath(xpathExpression));

    }


    /**
     * Gets screen shot.
     *
     * @param screenName the screen name
     */
    public void getScreenShot(String screenName) {
        try {
            getScreenShot(driver, screenName);
        } catch (Exception ex) {
            logger.error("Screen Shot functionality not working. Missing screen name " + screenName);
        }
    }


    /**
     * Start void.
     *
     * @throws Exception the exception
     */
    public void start() throws Exception {
        if (driver != null) {
            quit();
        }

        driver = driverFactory.getObject();
        webDriverWait = new WebDriverWait(driver, 2);

    }

    /**
     * Quit void.
     */
    public void quit() {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
        webDriverWait = null;
    }

    /**
     * Gets current url.
     *
     * @return the current url
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Wait for cilckale by css.
     *
     * @param cssString the css string
     */
    public void waitForCilckaleByCss(String cssString) {
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssString)));

    }

    public void waitAnElementByCSS(String cssString) {
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssString)));
        driver.findElements(By.cssSelector(cssString));

    }


    /**
     * Maximize window.
     */
    public void maximizeWindow() {
        driver.manage().window().maximize();
    }

    /**
     * Move to element and click.
     *
     * @param element the element
     */
    public void moveToElementAndClick(WebElement element) {
        new Actions(driver).moveToElement(element).click().build().perform();
    }

    /**
     * Move to element.
     *
     * @param element the element
     */
    public void moveToElement(WebElement element) {
        new Actions(driver).moveToElement(element).build().perform();
    }

    /**
     * Click void.
     *
     * @param element the element
     */
    public void click(WebElement element) {
        WebElement webElement = webDriverWait.until(ExpectedConditions.visibilityOf(element));
        if (webElement != null) {
            webElement.click();
        } else {
            assertNotNull("Element for click not found");
        }
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Resize to web.
     */
    public void resizeToWeb() {
        resizeBrowser(driver, BrowserSize.WEB);
    }

    /**
     * Resize to mobile.
     */
    public void resizeToMobile() {
        resizeBrowser(driver, BrowserSize.MOBILE);
    }

    /**
     * Resize to mobile.
     */
    public void resizeToPhantom() {
        resizeBrowser(driver, BrowserSize.PHANTOM);
    }

    /**
     * Resize to tablet.
     */
    public void resizeToTablet() {
        resizeBrowser(driver, BrowserSize.TABLET);
    }

    /**
     * Gets window handle.
     *
     * @return the window handle
     */
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }


    /**
     * Gets window handles.
     *
     * @return the window handles
     */
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }


    /**
     * Add cookie.
     *
     * @param name  the name
     * @param value the value
     */
    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        driver.manage().addCookie(cookie);
    }

    /**
     * Clear cookies.
     */
    public void clearCookies() {
        driver.manage().deleteAllCookies();
    }

    /**
     * Gets cookies.
     *
     * @return the cookies
     */
    public Set<Cookie> getCookies() {
        return driver.manage().getCookies();
    }

    /**
     * Switch to.
     *
     * @param handle the handle
     */
    public void switchTo(String handle) {
        driver.switchTo().window(handle);
    }

    /**
     * Close void.
     */
    public void close() {
        driver.close();
    }

    /**
     * Navigate back.
     */
    public void navigateBack() {
        driver.navigate().back();
    }

    /**
     * Execute script.
     *
     * @param script     the script
     * @param webElement the web element
     *
     * @return the object
     */
    public Object executeScript(String script, WebElement webElement) {
        return ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].complete && " +
                        "typeof arguments[0].naturalWidth != \"undefined\" && " +
                        "arguments[0].naturalWidth > 0", webElement);
    }

    /**
     * Navigates user from Page popout Window
     */
    public void popOutWindow() {
        WebElement popOutWindow = driver.findElement(By.cssSelector("#colorbox"));

        while (popOutWindow.isDisplayed()) {
            driver.findElement(By.cssSelector("#outbound-link > ul > li:nth-child(2) > a")).click();
            break;
        }


    }

    /**
     * Header method to resize the browser as per viewPortSize.
     * @param viewportSize
     */
    public void Header(String viewportSize) {
        if (viewportSize.equals("Mobile")) {
            resizeBrowser(driver, BrowserSize.MOBILE);
            assertTrue(" Header is missing on Mobile", driver.findElement(By.cssSelector("div#mobileMenuButton.showMobile")).isDisplayed());
        } else if (viewportSize.equals("Tablet")) {
            resizeBrowser(driver, BrowserSize.TABLET);
            assertTrue(" Header is missing on Tablet", driver.findElement(By.cssSelector("div#mobileMenuButton.showMobile")).isDisplayed());
        } else if (viewportSize.equals("Desktop")) {
            resizeBrowser(driver, BrowserSize.WEB);
            assertTrue(" Header is missing on Desktop", driver.findElements(By.cssSelector("div#categoryNav.showDesktop ul#categories li")).size() != 0);
        }

    }

    /**
     * Footer method to resize the browser as per viewPortSize.
     *
     * @param viewportSize
     */
    public void Footer(String viewportSize) {
        if (viewportSize.equals("Mobile")) {
            resizeBrowser(driver, BrowserSize.MOBILE);
            assertTrue(" Footer is missing on Mobile", driver.findElement(By.xpath("/html/body/div/div/footer/div[2]")).isDisplayed());
        } else if (viewportSize.equals("Tablet")) {
            resizeBrowser(driver, BrowserSize.TABLET);
            assertTrue(" Footer is missing on Tablet", driver.findElement(By.cssSelector("footer .footer-wrapper")).isDisplayed());
        } else if (viewportSize.equals("Desktop")) {
            resizeBrowser(driver, BrowserSize.WEB);
            assertTrue(" Footer is missing on Desktop", driver.findElement(By.cssSelector("footer .footer-wrapper")).isDisplayed());
        }


    }


}
