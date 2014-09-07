package com.trycucumber.systemtests.pages.home;

import com.trycucumber.systemtests.api.SeleniumAPI;
import com.trycucumber.systemtests.framework.AbstractSeleniumTest;
import com.trycucumber.systemtests.util.UriManager;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.log4testng.Logger;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Cucumber selenium test class for HomePage.
 * Created by Atish Narlawar on 9/6/14.
 */
public class HomePageIT extends AbstractSeleniumTest {

    /**
     * The constant logger.
     */
    private static final Logger logger = Logger.getLogger(HomePageIT.class);


    /**
     * The Selenium API.
     */
    @Autowired
    private SeleniumAPI seleniumAPI;

    /**
     * Sets up Home Page Test.
     *
     * @throws Exception the exception
     */
    @Before("@HomePage")
    public void setUp() throws Exception {
        logger.debug("Opening up Home Page");
        seleniumAPI.start();
        seleniumAPI.implicit();
    }

    /**
     * Start happy path.
     *
     * @throws java.io.IOException the iO exception
     */
    @Given("User browse to Yahoo home page")
    public void startHappyPath() throws IOException {
        seleniumAPI.openPage(getPageUrl(UriManager.homepage()));
        assertTrue(StringUtils.endsWith(seleniumAPI.getCurrentUrl(), UriManager.homepage()));
        seleniumAPI.getScreenShot("HomePageITIT-startHappyPath-");
    }


    @Then("Title of the page should be (.*)")
    public void checkTitle(String title) {
        String pageTitle = seleniumAPI.getTitle();
        assertTrue("Title of the Homepage doesnt match for the given condition", StringUtils.equals(pageTitle, title));
    }

}
