package com.qa.pages;

import org.openqa.selenium.WebElement;

import com.qa.MenuPage;
import com.qa.utils.TestUtils;

import io.appium.java_client.pagefactory.AndroidFindBy;

public class ProductDetailsPage extends MenuPage{
	TestUtils utils = new TestUtils();
	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[1]")
	private WebElement SLBTitle;
	
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[2]")
	private WebElement SLBTxt;
	
	@AndroidFindBy(accessibility = "test-BACK TO PRODUCTS")
	private WebElement backToProductsBtn;
//	
//	@AndroidFindBy(accessibility = "test-Price")
//	private WebElement SLBPrice;
	
	public String getSLBTitle() {
		String title = getText(SLBTitle);
		utils.log().info("title is - " + title);
		return title;
	}
	
	public String getSLBTxt() {
		String txt = getText(SLBTxt);
		utils.log().info("Text is " +txt);
		return txt;
	}
	
//	public String getSLBPrice() {
//		String price = getText(SLBPrice);
//		System.out.println(price);
//		return price;
//	}
//	
	public String scrollToSLBPriceAnsGetSLBPrice() {
		String price = getText(scrollToElement());
		utils.log().info("price is" +price);
		return 	price;
	}
	
	public ProductsPage pressBackToProductsBtn() {
		utils.log().info("Clicking on the Back to Product Button");
		click(backToProductsBtn);
		return new ProductsPage();
	}

}
