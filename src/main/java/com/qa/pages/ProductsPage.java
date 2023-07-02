package com.qa.pages;

import org.openqa.selenium.WebElement;

import com.qa.MenuPage;
import com.qa.utils.TestUtils;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class ProductsPage extends MenuPage {
	TestUtils utils = new TestUtils();
	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Cart drop zone\"]/android.view.ViewGroup/android.widget.TextView")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Toggle\"]/parent::*[1]/preceding-sibling::*[1]")
	private WebElement productTitleTxt;
	
	@AndroidFindBy(xpath = "(//android.widget.TextView[@content-desc=\"test-Item title\"])[1]")
	private WebElement SLBTitle;
	
	@AndroidFindBy(xpath = "(//android.widget.TextView[@content-desc=\"test-Price\"])[1]")
	private WebElement SLBPrice;
	
	public String getTilte() {
		String title = getText(productTitleTxt);
		utils.log().info("Product Page title is " + title);
		return title;
	}
	
	public String getSLBTitle() {
		String title = getText(SLBTitle);
		utils.log().info("title is " +title);
		return title;
	}
	
	public String getSLBPrice() {
		String price = getText(SLBPrice);
		utils.log().info("price is "+ price);
		return price;
	}
	
	public ProductDetailsPage pressSLBTitle() {
		utils.log().info("Press slb title link");
		click(SLBTitle);
		return new ProductDetailsPage();
	}

}
