package com.examples.seleniumrc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.examples.seleniumrc.util.PropertyReader;

import org.openqa.selenium.JavascriptExecutor;

public class CheckBookingKimonoEmulatorDevice {

	WebDriver driver;
	int numPerson;
	WebElement dateElement;
	String nameShop;
	String xpathDateElement, indrDate, indcDate;
	int indrFirstSelectedDate, indrLastSeletedDate, indcFirstSelectedDate, indcLastSeletedDate;
	String nameCustomer, emailCustomer, phoneCustomer, addressCustomer, birthCustomer, dateBooking, shopName,
			postercodeCustomer, feeEarlyDiscount;
	int priceDress, priceDressWeb, numEarlyDiscount = 0;
	int earlyDiscountFee = 0;
	List optionBookingList = new ArrayList();
	List optionDetailList = new ArrayList();
	String totalPriceBooking, totalprice_detail, totalPriceBooking_payweb, totalprice_detail_payweb,
			totalPriceBooking_tax, totalprice_detail_tax, totalPriceBooking_tax_web, totalprice_detail_tax_web;
	int totalprice;

	@Before
	public void setUp() throws Exception {

	//	setupEmuatorDevice("Apple iPhone 4");
	//setupEmuatorDevice("Apple iPhone 5");
	//	setupEmuatorDevice("Apple iPhone 6");
	setupEmuatorDevice("Apple iPhone 6 Plus");
	

	}

	// @After
	// public void tearDown() throws Exception {
	// driver.quit();
	// }

	@Test
	public void main() throws InterruptedException {
		System.out.println(
				"*********************************Test kimono standard*******************************************");

		numPerson = 19;

		 nameShop = "kyoto";
		// nameShop = "gionshijo";
		// nameShop = "osaka";
		//nameShop = "tokyo";

		// nameShop = "kamakura";
		// nameShop = "kinkakuji";
		// nameShop = "shinkyogoku";
		// nameShop = "kiyomizuzaka";

		// click tab kimono
		findCss(".kimono").click();
		waitForPageLoaded();

		// select people
		seLectNumberPerson(numPerson);

		// click next button
		clickButtonCss("#add_plan");
		waitForPageLoaded();

		// get price of dress because price dress of all person is same
		priceDress = Integer.parseInt(getAttributeElement(".//*[@id='person_amount']", "data-value"));
		System.out.println("price of dress=" + priceDress);
		priceDressWeb = Integer.parseInt(findCss("#total_cost_reduced").getAttribute("data-value")) / numPerson;
		System.out.println("price of dress pay web= " + priceDressWeb);

		// click place and date
		if (!seLectShopAndDate(nameShop))
			return;

		// chooose option
		chooseOption(numPerson);

		// check price option
		Assert.assertEquals("[FAIL]-check price option", Boolean.TRUE, checkPriceOptionTable(numPerson));

		Assert.assertEquals("[FAIL]-check price of photo table", Boolean.TRUE, checkPricePhotoTable());

		// check message in date
		Assert.assertEquals("[FAIL]-check Message Date table", Boolean.TRUE, checkMessageDateTable());

		// check total price in booking page(assert in function)
		totalprice = checkTotalPriceBooking();

		// check total price pay for web booking(assert in function)
		checkTotalPricePayWebBooking();

		inputCustomerInfomation();

		// get information of customer in booking page
		getInfoCustomer();

		// click complete button to detail page
		clickButtonXpath(".//*[@id='booking_confirm']");
		Thread.sleep(1000);
		// get list detail of booking page
		getAndCheckOptionDetailList();
		// check information of customer based on list booking and total price
		checkInfoCustomer();
		System.out
				.println("***************************************end test********************************************");

	}

	public void setupEmuatorDevice(String device) throws InterruptedException {
		// here creating our first map for deviceName
		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", device);

		// here creating the second map with key mobileEmulation
		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation", mobileEmulation);

		// setting DesiredCapabilities for chrome
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		String driverUrl = PropertyReader.getValue("chromedriver");
		System.setProperty("webdriver.chrome.driver", driverUrl);
		driver = new ChromeDriver(capabilities);
		driver.manage().window().maximize();

		// opting mobile website...
		driver.get("https://kyotokimono-rental.com/reserve");
		// TimeLoadPage("https://kyotokimono-rental.com/reserve");
		waitForPageLoaded();
	}

