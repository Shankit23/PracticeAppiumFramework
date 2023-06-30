package com.qa.test;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;
import org.json.JSONTokener;

import com.qa.MenuPage;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import com.qa.utils.TestUtils;

public class ProductTests extends MenuPage {
	LoginPage loginPage;
	ProductsPage productsPage;
	ProductDetailsPage productDetailsPage;
	SettingsPage settingsPage;
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
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (datais != null) {
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
		loginPage = new LoginPage();
		utils.log("\n" + "****** starting test: " + m.getName() + "*******" + "\n");
		productsPage = loginPage.login(loginUsers.getJSONObject("validUser").getString("username"),
				loginUsers.getJSONObject("validUser").getString("password"));
	}

	@AfterMethod
	public void afterMethod() {
		closeApp();
		launchApp();
	}

	@Test
	public void validateProductOnProductsPage() {
		SoftAssert sa = new SoftAssert();

		String SLBTitle = productsPage.getSLBTitle();
		sa.assertEquals(SLBTitle, getStrings().get("products_page_slb_title"));

		String SLBPrice = productsPage.getSLBPrice();
		sa.assertEquals(SLBPrice, getStrings().get("products_page_slb_price"));

//		settingsPage = productsPage.pressSettingsBtn();
//		loginPage = settingsPage.pressLogoutBtn();

		sa.assertAll();
	}

	@Test
	public void validateProductOnProductDetailsPage() throws InterruptedException {
		SoftAssert sa = new SoftAssert();

		productDetailsPage = productsPage.pressSLBTitle();

		String SLBTitle = productDetailsPage.getSLBTitle();
		sa.assertEquals(SLBTitle, getStrings().get("products_details_page_slb_title"));

		String SLBTxt = productDetailsPage.getSLBTxt();
		sa.assertEquals(SLBTxt, getStrings().get("products_details_page_slb_txt"));
		
		Thread.sleep(3000);
		
		String SLBPrice = productDetailsPage.scrollToSLBPriceAnsGetSLBPrice();
		sa.assertEquals(SLBPrice, getStrings().get("products_details_page_slb_price"));

		productsPage = productDetailsPage.pressBackToProductsBtn();

//		Thread.sleep(3000);
//		settingsPage = productsPage.pressSettingsBtn();
//		loginPage = settingsPage.pressLogoutBtn();

		sa.assertAll();
		utils.log("test case passed");
	}
}
