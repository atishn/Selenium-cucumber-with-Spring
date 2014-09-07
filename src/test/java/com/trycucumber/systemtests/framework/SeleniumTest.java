package com.trycucumber.systemtests.framework;

/**
 * Selenium Test interface.
 * User: Atish Narlawar
 */

public interface SeleniumTest {
    /**
     * Gets server port.
     *
     * @return the server port
     */
    public int getServerPort();

    /**
     * Gets server url.
     *
     * @return the server url
     */
    public String getServerUrl();

    /**
     * Gets page url.
     *
     * @param pagePath the page path
     *
     * @return the page url
     */
    public String getPageUrl(String pagePath);

    /**
     * Gets context path.
     *
     * @return the context path
     */
    public String getContextPath();

}