	public void waitForPageLoaded() {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}

	}

	public boolean assertDate() throws InterruptedException {

		try {
			Alert alertDate = driver.switchTo().alert();
			alertDate.accept();
			Thread.sleep(400);
			return true;
		} catch (NoAlertPresentException Ex) {
			Thread.sleep(400);
			return false;
		}

	}

	public Boolean clickButtonXpath(String idButton) {
		try {
			WebElement itemElement = driver.findElement(By.xpath((idButton)));
			itemElement.click();
			Thread.sleep(200);
			return true;
		} catch (Exception ex) {
			System.out.println("Not found for click button:" + idButton);
			System.exit(0);
			return false;
		}
	}

	public Boolean clickButtonCss(String idButton) {
		try {
			WebElement itemElement = driver.findElement(By.cssSelector((idButton)));
			itemElement.click();
			Thread.sleep(200);
			return true;
		} catch (Exception ex) {
			System.out.println("Not found for click button:" + idButton);
			System.exit(0);
			return false;
		}
	}

	public WebElement findXpath(String idButton) throws InterruptedException {
		try {
			WebElement itemElement = driver.findElement(By.xpath(idButton));
			return itemElement;
		} catch (Exception ex) {
			System.out.println("Not found for find button:" + idButton);
			System.exit(0);
			return null;
		}
	}

	public WebElement findCss(String idButton) throws InterruptedException {
		try {
			WebElement itemElement = driver.findElement(By.cssSelector(idButton));
			return itemElement;
		} catch (Exception ex) {
			System.out.println("Not found for find button:" + idButton);
			System.exit(0);
			return null;
		}
	}

	// this function is only to use verify price option , not for date table
	// because it is only have data-checked attribute
	public Boolean verifyChecked(String idButton) throws InterruptedException {
		String str = findCss(idButton).getAttribute("data-checked");
		if (str == null) {
			return false;
		}
		if ("1".equals(str)) {
			return true;
		}
		return false;
	}

	// select a number person with argument
	public void seLectNumberPerson(int number) throws InterruptedException {

		clickButtonXpath(".//*[@id='list_plans_1']");
		Thread.sleep(200);
		number++;
		clickButtonXpath(".//*[@id='list_plans_1']/option[" + Integer.toString(number) + "]");
		Thread.sleep(500);
	}

	// get attribute by create a element and find xpath
	public String getAttributeElement(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.xpath(s));
		String result = e.getAttribute(attb);
		return result;
	}

	public String getAttributeElementCSS(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.cssSelector(s));
		String result = e.getAttribute(attb);
		return result;
	}

	// click date in present table
	public Boolean chooseRanDomDateOneTable() throws InterruptedException {

		String DateText, parentClass, xpathDateElement;

		int row = getRowTableDate(".//*[@id='choose-date']/div[2]/div/table/tbody/tr");

		for (int j = 1; j <= 7; j++) {

			for (int i = 1; i <= row; i++) {
				indrDate = Integer.toString(i);
				indcDate = Integer.toString(j);

				// find class name of weekdays
				parentClass = getAttributeElement(".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + indrDate + "]",
						"class");
				// System.out.println("tên class day:" + parentClass);

				// compare the class name of current DE element is like 'thead'
				if ("thead".equals(parentClass)) {
					continue;// if it is that continue with i++;
				}
				// if not, get a DE element
				xpathDateElement = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + indrDate + "]/td[" + indcDate
						+ "]/div/div";

				dateElement = findXpath(xpathDateElement);

				DateText = dateElement.getText();

				if (("-").equals(DateText) || ("×").equals(DateText) || ("☎").equals(DateText)) {
					// nothing
				} else {

					scrollAndClickXpath(xpathDateElement, 0);
					if (assertDate())
						continue;
					else {
						indcFirstSelectedDate = j;
						indrFirstSelectedDate = i;
						return true;
					}

				}

			}
		}
		return false;
	}

	public void scrollAndClickXpath(String idelement, int timesleep) throws InterruptedException {
		WebElement element = driver.findElement(By.xpath(idelement));
		String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
				+ "var elementTop = arguments[0].getBoundingClientRect().top;"
				+ "window.scrollBy(0, elementTop-(viewPortHeight/3));";

		((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);
		Thread.sleep(timesleep);
		element.click();
	}

	public void scrollAndClickCSS(String idelement, int timesleep) throws InterruptedException {
		WebElement element = driver.findElement(By.cssSelector(idelement));
		String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
				+ "var elementTop = arguments[0].getBoundingClientRect().top;"
				+ "window.scrollBy(0, elementTop-(viewPortHeight/3));";

		((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);
		Thread.sleep(timesleep);
		element.click();
	}

	// Click another week if have not select a date whole table
	public void chooseRanDomDate() throws InterruptedException {

		int i = 1;
		while (i <= 8) {
			scrollAndClickXpath(".//*[@id='page-next']", 0);
			Thread.sleep(3000);
			i++;
		}
		while (!chooseRanDomDateOneTable()) {
			scrollAndClickXpath(".//*[@id='page-next']", 0);
			Thread.sleep(3000);
			chooseRanDomDateOneTable();
		}

	}

	// select shop name based on xpath with represent shop name
	// in case not found shop, the date is not selected
	public Boolean seLectShop(String shopname) throws InterruptedException {
		if (shopname.equals("kyoto")) {
			clickButtonCss("#choose-shop label[for='id-5-down']");
			return true;
		} else if (shopname.equals("gionshijo")) {
			clickButtonCss("#choose-shop label[for='id-6-down']");
			return true;
		} else if (shopname.equals("shinkyogoku")) {
			clickButtonCss("#choose-shop label[for='id-1-down']");
			return true;
		} else if (shopname.equals("kiyomizuzaka")) {
			clickButtonCss("#choose-shop label[for='id-2-down']");
			return true;
		} else if (shopname.equals("kinkakuji")) {
			clickButtonCss("#choose-shop label[for='id-4-down']");
			return true;
		} else if (shopname.equals("osaka")) {
			clickButtonCss("#choose-shop label[for='id-7-down']");
			return true;
		} else if (shopname.equals("tokyo")) {
			clickButtonCss("#choose-shop label[for='id-8-down']");
			return true;
		} else if (shopname.equals("kamakura")) {
			clickButtonCss("#choose-shop label[for='id-9-down']");
			return true;
		}

		return false;

	}

	public String getSelectedShop() {
		List<WebElement> firstAreaElements = driver.findElements(By.cssSelector("#choose-shop li"));
		for (WebElement element : firstAreaElements) {
			String liClass = element.getAttribute("class");
			if (liClass.contains("active")) {
				String nameshop = element.findElement(By.cssSelector(".text-shop")).getText().replace("\n", "");
				if ("京都駅前店京都タワー店".equals(nameshop)) {
					List<WebElement> shopNameElements = element.findElements(By.cssSelector("p"));
					return shopNameElements.get(0).getText();
				}
				return nameshop;

			}
		}
		return "";
	}

	// select shop name to choose row of date table

	public Boolean seLectShopAndDate(String shopname) throws InterruptedException {
		if (!seLectShop(shopname))
			return false;
		Thread.sleep(3000);
		chooseRanDomDate();
		return true;

	}

	// tam thoi lam cai ham nay sau nay doc file excel nhieu truong hop hon
	// kyoto voi osaka co them 1 lua chon delivery, con shop cuoi cung kamakura
	// chi co cai photo nho xi
	public void chooseOption(int numberperson) throws InterruptedException {
		for (int i = 1; i <= numberperson; i++) {

			scrollAndClickCSS("label[for='choose_later_" + Integer.toString(i) + "']", 0);
			Thread.sleep(200);
			scrollAndClickCSS("[for='plans[1][persons][" + Integer.toString(i - 1) + "][options][24]']", 0);
			Thread.sleep(200);

		}
//
//		// choose photo table 1
		scrollAndClickCSS("[for='checkbox-control-all']", 0);
//		Select dropdownbox58 = new Select(driver.findElement(By.cssSelector("[name='book_options[58]']")));
//		dropdownbox58.selectByIndex(1);
		Thread.sleep(200);
		// choose photo table 2
		
		Select dropdownbox59 = new Select(driver.findElement(By.cssSelector("[name='book_options[59]']")));
		dropdownbox59.selectByIndex(2);
	
		Select dropdownbox60 = new Select(driver.findElement(By.cssSelector("[name='book_options[60]']")));
		dropdownbox60.selectByIndex(2);
	
//		Select dropdownbox61 = new Select(driver.findElement(By.cssSelector("[name='book_options[61]']")));
//		dropdownbox61.selectByIndex(2);
		//choose photo table 3
		//scrollAndClickCSS("[for='checkbox-control-group-15']", 0);
//		Select dropdownbox62 = new Select(driver.findElement(By.cssSelector("[name='book_options[62]']")));
//		dropdownbox62.selectByIndex(2);
//		Select dropdownbox63 = new Select(driver.findElement(By.cssSelector("[name='book_options[63]']")));
//		dropdownbox63.selectByIndex(3);
//		Select dropdownbox64 = new Select(driver.findElement(By.cssSelector("[name='book_options[64]']")));
//		dropdownbox64.selectByIndex(6);
//		Thread.sleep(200);
//		// choosephoto table 4
//		scrollAndClickCSS("[for='checkbox-control-group-16']", 0);
//		Select dropdownbox65 = new Select(driver.findElement(By.cssSelector("[name='book_options[65]']")));
//		dropdownbox65.selectByIndex(8);
//		Select dropdownbox66 = new Select(driver.findElement(By.cssSelector("[name='book_options[66]']")));
//		dropdownbox66.selectByIndex(8);
//		Select dropdownbox67 = new Select(driver.findElement(By.cssSelector("[name='book_options[67]']")));
//		dropdownbox67.selectByIndex(8);
//		Select dropdownbox68 = new Select(driver.findElement(By.cssSelector("[name='book_options[68]']")));
//		dropdownbox68.selectByIndex(8);
//		Select dropdownbox69 = new Select(driver.findElement(By.cssSelector("[name='book_options[69]']")));
//		dropdownbox69.selectByIndex(8);

	}

	public int getPriceTableOption(String pricetext) throws InterruptedException {

		WebElement a = findCss(pricetext);
		int pr = Integer.parseInt(a.getAttribute("data-value"));
		return pr;
	}

	public int getTotalPriceTableOption(String s, int beg, int end) throws InterruptedException {
		String b, linkOp, item1, item2;
		int sum = 0, j;

		for (j = beg; j <= end; j++) {

			b = Integer.toString(j);
			linkOp = s + b + ") input";

			if (verifyChecked(linkOp)) {
				sum = sum + getPriceTableOption(linkOp);
				// add item checked in list option
				item1 = findCss(s + b + ") label").getText();
				item2 = " ￥" + getAttributeElementCSS(s + b + ") input", "data-value");
				item1 = item1 + item2;
				optionBookingList.add(item1);

			}
		}
		return sum;

	}

	public Boolean checkPriceOptionTablePerson(int Locationperson) throws InterruptedException {
		String a;
		int sum = 0, SumTotal = 0;
		a = Integer.toString(Locationperson);

		sum = sum
				+ getTotalPriceTableOption(
						"#plans_1_persons_" +a+ " ul:nth-child(1) li:nth-child(", 3, 5)
				+ getTotalPriceTableOption(
						"#plans_1_persons_" + a + " ul:nth-child(2) li:nth-child(", 2, 4)
				+ getTotalPriceTableOption(
						"#plans_1_persons_" +a + " ul:nth-child(3) li:nth-child(", 2, 3)
				+ getTotalPriceTableOption(
						"#plans_1_persons_" +a + " ul:nth-child(4) li:nth-child(", 2,
						10);

		SumTotal = getPriceTableOption("#plans_1_persons_" + a + " #option_cost");
		System.out.println("Get price of option table of person " + Locationperson + "=" + sum);
		if (sum == SumTotal) {
			return true;
		} else {
			System.out.println("Price of Option table of person " + a + " is NOT exactly");
			return false;
		}

	}

	public Boolean checkPriceOptionTable(int numberperson) throws InterruptedException {
		for (int i = 0; i < numberperson; i++) {
			if (!checkPriceOptionTablePerson(i)) {
				return false;
			}

		}
		return true;
	}

	public int getPricePhoto(String s, int beg, int end) throws InterruptedException {
		int sum = 0;
		WebElement e;
		String item;
		int sum_1_choose;

		for (int i = beg; i <= end; i++) {
			e = findCss((s + Integer.toString(i) + ") select"));
			if (Integer.parseInt(e.getAttribute("data-last-val")) > 0) {
				if ("book_options[62]".equals(e.getAttribute("id")) || "book_options[63]".equals(e.getAttribute("id"))
						|| "book_options[64]".equals(e.getAttribute("id"))) {
					sum_1_choose = (Integer.parseInt(e.getAttribute("data-last-val")) - 1)
							* (Integer.parseInt(e.getAttribute("data-value")) - 300)
							+ Integer.parseInt(e.getAttribute("data-value"));
				} else {
					sum_1_choose = (Integer.parseInt(e.getAttribute("data-last-val"))
							* Integer.parseInt(e.getAttribute("data-value")));
				}
				// add item to list to check with detail booking

				item = findCss(s + Integer.toString(i) + ") label").getText();
				if (!"1".equals(e.getAttribute("data-last-val").trim())) {
					item += " x" + e.getAttribute("data-last-val");
					// if customer choose amount more than 1 it is printed
					// amount in detail page
				}
				item += " " + "￥" + Integer.toString(sum_1_choose);
				optionBookingList.add(item);
				sum += sum_1_choose;
			}
		}
		return sum;

	}

	public Boolean checkPricePhotoTable() throws InterruptedException {

		int sum_photo = 0, sum;

		sum_photo = sum_photo + getPricePhoto("#book-option-list-group div:nth-child(1)  li:nth-child(", 2, 2)
				+ getPricePhoto("#book-option-list-group div:nth-child(2)  li:nth-child(", 2, 4)
				+ getPricePhoto("#book-option-list-group div:nth-child(3)  li:nth-child(", 2, 4)
				+ getPricePhoto("#book-option-list-group div:nth-child(4)  li:nth-child(", 2, 6);

		sum = Integer.parseInt(findCss("#book_option_cost").getAttribute("data-value"));
		System.out.println("Get price of photo table =" + sum_photo);
		if (sum == sum_photo) {
			return true;
		} else {
			System.out.println("Price of Photos table of customer is NOT exactly");
			return false;
		}
	}

	public int getRowTableDate(String s) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.xpath(s));
		int r = rows.size() - 1;// because it add a last row that is finished
								// head tag
		return r;
	}

	public Boolean checkMessageEarlyPriceDate(int price_early, String text_message) {
		int price_message;

		text_message = text_message.replace("(早朝料金:", "");
		text_message = text_message.replace("￥", "");
		text_message = text_message.replace(",", "");
		text_message = text_message.replace(")", "");
		text_message = text_message.trim();
		price_message = Integer.parseInt(text_message);

		if (price_message == price_early)
			return true;

		return false;
	}

	public Boolean checkMessageDiscountPriceDate(int price_Discount, String text_message) {
		int price_message;

		text_message = text_message.replace("(夕方割引 :", "");
		text_message = text_message.replace("￥", "");
		text_message = text_message.replace(",", "");
		text_message = text_message.replace(")", "");
		text_message = text_message.trim();
		price_message = Integer.parseInt(text_message);

		if (price_message == price_Discount)
			return true;

		return false;
	}

	// get all selected cell of date table
	public int getSelectedCellDateTable() throws InterruptedException {
		int count_cell = 0, i, j;
		int row = getRowTableDate(".//*[@id='choose-date']/div[2]/div/table/tbody/tr");
		String a, b, attclass, parentClass;
		WebElement ele;

		// get the last select date of table
		for (j = indcFirstSelectedDate; j <= 7; j++) {
			for (i = indrFirstSelectedDate; i <= row; i++) {
				a = Integer.toString(i);
				b = Integer.toString(j);

				parentClass = getAttributeElement(".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + a + "]",
						"class");

				// compare the class name of current DE element is like 'thead'
				if ("thead".equals(parentClass)) {
					continue;
				} // if it is that continue with i++;

				ele = findXpath((".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + a + "]/td[" + b + "]/div"));

				attclass = ele.getAttribute("class");
				if ("hour selected".equals(attclass)) {
					count_cell++;
					indrLastSeletedDate = i;
					indcLastSeletedDate = j;
				}

			}
		}
		return count_cell;
	}

	// xpath cell with xpath 'tr[x]'
	public int getPriceDate(String xpath_cell) throws InterruptedException {
		String trclass = findXpath(xpath_cell).getAttribute("class");
		if (!trclass.contains("even-early")) {
			return 0;
		}
		xpath_cell = xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div/div/p";
		String pricecell = findXpath(xpath_cell).getText();

		if ("-300円".equals(pricecell)) {

			return -300;
		} else {
			if ("+500円".equals(pricecell)) {

				return 500;
			}
		}
		return 0;

	}

	public Boolean checkTitleDateMessageTable() throws InterruptedException {
		String title = findCss(".datetime").getText();
		String get_title_var, date_var, time_first_date, time_last_date;
		String hour_last_time, min_last_time;

		date_var = getAttributeElement(".//*[@id='choose-date']/div[2]/div/table/tbody/tr["
				+ Integer.toString(indrFirstSelectedDate) + "]/td[" + Integer.toString(indcFirstSelectedDate) + "]/div",
				"data-time_date");
		date_var = convertDateDetailString(date_var);// convert
														// 2016-06-09->2016-6-9
		date_var = date_var.substring(5);// get 6-9
		date_var = date_var.replace("-", "/");

		time_first_date = getAttributeElement(".//*[@id='choose-date']/div[2]/div/table/tbody/tr["
				+ Integer.toString(indrFirstSelectedDate) + "]/td[" + Integer.toString(indcFirstSelectedDate) + "]/div",
				"data-time_hour");
		time_last_date = getAttributeElement(".//*[@id='choose-date']/div[2]/div/table/tbody/tr["
				+ Integer.toString(indrLastSeletedDate) + "]/td[" + Integer.toString(indcLastSeletedDate) + "]/div",
				"data-time_hour");
		// add 30s for last time get by date table
		hour_last_time = time_last_date.substring(0, 2);
		min_last_time = time_last_date.substring(3);
		if ("00".equals(min_last_time)) {
			min_last_time = "30";
		} else// min =30
		{
			min_last_time = "00";
			hour_last_time = Integer.toString(Integer.parseInt(hour_last_time) + 1);
		}
		get_title_var = date_var + " " + time_first_date + "-" + hour_last_time + ":" + min_last_time;
		System.out.println(get_title_var);
		if (!get_title_var.equals(title)) {
			System.out.println("Title date time Message of date table is wrong:" + get_title_var);
			return false;
		}
		return true;

	}

	// xpath cell with xpath to 'tr[x]'
	public Boolean checkOneMessageDateTable(String xpath_cell, String text_message, String text_price_message,
			int num_cell, int pr) throws InterruptedException {

		String xpath_selected, getted_message, hour_cell;
		int price_cell;

		xpath_selected = xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div";
		hour_cell = getAttributeElement(xpath_selected, "data-time_hour");

		if (pr > 0) {

			price_cell = num_cell * pr;
			if (!checkMessageEarlyPriceDate(price_cell, text_price_message))
				return false;
			getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します" + text_price_message;
			if (getted_message.equals(text_message)) {
				numEarlyDiscount += num_cell;
				System.out.println(getted_message);
				return true;
			}

		} else {

			if (pr < 0) {

				price_cell = num_cell * pr;
				if (!checkMessageDiscountPriceDate(price_cell, text_price_message))
					return false;
				getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します" + text_price_message;
				if (getted_message.equals(text_message)) {
					numEarlyDiscount += num_cell;
					System.out.println(getted_message);
					return true;

				}

			} else {
				getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します";
				if (getted_message.equals(text_message)) {
					System.out.println(getted_message);
					return true;

				}
			}
		}
		return false;
	}

	public Boolean getCheckOneMessageDateTable(String xpath_cell, String text_message, String text_price_message,
			int num, int pr, int li_message) throws InterruptedException {
		text_message = findCss(".time-plan.clearfix li:nth-child(" + Integer.toString(li_message) + ")").getText();
		pr = getPriceDate(xpath_cell);
		earlyDiscountFee += pr * num;// get early or discount fee to check total
		// price
		if (pr != 0) {
			text_price_message = findCss(".time-plan.clearfix li:nth-child(" + Integer.toString(li_message) + ") span")
					.getText();
		} else {
			text_price_message = "";
		}

		if (!checkOneMessageDateTable(xpath_cell, text_message, text_price_message, num, pr)) {
			System.out.println("Message date " + li_message + " is wrong");
			return false;
		}

		return true;

	}

	public Boolean checkMessageDateTable() throws InterruptedException {
		int count_cell, num_cell, num_lost = 0, li_message = 1, pr = 0;
		String xpath_cell, text_message = "", text_price_message = "";
		count_cell = getSelectedCellDateTable();

		System.out.println("--------------Message date table--------------");
		// check title message date table first
		if (!checkTitleDateMessageTable())
			return false;

		if (count_cell == 1) {
			xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indrFirstSelectedDate)
					+ "]";
			if (!getCheckOneMessageDateTable(xpath_cell, text_message, text_price_message, numPerson, pr, li_message)) {
				return false;
			}
			System.out.println("--------------End message date table----------");
			return true;

		}

		for (int i = indrFirstSelectedDate; i < indrLastSeletedDate; i++) {

			xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(i) + "]";
			if (getAttributeElement(xpath_cell, "class").equals("thead")) {
				continue;
			}
			if ("hour selected".equals(getAttributeElement(
					xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]" + "/div", "class"))) {
				num_cell = Integer.parseInt(
						findXpath((xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div/div/span"))
								.getText());
				num_lost = num_lost + num_cell;

				if (!getCheckOneMessageDateTable(xpath_cell, text_message, text_price_message, num_cell, pr,
						li_message)) {
					return false;
				}

				li_message++;
			}
		}

		num_lost = numPerson - num_lost;
		xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indrLastSeletedDate) + "]";
		if (!getCheckOneMessageDateTable(xpath_cell, text_message, text_price_message, num_lost, pr, li_message)) {
			System.out.println("Message date is wrong :" + text_message);
			return false;
		}
		System.out.println("--------------End message date table--------------");
		return true;

	}

	public void inputCustomerInfomation() throws InterruptedException {
		WebElement ele = findXpath((".//*[@id='Book_rep_name']"));
		ele.sendKeys("thuy test");

		ele = findXpath((".//*[@id='Book_rep_email']"));
		ele.sendKeys("nhatthuyld@gmail.com");

		ele = findXpath((".//*[@id='Book_rep_tel01']"));
		ele.sendKeys("8898988787867676");

		ele = findXpath((".//*[@id='Book_rep_postal_code']"));
		ele.sendKeys("1234567890");

		ele = findXpath((".//*[@id='Book_rep_addr01']"));
		ele.sendKeys("1234567890");
		Thread.sleep(200);
		// click to choose birthday
		Select dropdownboxyear = new Select(driver.findElement(By.cssSelector("[name='Book[birthday_year]']")));
		dropdownboxyear.selectByIndex(30);
		Select dropdownboxmonth = new Select(driver.findElement(By.cssSelector("[name='Book[birthday_month]']")));
		dropdownboxmonth.selectByIndex(11);
		Select dropdownboxday = new Select(driver.findElement(By.cssSelector("[name='Book[birthday_day]']")));
		dropdownboxday.selectByIndex(20);
		// choose chanel
		Select dropdownboxchanel = new Select(driver.findElement(By.cssSelector("[name='Book[source_id]']")));
		dropdownboxchanel.selectByIndex(2);

	}

	// check total price on booking page
	public int checkTotalPriceBooking() throws InterruptedException {

		int sum_price = 0, sum_price_1_person = 0, price_after_tax, sum_price_after_tax;
		String taxpr;
		for (int i = 0; i < numPerson; i++) {
			// check total of 1 person
			sum_price_1_person = Integer.parseInt(
					driver.findElement(By.cssSelector("#plans_1_persons_" + Integer.toString(i) + " #option_cost"))
							.getAttribute("data-value"))
					+ priceDress;
			Assert.assertEquals("[FAIL]:check total price of person" + i, Boolean.TRUE,
					sum_price_1_person == Integer
							.parseInt(driver
									.findElement(By
											.cssSelector("#plans_1_persons_" + Integer.toString(i) + " #person_amount"))
									.getAttribute("data-value")));

			// add total price of 1 person
			sum_price += sum_price_1_person;
		}

		// add with photo price
		sum_price += Integer.parseInt(getAttributeElement(".//*[@id='book_option_cost']", "data-value"));
		// add early fee or discount fee
		sum_price += earlyDiscountFee;

		System.out.println("Total price booking =" + sum_price);
		// compare total price
		Assert.assertEquals("[FAIL]:check total price booking", Boolean.TRUE,
				sum_price == Integer.parseInt(findCss("#total_cost").getAttribute("data-value")));

		// get and compare with total price after tax of price
		sum_price_after_tax = (int) (sum_price * 1.08);
		taxpr = findCss("#total_cost_tax").getText();
		taxpr = taxpr.replace("￥", "");
		taxpr = taxpr.replace(",", "");
		price_after_tax = Integer.parseInt(taxpr);

		Assert.assertEquals("[FAIL]:check total price after tax", Boolean.TRUE, sum_price_after_tax == price_after_tax);
		// price after pay for web

		System.out.println("Total price booking after tax =" + sum_price_after_tax);
		return sum_price;
	}

	public int checkTotalPricePayWebBooking() throws InterruptedException {
		// check price pay web
		int totalpriceweb = totalprice - priceDress * numPerson + priceDressWeb * numPerson;
		System.out.println("Total of price by pay web = " + totalpriceweb);

		Assert.assertEquals("[FAIL]:check total price pay web booking", Boolean.TRUE,
				totalpriceweb == (Integer.parseInt(findCss("#total_cost_reduced").getAttribute("data-value"))));
		// check price tax pay web
		int totalpricewebtax = (int) (totalpriceweb * 1.08);
		String ttpricetax = findCss("#total_cost_reduced_tax").getText();
		ttpricetax = ttpricetax.replace("￥", "");
		ttpricetax = ttpricetax.replace(",", "");
		ttpricetax = ttpricetax.trim();

		System.out.println("Total of price pay web after tax= " + totalpricewebtax);
		Assert.assertEquals("[FAIL:check total price pay web tax booking]", Boolean.TRUE,
				Integer.toString(totalpricewebtax).equals(ttpricetax));

		return totalpriceweb;
	}

	public Boolean getRowOptionDetailOnePerson(int order_person) throws InterruptedException {
		String s = "#plans_1_persons_" + Integer.toString(order_person) + " .clearfix.option-list p";
		List<WebElement> rowoptions = driver.findElements(By.cssSelector(s));
		int r = rowoptions.size();
		String info;

		if (r <= 0)
			return false;
		for (int i = 1; i <= r; i++) {
			info = findCss(s + ":nth-child(" + Integer.toString(i) + ") label").getText() + " "
					+ findCss(s + ":nth-child(" + Integer.toString(i) + ") span").getText().replace(",", "");
			optionDetailList.add(info);
		}
		return true;
	}

	public Boolean getRowphotoDetailOneTable(int index_table) throws InterruptedException {
		String s = ".//*[@id='booking_confirm']/div[2]/div[2]/div/div[" + Integer.toString(index_table) + "]/div[2]/p";
		List<WebElement> rowphotos = driver.findElements(By.xpath(s));
		int r = rowphotos.size();
		String info;

		if (r <= 0)
			return false;
		for (int i = 1; i <= r; i++) {
			info = findXpath(s + "[" + Integer.toString(i) + "]/label").getText() + " "
					+ findXpath(s + "[" + Integer.toString(i) + "]/span").getText().replace(",", "");

			optionDetailList.add(info);
		}
		return true;
	}

	public void getAndCheckOptionDetailList() throws InterruptedException {
		// get option
		for (int i = 0; i < numPerson; i++) {
			getRowOptionDetailOnePerson(i);
		}
		
		// get photo option
		for (int i = 1; i <= 4; i++) {
			getRowphotoDetailOneTable(i);
		}

		System.out.println("option booking list:" + optionBookingList);
		System.out.println("option detail list :" + optionDetailList);
		Assert.assertEquals("[FAIL:check options,photo options in booking page and detail page ", Boolean.TRUE,
				optionBookingList.size() == optionDetailList.size() && optionBookingList.containsAll(optionDetailList));

	}

	public void getInfoCustomer() throws InterruptedException {
		// get they in booking page

		nameCustomer = getAttributeElement(".//*[@id='Book_rep_name']", "value");
		emailCustomer = getAttributeElement(".//*[@id='Book_rep_email']", "value");
		phoneCustomer = getAttributeElement(".//*[@id='Book_rep_tel01']", "value");
		addressCustomer = getAttributeElement(".//*[@id='Book_rep_addr01']", "value");
		postercodeCustomer = getAttributeElement(".//*[@id='Book_rep_postal_code']", "value");
		birthCustomer = getAttributeElement(".//*[@id='select2-Book_birthday_year-container']", "title") + "/"
				+ getAttributeElement(".//*[@id='select2-Book_birthday_month-container']", "title") + "/"
				+ getAttributeElement(".//*[@id='select2-Book_birthday_day-container']", "title");
		dateBooking = "2016/" + findCss(".datetime").getText().trim();
		shopName = getSelectedShop();
		if (earlyDiscountFee != 0) {
			feeEarlyDiscount = findXpath(".//*[@id='span_early_fee']/span").getText().trim() + "("
					+ Integer.toString(numEarlyDiscount) + "名様分" + ")";

		}

		totalPriceBooking = findXpath(".//*[@id='total_cost']").getText().trim();
		totalPriceBooking_payweb = findXpath(".//*[@id='total_cost_reduced']").getText().trim();
		totalPriceBooking_tax = findXpath(".//*[@id='total_cost_tax']").getText().trim();
		totalPriceBooking_tax_web = findXpath(".//*[@id='total_cost_reduced_tax']").getText().trim();

	}

	// because date in detail page has form is xxxx/xx/xx
	public String convertDateDetailString(String s) throws InterruptedException {
		String kq = null;
		if (s.charAt(5) == '0' && s.charAt(8) == '0') {
			kq = s.substring(0, 5) + s.substring(6, 8) + s.substring(9);
			return kq;
		}

		if (s.charAt(5) == '0') {
			kq = s.substring(0, 5) + s.substring(6);
			return kq;
		}

		if (s.charAt(8) == '0') {
			kq = s.substring(0, 8) + s.substring(9);
			return kq;
		}
		return s;
	}

	// check all information in detail page with date ,place ,fee booking and
	// total price
	public Boolean checkInfoCustomer() throws InterruptedException {

		String date_detail, numPerson_detail, shopname_detail, fee_detail = null, namecus_detail, emailcus_detail,
				phonecus_detail, addresscus_detail, postcode_detail, birthcus_detail;

		date_detail = findCss(".book_time span").getText().trim();
		// convert datebook 2016/06/09->2016/6/9
		date_detail = convertDateDetailString(date_detail);

		numPerson_detail = findCss(".content p:nth-child(2) span").getText().trim();
		shopname_detail = findCss(".shop-name span").getText().trim();
		// because if date is not in early fee time , it is not show in booking
		// or detail page
		if (earlyDiscountFee != 0) {
			fee_detail = findCss(".content p:nth-child(4) span").getText().trim();
		}
		namecus_detail = findCss(" .form li:nth-child(1) div:nth-child(2)").getText().trim();
		emailcus_detail = findCss(" .form li:nth-child(3) div:nth-child(2)").getText().trim();
		phonecus_detail = findCss(" .form li:nth-child(4) div:nth-child(2)").getText().trim();
		postcode_detail =  findCss(" .form li:nth-child(5) div:nth-child(2)").getText().trim();
		addresscus_detail = findCss(" .form li:nth-child(6) div:nth-child(2)").getText().trim();
		birthcus_detail =  findCss(" .form li:nth-child(7) div:nth-child(2)").getText().trim();

		totalprice_detail = findXpath(".//*[@id='total_cost']").getText().trim();
		totalprice_detail_payweb = findXpath(".//*[@id='total_cost_reduced']").getText().trim();
		totalprice_detail_tax = findXpath(".//*[@id='total_cost_tax']").getText().trim();
		totalprice_detail_tax_web = findXpath(".//*[@id='total_cost_reduced_tax']").getText().trim();

		Assert.assertEquals("[FAIL:check date booking on detail page", Boolean.TRUE,
				dateBooking.equals(date_detail));
		String num = Integer.toString(numPerson) + " 名様";
		
		Assert.assertEquals("[FAIL:check number of customer in detail page", Boolean.TRUE,
				num.equals(numPerson_detail));
	
		Assert.assertEquals("[FAIL:check shop name in detail page", Boolean.TRUE,
				shopName.equals(shopname_detail));
		if (earlyDiscountFee != 0) {
		
			Assert.assertEquals("[FAIL:check fee early or discount  in detail page", Boolean.TRUE,
					feeEarlyDiscount.equals(fee_detail));
		}		
		
		Assert.assertEquals("[FAIL:check name of customer  in detail page", Boolean.TRUE,
				nameCustomer.equals(namecus_detail));
	
		Assert.assertEquals("[FAIL:check email customer  in detail page", Boolean.TRUE,
				emailCustomer.equals(emailcus_detail));
		
		Assert.assertEquals("[FAIL:check phone number of customer  in detail page", Boolean.TRUE,
				phoneCustomer.equals(phonecus_detail));
		
		Assert.assertEquals("[FAIL:checkpostcode of customer  in detail page", Boolean.TRUE,
				postercodeCustomer.equals(postcode_detail));
		
		Assert.assertEquals("[FAIL:address of customer  in detail page", Boolean.TRUE,
				addressCustomer.equals(addresscus_detail));
		
		Assert.assertEquals("[FAIL:checkpostcode of customer  in detail page", Boolean.TRUE,
				postercodeCustomer.equals(postcode_detail));
	
		Assert.assertEquals("[FAIL:birthday of customer  in detail page", Boolean.TRUE,
				birthCustomer.equals(birthcus_detail));

		Assert.assertEquals("[FAIL:checkpostcode of customer  in detail page", Boolean.TRUE,
				postercodeCustomer.equals(postcode_detail));

		Assert.assertEquals("[FAIL:check total price  in detail page", Boolean.TRUE,
				totalPriceBooking.equals(totalprice_detail));
		
		Assert.assertEquals("[FAIL:check total price payment   in detail page", Boolean.TRUE,
				totalPriceBooking_payweb.equals(totalprice_detail_payweb));
		
		Assert.assertEquals("[FAIL:check total price tax  in detail page", Boolean.TRUE,
				totalPriceBooking_tax.equals(totalprice_detail_tax));
		
		Assert.assertEquals("[FAIL:checktotal price tax pay for web  in detail page", Boolean.TRUE,
				totalPriceBooking_tax_web.equals(totalprice_detail_tax_web));

		return true;

	}

}