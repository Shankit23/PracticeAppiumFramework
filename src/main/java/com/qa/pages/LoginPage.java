package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

import org.openqa.selenium.WebElement;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;

public class LoginPage extends BaseTest {
	TestUtils utils = new TestUtils();
	@AndroidFindBy(accessibility = "test-Username")
	@iOSXCUITFindBy(id = "test-Username")
	private WebElement usernameTxtFld;

	@AndroidFindBy(accessibility = "test-Password")
	@iOSXCUITFindBy(id = "test-Password")
	private WebElement passwordTxtFld;

	@AndroidFindBy(accessibility = "test-LOGIN")
	@iOSXCUITFindBy(id = "test-LOGIN")
	private WebElement loginBtn;

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
	@iOSXCUITFindBy(id = "//XCUIElementTypeOther[@name=\"test-Error message\"]/child::XCUIElementTypeStaticText")
	private WebElement errTxt;

	public LoginPage enterUserName(String username) {
		clear(usernameTxtFld);
		utils.log().info("login with " + username);
		sendKeys(usernameTxtFld, username);
		return this;
	}

	public LoginPage enterPassword(String password) {
		clear(passwordTxtFld);
		utils.log().info("Entering Password " + password);
		sendKeys(passwordTxtFld, password);
		return this;
	}

	public ProductsPage pressLoginBtn() {
		utils.log().info("Press Login button");
		click(loginBtn);
		return new ProductsPage();
	}
	
	public ProductsPage login(String username, String password) {
		enterUserName(username);
		enterPassword(password);
		return pressLoginBtn();
	}

	public String getErrTxt() {
		String err = getText(errTxt);
		utils.log().info("error text is - " + err);
		return err;
	}

}
