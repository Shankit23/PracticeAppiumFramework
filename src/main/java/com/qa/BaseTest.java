package com.qa;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
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
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.apache.logging.log4j.ThreadContext;
import org.codehaus.plexus.util.Base64;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {

	protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
	protected static ThreadLocal<Properties> props = new ThreadLocal<Properties>();
	protected static ThreadLocal<HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
	protected static ThreadLocal<String> platform = new ThreadLocal<String>();
	protected static ThreadLocal<String> dateTime = new ThreadLocal<String>();
	protected static ThreadLocal<String> deviceName = new ThreadLocal<String>();
	private static AppiumDriverLocalService server;
	TestUtils utils = new TestUtils();

//	URI uri;
//	URL appiumURL;

	public AppiumDriver getDriver() {
		return driver.get();
	}

	public void setDriver(AppiumDriver driver2) {
		driver.set(driver2);
	}

	public Properties getProps() {
		return props.get();
	}

	public void setProps(Properties props2) {
		props.set(props2);
	}

	public HashMap<String, String> getStrings() {
		return strings.get();
	}

	public void setStrings(HashMap<String, String> strings2) {
		strings.set(strings2);
	}

	public String getPlatform() {
		return platform.get();
	}

	public void setPlatform(String platform2) {
		platform.set(platform2);
	}

	public String getDateTime() {
		return dateTime.get();
	}

	public void setDateTime(String dateTime2) {
		dateTime.set(dateTime2);
	}

	public String getDeviceName() {
		return deviceName.get();
	}

	public void setDeviceName(String deviceName2) {
		deviceName.set(deviceName2);
	}

	public BaseTest() {
		PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
	}

	// This method will implement the logic to start the video recording before each
	// method
	@BeforeMethod
	public void beforeMethod() {
		((CanRecordScreen) getDriver()).startRecordingScreen();
	}

	// This method will implement the logic to stop the video recording after each
	// method
	@AfterMethod
	public void afterMethod(ITestResult result) {
		String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();

		// put the below code in this if block if you want to record for videos only for
		// failed test cases.

//		 if(result.getStatus()== 2) {}

		Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

		String mediaPath = "videos" + File.separator + params.get("platformName") +  "_" + params.get("deviceName") + File.separator + getDateTime() + File.separator
				+ result.getTestClass().getRealClass().getSimpleName();

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
	
	@BeforeSuite
	public void beforeSuite() throws Exception, Exception{
		ThreadContext.put("ROUTINGKEY", "ServerLogs");
		server = getAppiumServiceDefault();
		if(!checkIfAppiumServerIsRunning(4723)) {
			utils.log().info("server is not running");
			server.start();
			server.clearOutPutStreams();
			utils.log().info("Server is started successfully.");
		} else {
			utils.log().info("Server is already running");
		}
	}
	
	@AfterSuite
	public void afterSuite() {
		server.stop();
		utils.log().info("Server is stopped successfully");
	}
	
	public AppiumDriverLocalService getAppiumServiceDefault() {
		return AppiumDriverLocalService.buildDefaultService();
	}
	
	public boolean checkIfAppiumServerIsRunning(int port) throws Exception {
		boolean isAppiumServerRunning = false;
		ServerSocket socket;
		try {
			socket = new ServerSocket(port);
			socket.close();
		} catch (IOException e) {
			System.out.println("1");
			isAppiumServerRunning = true;
		}
		return isAppiumServerRunning;
	}

	@Parameters({ "emulator", "platformName", "udid", "deviceName", "systemPort", "chromeDriverPort", "wdaLocalPort",
			"webKitDebugProxyPort" })
	@BeforeTest
	public void beforeTest(@Optional("androidOnly") String emulator, String platformName, String udid,
			String deviceName, @Optional("androidOnly") String systemPort,
			@Optional("androidOnly") String chromeDriverPort, @Optional("iOSOnly") String wdaLocalPort,
			@Optional("iOSOnly") String webKitDebugProxyPort) throws Exception {
		setDateTime(utils.dateTime());
		setPlatform(platformName);
		setDeviceName(deviceName);
		URI uri;
		URL appiumURL;
		InputStream inputStream = null;
		InputStream stringsis = null;
		Properties props;
		AppiumDriver driver;
		
		//logging directory code
		String strFile = "logs" + File.separator + platformName + "_" + deviceName;
		File logFile = new File(strFile);
		if(!logFile.exists()) {
			logFile.mkdirs();
		}
		ThreadContext.put("ROUTINGKEY", strFile);
		
		try {
			props = new Properties();
			String propFileName = "config.properties";// Write path
			String xmlFileName = "Strings/strings.xml";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			props.load(inputStream);
			setProps(props);
			// initializing String parsing file
			stringsis = getClass().getClassLoader().getResourceAsStream(xmlFileName);

			setStrings(utils.parseStringXML(stringsis));

			uri = new URI(props.getProperty("appiumURL"));
			appiumURL = uri.toURL();
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
			caps.setCapability(MobileCapabilityType.UDID, udid);
			caps.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);

			switch (EnumConstants.valueOf(platformName)) {
			case Android:
				caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("androidAutomatioName"));
				caps.setCapability("appPackage", props.getProperty("androidAppPackage"));
				caps.setCapability("appActivity", props.getProperty("androidAppActivity"));
				caps.setCapability("systemPort", systemPort);
				caps.setCapability("chromeDriverPort", chromeDriverPort);
				if (emulator.equalsIgnoreCase("true")) {
					caps.setCapability("avd", "Pixel_3a_API_33_x86_64");
				}
				utils.log().info(appiumURL + "");
//				String appURL = getClass().getResource(props.getProperty("androidAppLocation")).getPath().substring(1);
				String androidAppURL = getClass().getResource(props.getProperty("androidAppLocation")).getFile()
						.substring(1);
//				utils.log().info(androidAppURL);
//			    String appURL = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "app" + File.separator + "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
				caps.setCapability(MobileCapabilityType.APP, androidAppURL);
				driver = new AndroidDriver(appiumURL, caps);
				break;
			case iOS:
				caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("iOSAutomatioName"));
				caps.setCapability("bundleId", props.getProperty("iOSBundleId"));
				caps.setCapability("wdaLocalPort", wdaLocalPort);
				caps.setCapability("webKitDebugProxyPort", webKitDebugProxyPort);
				utils.log().info(appiumURL + "");
				String appURL = getClass().getResource(props.getProperty("iOSAppLocation")).getFile().substring(1);
//				utils.log().info(appURL);
//			String appURL = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "app" + File.separator + "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
				caps.setCapability(MobileCapabilityType.APP, appURL);
				driver = new IOSDriver(appiumURL, caps);
				break;
			default:
				throw new Exception("Invalid Platform! -" + platformName);
			}
			setDriver(driver);
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

	public void waitForVisibility(WebElement e) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT));
		wait.until(ExpectedConditions.visibilityOf(e));
	}

	public void clear(WebElement e) {
		waitForVisibility(e);
		e.clear();
	}

	public void click(WebElement e, String msg) {
		waitForVisibility(e);
		utils.log().info(msg);
		ExtentReport.getTest().log(Status.INFO, msg);
		e.click();
	}

	public void sendKeys(WebElement e, String txt, String msg) {
		waitForVisibility(e);
		utils.log().info(msg);
		ExtentReport.getTest().log(Status.INFO, msg);
		e.sendKeys(txt);
	}

	public String getAttribute(WebElement e, String attribute) {
		waitForVisibility(e);
		return e.getAttribute(attribute);
	}

	public String getText(WebElement e, String msg) {
		String txt = null;
		switch (EnumConstants.valueOf(getPlatform())) {
		case Android:
			txt = getAttribute(e, "text");
			break;
		case iOS:
			txt = getAttribute(e, "label");
			break;
		}
		utils.log().info(msg + txt);
		ExtentReport.getTest().log(Status.INFO, msg);
		return txt;
	}

	public void closeApp() {
		switch (EnumConstants.valueOf(getPlatform())) {
		case Android:
			((AndroidDriver) getDriver()).terminateApp(getProps().getProperty("androidAppPackage"));
			break;
		case iOS:
			((IOSDriver) getDriver()).terminateApp(getProps().getProperty("iOSBundleId"));
		}
	}

	// You can use either particular driver casting like in closeApp() method or
	// InteractsWithApps casting
	public void launchApp() {
		switch (EnumConstants.valueOf(getPlatform())) {
		case Android:
			((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("androidAppPackage"));
			break;
		case iOS:
			((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("iOSBundleId"));
		}
	}

	public WebElement scrollToElement() {
//		return driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()" + ".description(\"test-Inventory item page\")).scrollIntoView("+"new UiSelector().description(\"test-Price\"));"));
		return getDriver().findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()"
				+ ".scrollable(true)).scrollIntoView(" + "new UiSelector().description(\"test-Price\"));"));
	}

	public enum EnumConstants {
		Android, iOS
	}

	@AfterTest
	public void afterTest() {
		getDriver().quit();
	}

}
