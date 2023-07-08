package com.qa.listeners;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//import org.codehaus.plexus.util.FileUtils;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.Base64;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.qa.BaseTest;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;

public class TestListener implements ITestListener{
	TestUtils utils = new TestUtils();
	
	@Override
	public void onTestFailure(ITestResult result) {
		if(result.getThrowable()!=null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			result.getThrowable().printStackTrace(pw);
			utils.log().info(sw.toString());
		}
		
		//To capture Screenshot
		BaseTest base = new BaseTest();
		
		//command to capture screenshot on file
		File file = base.getDriver().getScreenshotAs(OutputType.FILE);
		
		//code to convert for file object to base64 string to attach screenshot in extent report
		byte[] encoded = null;
		try {
			encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
		} catch(IOException e1) {
			e1.printStackTrace();
		}
		
		//In our directory we also used parameters so we will get them from result object and we get them in the form of map
		Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
		
		//To create directory where screenshots will be stored
		String imagePath = "Screenshots" + File.separator + params.get("platformName") + "_" + params.get("platformVersion") + "_"
				+ params.get("deviceName") + File.separator + base.getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName()
				+ File.separator + result.getName() + ".png";
		
		//Image path using to add image on report
		String completeImagePath= System.getProperty("user.dir") + File.separator + imagePath;
		
		//To copy the screenshot to our directory		
        try {
        	FileUtils.copyFile(file, new File(imagePath));
        	// Attaching screenshot to the report and reporter class is used to add logs and screenshot to testng report
        	Reporter.log("This is the sample Screenshot");
        	Reporter.log("<a href = '"+ completeImagePath +"'> <img src ='"+completeImagePath +"' height = '200' width='200'/></a>");
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
      //Attaching screenshot on TestFailure on Extent Report
        try {
      //FilePath method
      ExtentReport.getTest().fail("Test Failed", MediaEntityBuilder.createScreenCaptureFromPath(completeImagePath).build());
      //Base64 method  	
      ExtentReport.getTest().fail("Test Failed",MediaEntityBuilder.createScreenCaptureFromBase64String(new String(encoded, StandardCharsets.US_ASCII)).build());
      }catch (Exception e) {
        	e.printStackTrace();
        }
		ExtentReport.getTest().fail(result.getThrowable());
	}
	
    @Override
    public void onTestStart(ITestResult result) {
        BaseTest baseTest = new BaseTest();
        ExtentReport.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(baseTest.getPlatform() + "_" + baseTest.getDeviceName()).assignAuthor("Shankit");
 
    }
 
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReport.getTest().log(Status.PASS, "Test Passed");
 
    }
 
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReport.getTest().log(Status.SKIP, "Test Skipped");
    }
 
    @Override
    public void onFinish(ITestContext context) {
        ExtentReport.getReporter().flush();
    }
	
	

}
