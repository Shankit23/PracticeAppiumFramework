package com.qa.test;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;

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
	InputStream datais;
	JSONObject loginUsers;
	
	@BeforeClass
	public void beforeClass() throws Exception {
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
		System.out.println("Login Test before method");
		loginPage = new LoginPage();
		System.out.println("\n" + "****** starting test: " + m.getName() + "*******" + "\n");
	}

	@AfterMethod
	public void afterMethod() {
		System.out.println("Login Test after method");
	}

	
	@Test
	public void invalidUserName() throws Exception {
		loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("username"));
		loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
		loginPage.pressLoginBtn();
		String actualErrTxt = loginPage.getErrTxt();
		String expectedErrTxt = strings.get("err_invalid_username_or_password");
		System.out.println("actual error txt -" + actualErrTxt + "\n" + "expected error txt -" + expectedErrTxt);

		Assert.assertEquals(actualErrTxt, expectedErrTxt);
	}

	@Test
	public void invalidPassword() {
		loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("username"));
		loginPage.enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"));
		loginPage.pressLoginBtn();

		String actualErrTxt = loginPage.getErrTxt();
		String expectedErrTxt = strings.get("err_invalid_username_or_password");
		System.out.println("actual error txt -" + actualErrTxt + "\n" + "expected error txt -" + expectedErrTxt);

		Assert.assertEquals(actualErrTxt, expectedErrTxt);
	}

	@Test
	public void successfulLogin() {
		loginPage.enterUserName(loginUsers.getJSONObject("validUser").getString("username"));
		loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));
		productsPage = loginPage.pressLoginBtn();

		String actualProductTitle = productsPage.getTilte();
		String expectedProductTitle = strings.get("product_title");
		System.out.println("actual product title - " + actualProductTitle + "\n" + "expected product title -- "
				+ expectedProductTitle);
		Assert.assertEquals(actualProductTitle, expectedProductTitle);

	}

}
