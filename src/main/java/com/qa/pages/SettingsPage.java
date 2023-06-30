package com.qa.pages;

import org.openqa.selenium.WebElement;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;

import io.appium.java_client.pagefactory.AndroidFindBy;

public class SettingsPage extends BaseTest{
	TestUtils utils = new TestUtils();
	@AndroidFindBy (accessibility = "test-LOGOUT")
	private WebElement logoutBtn;
	
	public LoginPage pressLogoutBtn() {
		utils.log("Press Logout button");
		click(logoutBtn);
		return new LoginPage();
	}
	

}
