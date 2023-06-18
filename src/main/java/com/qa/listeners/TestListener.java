package com.qa.listeners;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.plexus.util.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.qa.BaseTest;

public class TestListener implements ITestListener{
	
	public void onTestFailure(ITestResult result) {
		if(result.getThrowable()!=null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			result.getThrowable().printStackTrace(pw);
			System.out.println(sw.toString());
		}
		
		//To capture Screenshot
		BaseTest base = new BaseTest();
		
		//command to capture screenshot on file
		File file = base.getDriver().getScreenshotAs(OutputType.FILE);
		
		//In our directory we also used parameters so we will get them from result object and we get them in the form of map
		Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
		
		//To create directory where screenshots will be stored
		String imagePath = "Screenshots" + File.separator + params.get("platformName") + "_" + params.get("platformVersion") + "_"
				+ params.get("udid") + File.separator + base.getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName()
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
		
	}

}
