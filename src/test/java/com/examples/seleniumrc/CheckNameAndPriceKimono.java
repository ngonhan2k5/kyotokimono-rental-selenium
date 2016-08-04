package com.examples.seleniumrc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.metal.MetalIconFactory.FileIcon16;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.examples.seleniumrc.util.PropertyReader;
import com.sun.jna.platform.win32.OaIdl.IDLDESC;

public class CheckNameAndPriceKimono {

	WebDriver driver;
	List listShopName = new ArrayList();
	List listShopNamePage2 = new ArrayList();
	List listIdKimono = new ArrayList(
			Arrays.asList("1", "2", "26", "3", "39", "35", "36", "4", "6", "7", "37", "8", "40"));

	@Before
	public void setUp() throws Exception {
		String driverUrl = PropertyReader.getValue("chromedriver");
		System.setProperty("webdriver.chrome.driver", driverUrl);
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://kyotokimono-rental.com/reserve");
		Thread.sleep(4000);

	}

	// @After
	// public void tearDown() throws Exception {
	// driver.quit();
	// }

	@Test
	public void checkMain() throws InterruptedException {

		System.out.println("*************************checknameandpriceYukata***************************");
		for (int i = 1; i <= listIdKimono.size(); i++) {
			String a = Integer.toString(i);
			String b = (String) listIdKimono.get(i - 1);
			if (!checkoneBookingKimono(
					"#kimono-tab .list-kimono-item:nth-child(" + a + ") .reserve-list-kimono-content .name",
					"#list_plans_" + b + "SelectBoxItArrowContainer",
					"#list_plans_" + b + "SelectBoxItOptions li:nth-child(2) .number",
					"#kimono-tab .list-kimono-item:nth-child(" + a + ") .price_small",
					"#kimono-tab .list-kimono-item:nth-child(" + a + ") .price_large", a))
				return;

		}

		System.out.println("*************************endcheck***************************");

	}

	public Boolean checkoneBookingKimono(String idnamedress, String idselectbox, String idnumperson, String idprice,
			String idpriceweb, String orderdress) throws InterruptedException {
		String namedress, namedress_page2, price, price_page2, pricepayweb, pricepayweb_page2;

		// click tab kimono
		waitFindCssElementLoaded(".kimono");
		findCss(".kimono").click();
		Thread.sleep(500);

		namedress = findCss(idnamedress).getText().replace("♥", "").replace("\n", "");
		price = getPriceKimono(idprice);
		pricepayweb = getPriceKimono(idpriceweb);
		getNameshop(orderdress);

		// choose number and click next button
		findCss(idselectbox).click();
		Thread.sleep(200);
		findCss(idnumperson).click();
		Thread.sleep(200);
		findCss(".go-to-page").click();
		waitForPageLoaded(driver.getCurrentUrl());

		getNameshoppage2();
		Thread.sleep(500);
		namedress_page2 = findCss(".plan_name.kimono").getText();

		if (!listShopName.containsAll(listShopNamePage2) || !(listShopName.size() == listShopNamePage2.size())) {
			System.out.println("[FAIL:shop names" + orderdress + " are NOT match]");
			System.out.println(listShopName);
			System.out.println(listShopNamePage2);
			return false;
		}
		listShopName.clear();
		listShopNamePage2.clear();
		if (!namedress.equals(namedress_page2)) {
			System.out.println("[FAIL]-names of kimono are NOT match");
			System.out.println(namedress);
			System.out.println(namedress_page2);
			return false;
		}

		price_page2 = findCss("#total_cost").getAttribute("data-value");
		if (!price.equals(price_page2)) {
			System.out.println("[FAIL]-price of kimono are NOT match");
			System.out.println(price);
			System.out.println(price_page2);
			return false;
		}

		pricepayweb_page2 = findCss("#total_cost_reduced").getAttribute("data-value");
		if (!pricepayweb.equals(pricepayweb_page2)) {
			System.out.println("[FAIL]-price pay web of kimono are NOT match");
			System.out.println(pricepayweb);
			System.out.println(pricepayweb_page2);
			return false;
		}

		backAndAssertMessage();
		return true;

	}

	public void waitForPageLoaded(String previousurl) throws InterruptedException {
		String currenturl = driver.getCurrentUrl();
		while (previousurl.equals(currenturl)) {
			currenturl = driver.getCurrentUrl();
			Thread.sleep(100);
		}

	}
	
	public void waitFindCssElementLoaded(String idelement) throws InterruptedException {
		while(findCss(idelement)==null)
		{
			Thread.sleep(100);
		}
	}
	
	public void getNameshoppage2() throws InterruptedException {
		List<WebElement> AreaElements = driver.findElements(By.cssSelector("#choose-shop li"));
		for (WebElement element : AreaElements) {
			String nameshop = element.findElement(By.cssSelector(".text-shop")).getText().replace("\n", "");
			listShopNamePage2.add(nameshop);

		}
	}

	public void getNameshop(String orderdress) throws InterruptedException {
		String nameshop = "";
		List<WebElement> AreaElements = driver.findElements(
				By.cssSelector("#kimono-tab .list-kimono-item:nth-child(" + orderdress + ") .group-icon"));
		for (WebElement element : AreaElements) {
			String liClass = element.getAttribute("class");
			if (!liClass.contains("disable")) {
				nameshop = element.findElement(By.cssSelector(".text-shop")).getText().replace("\n", "");
				if ("京都駅前京都タワー店".equals(nameshop)) {
					listShopName.add("京都駅前店京都タワー店");
				} else
					listShopName.add(nameshop);
			}
		}

	}

	public String getPriceKimono(String idprice) throws InterruptedException {
		String price = findCss(idprice).getText();
		price = price.replace(",", "");
		price = price.replace("円", "");
		price = price.replace("(税抜)", "");
		price = price.trim();
		return price;
	}

	public void backAndAssertMessage() throws InterruptedException {
		// click back button
		findCss("#booking_back").click();
		// assert message

		assertNumberPerson();
	}

	public int countRow(String idButton) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.cssSelector(idButton));
		int r = rows.size();
		return r;
	}

	public WebElement findCss(String idButton) throws InterruptedException {
		try {
			WebElement itemElement = driver.findElement(By.cssSelector(idButton));
			Thread.sleep(500);
			return itemElement;
		} catch (Exception ex) {
			System.out.println("Not found for find button:" + idButton);
			Thread.sleep(200);
			return null;
		}
	}

	public Boolean clickButton(String idButton) throws InterruptedException {
		try {
			WebElement itemElement = driver.findElement(By.xpath(idButton));
			itemElement.click();
			return true;
		} catch (Exception ex) {
			System.out.println("Not found for click button:" + idButton);
			return false;
		}
	}

	public boolean assertNumberPerson() throws InterruptedException {

		try {
			Alert alertnumber = driver.switchTo().alert();
			alertnumber.accept();
			Thread.sleep(400);
			return true;
		} catch (NoAlertPresentException Ex) {
			Thread.sleep(400);
			return false;
		}

	}

}