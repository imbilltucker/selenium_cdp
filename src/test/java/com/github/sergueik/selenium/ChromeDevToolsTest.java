package com.github.sergueik.selenium;

import java.nio.file.Paths;
import java.time.Duration;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.Console;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Selected test scenarios for Selenium Chrome Developer Tools Selenium 4 bridge
 * based on:
 * https://github.com/adiohana/selenium-chrome-devtools-examples/blob/master/src/test/java/ChromeDevToolsTest.java
 * https://codoid.com/selenium-4-chrome-devtools-log-entry-listeners/
 * etc.
 * NOTE: https://chromedevtools.github.io/devtools-protocol/tot/Console/ says 
 * The Console domain is deprecated - use Runtime or Log instead.
 *
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ChromeDevToolsTest {

	private static ChromiumDriver driver;
	private static String osName = Utils.getOSName();
	private static DevTools chromeDevTools;

	private static String baseURL = "https://apache.org";

	private final static int id = (int) (java.lang.Math.random() * 1_000_000);
	public final static String consoleMessage = "message from test id #" + id;

	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setUp() throws Exception {
		System
				.setProperty("webdriver.chrome.driver",
						Paths.get(System.getProperty("user.home"))
								.resolve("Downloads").resolve(osName.equals("windows")
										? "chromedriver.exe" : "chromedriver")
								.toAbsolutePath().toString());

		driver = new ChromeDriver();
		Utils.setDriver(driver);
		chromeDevTools = driver.getDevTools();
		chromeDevTools.createSession();
	}

	@BeforeClass
	// https://chromedevtools.github.io/devtools-protocol/tot/Console#method-enable
	public static void beforeClass() throws Exception {
		// enable Console
		chromeDevTools.send(Console.enable());
		// add event listener to show in host console the browser console message
		chromeDevTools.addListener(Console.messageAdded(), System.err::println);
		driver.get(baseURL);
	}

	@AfterClass
	public static void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	// https://chromedevtools.github.io/devtools-protocol/tot/Console#event-messageAdded
	public void consoleMessageAddTest() {
		// Assert
		// add event listener to verify the console message text
		chromeDevTools.addListener(Console.messageAdded(),
				o -> Assert.assertEquals(true, o.getText().equals(consoleMessage)));

		// Act
		// write console message by executing Javascript
		Utils.executeScript("console.log('" + consoleMessage + "');");
	}

}
