package com.qa.pages;

import org.openqa.selenium.WebElement;

import com.qa.MenuPage;

import io.appium.java_client.pagefactory.AndroidFindBy;

public class ProductDetailsPage extends MenuPage{
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
		return getText(SLBTitle);
	}
	
	public String getSLBTxt() {
		return getText(SLBTxt);
	}
	
//	public String getSLBPrice() {
//		String price = getText(SLBPrice);
//		System.out.println(price);
//		return price;
//	}
//	
	public String scrollToSLBPriceAnsGetSLBPrice() {
		return getText(scrollToElement());	
	}
	
	public ProductsPage pressBackToProductsBtn() {
		click(backToProductsBtn);
		return new ProductsPage();
	}

}
