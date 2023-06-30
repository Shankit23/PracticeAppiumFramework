package com.qa.test;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import com.qa.utils.TestUtils;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.lang.reflect.Method;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
	LoginPage loginPage;
	ProductsPage productsPage;
	JSONObject loginUsers;
	TestUtils utils = new TestUtils();
	
	@BeforeClass
	public void beforeClass() throws Exception {
		InputStream datais = null;
		try {
			String dataFileName = "data/LoginUsers.json";
			datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
			JSONTokener tokener = new JSONTokener(datais);
			loginUsers = new JSONObject(tokener);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			if(datais!=null) {
				datais.close();
			}
		}
		closeApp();
		launchApp();
	}

	@AfterClass
	public void afterClass() {
	}


	@BeforeMethod
	public void beforeMethod(Method m) {
		utils.log("Login Test before method");
		loginPage = new LoginPage();
		utils.log("\n" + "****** starting test: " + m.getName() + "*******" + "\n");
	}

	@AfterMethod
	public void afterMethod() {
		utils.log("Login Test after method");
	}

	
	@Test
	public void invalidUserName() throws Exception {
		loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("username"));
		loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
		loginPage.pressLoginBtn();
		String actualErrTxt = loginPage.getErrTxt();
		String expectedErrTxt = getStrings().get("err_invalid_username_or_password");
		utils.log("actual error txt -" + actualErrTxt + "\n" + "expected error txt -" + expectedErrTxt);

		Assert.assertEquals(actualErrTxt, expectedErrTxt);
	}

	@Test
	public void invalidPassword() {
		loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("username"));
		loginPage.enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"));
		loginPage.pressLoginBtn();

		String actualErrTxt = loginPage.getErrTxt();
		String expectedErrTxt = getStrings().get("err_invalid_username_or_password");
		utils.log("actual error txt -" + actualErrTxt + "\n" + "expected error txt -" + expectedErrTxt);

		Assert.assertEquals(actualErrTxt, expectedErrTxt);
	}

	@Test
	public void successfulLogin() {
		loginPage.enterUserName(loginUsers.getJSONObject("validUser").getString("username"));
		loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));
		productsPage = loginPage.pressLoginBtn();

		String actualProductTitle = productsPage.getTilte();
		String expectedProductTitle = getStrings().get("product_title");
		utils.log("actual product title - " + actualProductTitle + "\n" + "expected product title -- "
				+ expectedProductTitle);
		Assert.assertEquals(actualProductTitle, expectedProductTitle);

	}

}
