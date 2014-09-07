package com.trycucumber.systemtests.framework;

import com.opera.core.systems.OperaDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Important Factory class to implement factory of all Major Browser drivers.
 * User: Atish Narlawar
 */

@Component
public class DriverFactory implements FactoryBean<WebDriver> {

    /**
     * The Server url.
     */
    @Value("${server.host}")
    private String serverUrl;

    /**
     * The Server port.
     */
    @Value("${server.port}")
    private int serverPort;

    /**
     * The Saucelabs username.
     */
    @Value("${saucelabs.username}")
    private String saucelabsUsername;

    /**
     * The Saucelabs key.
     */
    @Value("${saucelabs.key}")
    private String saucelabsKey;

    /**
     * The enum Browser type.
     */
    private static enum BrowserType {
        /**
         * The FIREFOX.
         */FIREFOX("firefox"),
        /**
         * The IE.
         */IE("ie"),
        /**
         * The HEADLESS.
         */HEADLESS("headless"),
        /**
         * The OPERA.
         */OPERA("opera"),
        /**
         * The SAFARI.
         */SAFARI("safari"),
        /**
         * The CHROME.
         */CHROME("google-chrome"),
        /**
         * The PHANTOMJS.
         */PHANTOMJS("phantomjs"),

        /**
         * The RWD_CHROME.
         */RWD_CHROME("rwdchrome"),
        /**
         * The RWD_PHANTOMJS.
         */RWD_PHANTOMJS("rwdphantomjs"),
        /**
         * The RWD_FIREFOX.
         */RWD_FIREFOX("rwdfirefox"),

        /**
         * The RWD_IE8_VM.
         */RWD_IE8_VM("rwdie8vm"),

        /**
         * The saucelabs browsertype
         */
        SAUCELABS("sauceLabs");

        /**
         * The Browser type.
         */
        private final String browserType;

        /**
         * Instantiates a new Browser type.
         *
         * @param browserType the browser type
         */
        BrowserType(final String browserType) {
            this.browserType = browserType;
        }

        /**
         * Gets browser type string.
         *
         * @return the browser type string
         */
        public String getBrowserTypeString() {
            return browserType;
        }
    }

    /**
     * The Browser type.
     */
    private final BrowserType browserType;

    /**
     * Instantiates a new Driver factory.
     *
     * @param browserType the browser type
     */
    @Autowired
    public DriverFactory(@Value(value = "${browser.type}") String browserType) {

        checkNotNull(browserType);
        for (BrowserType browserTypeEnum : BrowserType.values()) {
            if (StringUtils.equalsIgnoreCase(browserTypeEnum.getBrowserTypeString(), browserType)) {
                this.browserType = browserTypeEnum;
                return;
            }
        }
        throw new IllegalArgumentException("Invalid browser type set in properties " + browserType);
    }


    /**
     * Gets object.
     *
     * @return the object
     *
     * @throws Exception the exception
     */
    public WebDriver getObject() throws Exception {

        final WebDriver webDriver;
        DesiredCapabilities capabilities;

        switch (browserType) {
            case FIREFOX:
                FirefoxProfile profile = new FirefoxProfile();
                profile.setAssumeUntrustedCertificateIssuer(false);
                webDriver = new FirefoxDriver(profile);
                break;
            case HEADLESS:
                webDriver = new HtmlUnitDriver();
                break;
            case IE:
                webDriver = new InternetExplorerDriver();
                break;
            case OPERA:
                webDriver = new OperaDriver();
                break;
            case SAFARI:
                webDriver = new SafariDriver();
                break;
            case CHROME:
                webDriver = new ChromeDriver();
                break;
            case PHANTOMJS:
                //TODO: KER figure out how to configure this correct
                capabilities = DesiredCapabilities.phantomjs();
                webDriver = new PhantomJSDriver(capabilities);
                break;
            case RWD_CHROME:
                capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability("webdriver.chrome.driver", "~/Documents/chromedriver");

                webDriver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);

                break;
            case RWD_PHANTOMJS:
                capabilities = DesiredCapabilities.phantomjs();
                capabilities.setCapability("phantomjs.binary.path", "/usr/bin/phantomjs");

                webDriver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
                break;
            case RWD_FIREFOX:
                capabilities = DesiredCapabilities.firefox();

                webDriver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
                break;

            case RWD_IE8_VM:
                capabilities = DesiredCapabilities.firefox();
                //capabilities.setCapability("webdriver.ie.driver", "C:\\selenium\\IEDriverServer.exe");

                webDriver = new RemoteWebDriver(new URL("http://10.10.22.113:4444/wd/hub"), capabilities);
                break;

            case SAUCELABS:
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability("name", "Lexus Selenium");
                webDriver = new RemoteWebDriver(
                        new URL("http://" + saucelabsUsername + ":" + saucelabsKey + "@ondemand.saucelabs.com:80/wd/hub"),
                        capabilities);
                break;

            default:
                throw new IllegalArgumentException("Invalid browser type set in class injection " + browserType.getBrowserTypeString());
        }

        initWebDriver(webDriver);
        return webDriver;
    }

    /**
     * Init web driver.
     *
     * @param webDriver the web driver
     */
    private void initWebDriver(WebDriver webDriver) {
        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    /**
     * Gets object type.
     *
     * @return the object type
     */
    public Class<?> getObjectType() {
        return WebDriver.class;
    }

    /**
     * Is singleton.
     *
     * @return the boolean
     */
    public boolean isSingleton() {
        return false;
    }

}
