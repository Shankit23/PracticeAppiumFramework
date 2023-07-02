package com.qa;

import org.openqa.selenium.WebElement;

import com.qa.pages.SettingsPage;
import com.qa.utils.TestUtils;

import io.appium.java_client.pagefactory.AndroidFindBy;

public class MenuPage extends BaseTest{
	TestUtils utils = new TestUtils();
	
	@AndroidFindBy(xpath= "//android.view.ViewGroup[@content-desc=\"test-Menu\"]/android.view.ViewGroup/android.widget.ImageView")
	private WebElement settingsBtn;
	
	public SettingsPage pressSettingsBtn() {
		utils.log().info("Press Settings Button");
		click(settingsBtn);
		return new SettingsPage();
	}

}
