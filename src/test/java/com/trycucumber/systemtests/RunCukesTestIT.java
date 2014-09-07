package com.trycucumber.systemtests;


import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;


/**
 * Controller class for all Cucumber Selenium tests. This class execute All TestNgCucumberTests present in existing package.
 * <p/>
 * User: Atish Narlawar
 * This is the cucumber Test
 *
 */

/**
 * started cucumber option
 */

@CucumberOptions(features = "src/test/resources/features", tags = {"@ready", "~@wip", "@happypath"}, format = {"html:target/cucumber-html-report", "json:target/cucumber-json-report.json"})
public class RunCukesTestIT extends AbstractTestNGCucumberTests {

}