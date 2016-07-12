package com.examples.seleniumrc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.examples.seleniumrc.util.PropertyReader;

public class checknameandpriceYukata {

	WebDriver driver;
	List listshopname = new ArrayList();
	List listshopname_page2 = new ArrayList();
	List listIdYukata = new ArrayList(
			Arrays.asList("12", "13", "14", "15", "79", "16", "18", "17", "20", "21", "22", "23"));
	//lost id 19 because it is in table with id 17

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
	public void checkBookingKimono() throws InterruptedException {
		System.out.println("*************************checknameandpriceYukata***************************");
		
		String a, b;

		for (int i = 1; i <= listIdYukata.size(); i++) {
			//boy baby dress in table same girl baby
	
			a = Integer.toString(i);
			b = (String) listIdYukata.get(i - 1);
			if (!checkoneBookingKimono(
					"#yukata-tab .list-kimono-item:nth-child(" + a + ") .reserve-list-kimono-content .name",
					"#list_plans_" + b + "SelectBoxItArrowContainer",
					"#list_plans_" + b + "SelectBoxItOptions li:nth-child(2) .number",
					"#yukata-tab .list-kimono-item:nth-child(" + a + ") .price_small",
					"#yukata-tab .list-kimono-item:nth-child(" + a + ") .price_large", a)) {
				return;
			}

		}

		System.out.println("*************************endcheck***************************");

	}

	public Boolean checkoneBookingKimono(String idnamedress, String idselectbox, String idnumperson, String idprice,
			String idpriceweb, String orderdress) throws InterruptedException {
		String namedress, namedress_page2, price, price_page2, pricepayweb, pricepayweb_page2;

		waitFindCSSElementLoaded(".yukata");
		namedress = findCss(idnamedress).getText().replace("♥", "").replace("\n", "");
		price = getPriceKimono(idprice);
		pricepayweb = getPriceKimono(idpriceweb);
		getNameshop(orderdress);

		// choose number and click next button

		findCss(idselectbox).click();
		Thread.sleep(200);
		findCss(idnumperson).click();
		Thread.sleep(200);
		findCss("#yukata-tab .go-to-page").click();
		waitForPageLoaded(driver.getCurrentUrl());
		
		getNameshoppage2();
		Thread.sleep(500);
		namedress_page2 = driver
				.findElement(By
						.xpath(".//*[@id='sec-booking-chooseplan']/div/table/tbody/tr/td[1]/div/table/tbody/tr[2]/td[2]"))
				.getText();
		if (!listshopname.containsAll(listshopname_page2) || !(listshopname.size() == listshopname_page2.size())) {
			System.out.println("[FAIL:shop names" + orderdress + " are NOT match]");
			System.out.println(listshopname);
			System.out.println(listshopname_page2);
			return false;
		}
		listshopname.clear();
		listshopname_page2.clear();
		if("#list_plans_23SelectBoxItArrowContainer".equals(idselectbox))
		{
			;// name dress of select box's id is 23 ,is sort than name dress in page 2->pass
		}
		else if (!namedress.equals(namedress_page2)) {
			
			System.out.println("[FAIL]-names of kimono "+idnamedress+" are NOT match");
			System.out.println(namedress);
			System.out.println(namedress_page2);
			return false;
		}

		price_page2 = findCss("#total_cost").getAttribute("data-value");
		if (!price.equals(price_page2)) {
			System.out.println("[FAIL]-price of kimono "+idnamedress+" are NOT match");
			System.out.println(price);
			System.out.println(price_page2);
			return false;
		}

		pricepayweb_page2 = findCss("#total_cost_reduced").getAttribute("data-value");
		if (!pricepayweb.equals(pricepayweb_page2)) {
			System.out.println("[FAIL]-price pay web of kimono "+idnamedress+" are NOT match");
			System.out.println(pricepayweb);
			System.out.println(pricepayweb_page2);
			return false;
		}

		backandassertMessage();
		return true;

	}
	
	public void waitForPageLoaded(String previousurl) throws InterruptedException {
		String currenturl=driver.getCurrentUrl();
		while(previousurl.equals(currenturl))
		{
			currenturl=driver.getCurrentUrl();
			Thread.sleep(100);
		}

	}
	
	public void waitFindCSSElementLoaded(String idelement) throws InterruptedException {
		while(findCss(idelement)==null)
		{
			Thread.sleep(100);
		}
	}

	public void getNameshoppage2() throws InterruptedException {
		List<WebElement> AreaElements = driver.findElements(By.cssSelector("#choose-shop li"));
		for (WebElement element : AreaElements) {
			String nameshop = element.findElement(By.cssSelector(".text-shop")).getText().replace("\n", "");
			listshopname_page2.add(nameshop);

		}
	}

	public void getNameshop(String orderdress) throws InterruptedException {
		String nameshop = "";
		List<WebElement> AreaElements = driver.findElements(
				By.cssSelector("#yukata-tab .list-kimono-item:nth-child(" + orderdress + ") .group-icon"));
		for (WebElement element : AreaElements) {
			String liClass = element.getAttribute("class");
			if (!liClass.contains("disable")) {
				nameshop = element.findElement(By.cssSelector(".text-shop")).getText().replace("\n", "");
				if ("京都駅前京都タワー店".equals(nameshop)) {
					listshopname.add("京都駅前店京都タワー店");
				} else
					listshopname.add(nameshop);
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

	public void backandassertMessage() throws InterruptedException {
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