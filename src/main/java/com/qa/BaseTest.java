package com.qa;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.qa.utils.TestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.codehaus.plexus.util.Base64;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {

	protected static AppiumDriver driver;
	protected static Properties props;
	protected static HashMap<String, String> strings = new HashMap<String, String>();
	protected static String platform;
	protected static String dateTime;
	InputStream inputStream;
	InputStream stringsis;
	TestUtils utils;
	URI uri;
	URL appiumURL;

	public BaseTest() {
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	// This method will implement the logic to start the video recording before each
	// method
	@BeforeMethod
	public void beforeMethod() {
		System.out.println("Super before method");
		((CanRecordScreen) driver).startRecordingScreen();
	}

	// This method will implement the logic to stop the video recording after each
	// method
	@AfterMethod
	public void afterMethod(ITestResult result) {
		System.out.println("Super after method");

		String media = ((CanRecordScreen) driver).stopRecordingScreen();

		// put the below code in this if block if you want to record for videos only for
		// failed test cases.
		
//		 if(result.getStatus()== 2) {}

		Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

		String mediaPath = "videos" + File.separator + params.get("platformName") + "_" + params.get("platformVersion")
				+ "_" + params.get("udid") + File.separator + dateTime + File.separator
				+ result.getTestClass().getRealClass().getSimpleName() + File.separator + result.getName() + ".png";

		File videoDir = new File(mediaPath);

		if (!videoDir.exists()) {
			videoDir.mkdirs();
		}

		try {
			FileOutputStream stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
			stream.write(Base64.decodeBase64(media.getBytes()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Parameters({ "emulator", "platformName", "platformVersion", "udid" })
	@BeforeTest
	public void beforeTest(String emulator, String platformName, String platformVersion, String udid) throws Exception {
		utils = new TestUtils();
		dateTime = utils.getDateTime();
		platform = platformName;
		try {
			props = new Properties();
			String propFileName = "config.properties";// Write path
			String xmlFileName = "Strings/strings.xml";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			props.load(inputStream);

			// initializing String parsing file
			stringsis = getClass().getClassLoader().getResourceAsStream(xmlFileName);

			strings = utils.parseStringXML(stringsis);

			uri = new URI(props.getProperty("appiumURL"));
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
			caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
			caps.setCapability(MobileCapabilityType.UDID, udid);

			switch (EnumConstants.valueOf(platformName)) {
			case Android:
				caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("androidAutomatioName"));
				caps.setCapability("appPackage", props.getProperty("androidAppPackage"));
				caps.setCapability("appActivity", props.getProperty("androidAppActivity"));
				if (emulator.equalsIgnoreCase("true")) {
					caps.setCapability("avd", "Pixel_3a_API_33_x86_64");
				}
				appiumURL = uri.toURL();
				System.out.println(appiumURL);
//				String appURL = getClass().getResource(props.getProperty("androidAppLocation")).getPath().substring(1);
				String androidAppURL = getClass().getResource(props.getProperty("androidAppLocation")).getFile()
						.substring(1);
				System.out.println(androidAppURL);
//			String appURL = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "app" + File.separator + "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
//				caps.setCapability(MobileCapabilityType.APP, androidAppURL);
				driver = new AndroidDriver(appiumURL, caps);
				break;
			case iOS:
				caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("iOSAutomatioName"));
				caps.setCapability("bundleId", props.getProperty("iOSBundleId"));
				appiumURL = uri.toURL();
				System.out.println(appiumURL);
//				String appURL = getClass().getResource(props.getProperty("androidAppLocation")).getPath().substring(1);
				String appURL = getClass().getResource(props.getProperty("iOSAppLocation")).getFile().substring(1);
				System.out.println(appURL);
//			String appURL = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "app" + File.separator + "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
				caps.setCapability(MobileCapabilityType.APP, appURL);
				driver = new IOSDriver(appiumURL, caps);
				break;
			default:
				throw new Exception("Invalid Platform! -" + platformName);
			}
			String SessionId = driver.getSessionId().toString();
			System.out.println(SessionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (stringsis != null) {
				stringsis.close();
			}
		}
	}

	public AppiumDriver getDriver() {
		return driver;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void waitForVisibility(WebElement e) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
		wait.until(ExpectedConditions.visibilityOf(e));
	}

	public void clear(WebElement e) {
		waitForVisibility(e);
		e.clear();
	}

	public void click(WebElement e) {
		waitForVisibility(e);
		e.click();
	}

	public void sendKeys(WebElement e, String txt) {
		waitForVisibility(e);
		e.sendKeys(txt);
	}

	public String getAttribute(WebElement e, String attribute) {
		waitForVisibility(e);
		return e.getAttribute(attribute);
	}

	public String getText(WebElement e) {
		switch (EnumConstants.valueOf(platform)) {
		case Android:
			return getAttribute(e, "text");
		case iOS:
			return getAttribute(e, "label");
		}
		return null;
	}

	public void closeApp() {
		switch (EnumConstants.valueOf(platform)) {
		case Android:
			((AndroidDriver) driver).terminateApp(props.getProperty("androidAppPackage"));
			break;
		case iOS:
			((IOSDriver) driver).terminateApp(props.getProperty("iOSBundleId"));
		}
	}

	// You can use either particular driver casting like in closeApp() method or
	// InteractsWithApps casting
	public void launchApp() {
		switch (EnumConstants.valueOf(platform)) {
		case Android:
			((InteractsWithApps) driver).activateApp(props.getProperty("androidAppPackage"));
			break;
		case iOS:
			((InteractsWithApps) driver).activateApp(props.getProperty("iOSBundleId"));
		}
	}

	public WebElement scrollToElement() {
//		return driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()" + ".description(\"test-Inventory item page\")).scrollIntoView("+"new UiSelector().description(\"test-Price\"));"));
		return driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()"
				+ ".scrollable(true)).scrollIntoView(" + "new UiSelector().description(\"test-Price\"));"));
	}

	public enum EnumConstants {
		Android, iOS
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
	}

}
