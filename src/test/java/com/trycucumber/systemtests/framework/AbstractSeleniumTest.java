package com.trycucumber.systemtests.framework;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import static org.testng.Assert.fail;

/**
 * Abstract class for SeleniumTest interface.
 * User : Atish Narlawar
 */
@ContextConfiguration(locations = {"classpath:seleniumContext.xml"})
public abstract class AbstractSeleniumTest extends AbstractTestNGSpringContextTests implements SeleniumTest {

    /**
     * The Context path.
     */
    @Value("${server.context.path}")
    private String contextPath;

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
     * The Verification errors.
     */
    protected final StringBuffer verificationErrors = new StringBuffer();


    /**
     * Gets server url.
     *
     * @return the server url
     */
    @Override
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Gets server port.
     *
     * @return the server port
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Gets page url.
     *
     * @param pagePath the page path
     *
     * @return the page url
     */
    @Override
    public String getPageUrl(String pagePath) {
        String url = this.getServerUrl() + ":" + this.getServerPort();
        if (StringUtils.isNotEmpty(this.getContextPath())) {
            url = url + "/" + this.getContextPath();
        }

        return url + pagePath;
    }

    /**
     * Gets context path.
     *
     * @return the context path
     */
    @Override
    public String getContextPath() {
        return contextPath;
    }


    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @BeforeTest
    public void testSetup() throws Exception {

        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
        logger.debug("Test torn down");
    }


    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @AfterTest
    public void tearDown() throws Exception {

        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
        logger.debug("Test torn down");
    }


}