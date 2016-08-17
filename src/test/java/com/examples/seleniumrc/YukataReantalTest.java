package com.examples.seleniumrc;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.examples.seleniumrc.util.PropertyReader;
import com.thoughtworks.selenium.webdriven.commands.KeyEvent;

public class YukataReantalTest {

	WebDriver driver;
	int numPerson;
	WebElement dateElement;
	String shopName;
	String xpathDateElement, indrDate, indcDate;
	int indrFirstSelectedDate, indrLastSeletedDate, indcFirstSelectedDate, indcLastSeletedDate;
	String nameCustomer, emailCustomer, phoneCustomer, addressCustomer, birthdayCustomer, dateBooking, shopNameDetail,
			postercodeCustomer, feeEarlyDiscountBooking;
	int priceDress, priceDressWeb, numEarlyDiscount = 0;
	int earDiscountFee = 0;
	List optionBookingList = new ArrayList();
	List optionDetailList = new ArrayList();
	String totalPriceBooking, totalPriceDetail, totalPriceBookingPayweb, totalPriceDetailPayWeb, totalPriceBookingTax,
			totalPriceDetailTax, totalPriceBookingTaxWeb, totalPriceDetailTaxWeb;
	int totalprice;

	@Before
	public void setUp() throws Exception {

		String driverUrl = PropertyReader.getValue("chromedriver");
		System.setProperty("webdriver.chrome.driver", driverUrl);
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://kyotokimono-rental.com/reserve");
		Thread.sleep(4000);

//		//press esc
//		Robot robot = new Robot();
//		robot.keyPress(27);

	}

	// @After
	// public void tearDown() throws Exception {
	// driver.quit();
	// }

	public boolean assertDate() throws InterruptedException {
		
		try {
			Alert alertDate = driver.switchTo().alert();
			alertDate.accept();
			return true;
		} catch (NoAlertPresentException Ex) {
			Thread.sleep(400);
			return false;
		}

	}

	@Test
	public void checkMain() throws InterruptedException {
		System.out.println(
				"*********************************Test kimono standard*******************************************");

		numPerson = 19;

		shopName = "kyoto";
		// shopName = "gionshijo";
		// shopName = "osaka";
		// shopName = "tokyo";

		// shopName = "kamakura";
		// shopName = "kinkakuji";
		// shopName = "shinkyogoku";
		// shopName = "kiyomizuzaka";

		// select people
		seLectNumberPerson(numPerson);

		// click next button
		clickButtonXpath(".//*[@id='add_plan_yukata']");
		waitForPageLoaded(driver.getCurrentUrl());
		// Thread.sleep(2000);

		// get price of dress because price dress of all person is same
		priceDress = Integer.parseInt(getAttributeElement(".//*[@id='person_amount']", "data-value"));
		priceDressWeb = Integer.parseInt(findCss("#total_cost_reduced").getAttribute("data-value")) / numPerson;

		// click place and date
		if (!seLectShopAndDate(shopName))
			return;
		Thread.sleep(1000);

		// chooose option
		chooseOption(numPerson);

		// check price option
		Assert.assertEquals("[FAIL]-check price option", Boolean.TRUE, checkPriceOptionTable(numPerson));

		Assert.assertEquals("[FAIL]-check price of photo table", Boolean.TRUE, checkPricePhotoTable());

		// check message in date
		Assert.assertEquals("[FAIL]-check Message Date table", Boolean.TRUE, checkMessageDateTable());

		// check total price in booking page
		totalprice = checkTotalPriceBooking();
		if (totalprice < 0) {
			System.out.println("[FAIL]check total price is wrong");
			return;
		}
		// check total price pay for web booking
		checkTotalPricePayWebBooking();

		// Thread.sleep(200);
		inputCustomerInfomation();
		// click to complete option

		// get information of customer in booking page
		getInfoCustomer();

		// click complete button to detail page
		clickButtonXpath(".//*[@id='booking_confirm']");
		Thread.sleep(200);

		// get list detail of booking page
		getoptionDetailList();
		// Thread.sleep(1000);

		// check information of customer based on list booking and total price
		checkInfoCustomer();
		System.out
				.println("***************************************end test********************************************");

	}

	public Boolean clickButtonXpath(String idButton) {
		try {
			WebElement itemElement = driver.findElement(By.xpath((idButton)));
			itemElement.click();
			Thread.sleep(200);
			return true;
		} catch (Exception ex) {
			System.out.println("Not found for click button:" + idButton);
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

	public void waitForPageLoaded(String previousurl) throws InterruptedException {
		String currenturl = driver.getCurrentUrl();
		while (previousurl.equals(currenturl)) {
			currenturl = driver.getCurrentUrl();
			Thread.sleep(100);
		}

	}

	// this function is only to use verify price option , not for date table
	// because it is only have data-checked attribute
	public Boolean verifyChecked(String idButton) throws InterruptedException {
		String str = getAttributeElement(idButton, "data-checked");
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

		clickButtonXpath(".//*[@id='list_plans_12SelectBoxIt']/span[3]");
		Thread.sleep(200);
		number++;
		clickButtonXpath(".//*[@id='list_plans_12SelectBoxItOptions']/li[" + Integer.toString(number) + "]/a/span[2]");
		Thread.sleep(500);
	}

	// get attribute by create a element and find xpath
	public String getAttributeElement(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.xpath(s));
		String result = e.getAttribute(attb);
		return result;
	}

	// click date in present table
	public Boolean chooseRanDomDateOneTable() throws InterruptedException {

		int alreadyClick = 0;
		String DateText, parentClass, DateString;

		int row = getRowTableDate(".//*[@id='choose-date']/div[2]/div/table/tbody/tr");

		for (int j = 1; j <= 7; j++) {

			for (int i = 1; i <= row; i++) {
				System.out.println("finding cell row " + i + " column " + j);
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
				DateString = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + indrDate + "]/td[" + indcDate
						+ "]/div";

				dateElement = findXpath(DateString + "/div");

				DateText = dateElement.getText();

				if (("-").equals(DateText) || ("×").equals(DateText) || ("☎").equals(DateText)) {
					// nothing
				} else {
					if (alreadyClick == 0) {
						clickButtonXpath(DateString);
					}
					if (assertDate())
						continue;
					else {
						alreadyClick++;
						Thread.sleep(200);
						if ("hour selected".equals(getAttributeElement(DateString, "class"))) {
							indcFirstSelectedDate = j;
							indrFirstSelectedDate = i;
							return true;
						}
					}
				}

			}
		}
		return false;
	}

	// Click another week if have not select a date whole table
	public void chooseRanDomDate() throws InterruptedException {

		while (!chooseRanDomDateOneTable()) {
			clickButtonXpath(".//*[@id='page-next']");
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
		// chooseRanDomDate();
		clickButtonXpath(".//*[@id='choose-date']/div[2]/div/table/tbody/tr[20]/td[3]/div");
		assertDate();
		return true;

	}

	// tam thoi lam cai ham nay sau nay doc file excel nhieu truong hop hon
	// kyoto voi osaka co them 1 lua chon delivery, con shop cuoi cung kamakura
	// chi co cai photo nho xi
	public void chooseOption(int numberperson) throws InterruptedException {
		for (int i = 1; i <= numberperson; i++) {

			driver.findElement(By.cssSelector("label[for='choose_later_" + Integer.toString(i) + "']")).click();
			;
			Thread.sleep(200);

			clickButtonXpath(".//*[@id='plans_12_persons_" + Integer.toString(i - 1)
					+ "']/div/div[2]/div/table/tbody/tr/td[3]/ul/li[3]/label");
			Thread.sleep(200);

		}

		// choose photo book

		clickButtonXpath(".//*[@id='book_options[60]SelectBoxItText']");
		// Thread.sleep(200);

		clickButtonXpath(".//*[@id='book_options[60]SelectBoxItOptions']/li[6]/a");

		// Thread.sleep(200);
		clickButtonXpath(".//*[@id='book_options[63]SelectBoxItText']");
		clickButtonXpath(".//*[@id='book_options[63]SelectBoxItOptions']/li[4]/a");

	}

	public int getPriceTableOption(String pricetext) throws InterruptedException {

		WebElement a = findXpath(pricetext);
		int pr = Integer.parseInt(a.getAttribute("data-value"));
		return pr;
	}

	public int getTotalPriceTableOption(String s, int beg, int end) throws InterruptedException {
		String b, linkOp, item1, item2;
		int sum = 0, j;

		for (j = beg; j <= end; j++) {

			b = Integer.toString(j);
			linkOp = s + b + "]/input";

			if (verifyChecked(linkOp)) {
				sum = sum + getPriceTableOption(linkOp);
				// add item checked in list option
				item1 = findXpath(s + b + "]/label").getText();
				item2 = " ￥" + getAttributeElement(s + b + "]/input", "data-value");
				item1 = item1 + item2;
				optionBookingList.add(item1);

			}
		}
		return sum;

	}

	public Boolean checkPriceOptionTable_person(int Locationperson) throws InterruptedException {
		String a;
		int sum = 0, SumTotal;
		a = Integer.toString(Locationperson);

		sum = sum
				+ getTotalPriceTableOption(
						".//*[@id='plans_12_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[3]/ul/li[", 3, 5)
				+ getTotalPriceTableOption(
						".//*[@id='plans_12_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[5]/ul/li[", 3, 5)
				+ getTotalPriceTableOption(
						".//*[@id='plans_12_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[7]/ul[1]/li[", 2, 3)
				+ getTotalPriceTableOption(
						".//*[@id='plans_12_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[7]/ul[2]/li[", 2, 5);

		SumTotal = getPriceTableOption(
				".//*[@id='plans_12_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[7]/div/span");

		if (sum == SumTotal) {
			return true;
		} else {
			System.out.println("Price of Option table of person " + a + " is NOT exactly");
			return false;
		}

	}

	public Boolean checkPriceOptionTable(int numberperson) throws InterruptedException {
		for (int i = 0; i < numberperson; i++) {
			if (!checkPriceOptionTable_person(i)) {
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
			e = findXpath((s + Integer.toString(i) + "]/select"));
			if (Integer.parseInt(e.getAttribute("data-last-val")) > 0) {
				// discount 300 for people more than 2 of third table photo
				if (("book_options[62]".equals(e.getAttribute("id")) || "book_options[63]".equals(e.getAttribute("id"))
						|| "book_options[64]".equals(e.getAttribute("id")))
						&& Integer.parseInt(e.getAttribute("data-last-val")) > 1) {

					sum_1_choose = (Integer.parseInt(e.getAttribute("data-last-val")) - 1)
							* (Integer.parseInt(e.getAttribute("data-value")) - 300)
							+ Integer.parseInt(e.getAttribute("data-value"));

				} else {
					sum_1_choose = (Integer.parseInt(e.getAttribute("data-last-val"))
							* Integer.parseInt(e.getAttribute("data-value")));
				}

				// add item to list to check with detail booking

				item = findXpath(s + Integer.toString(i) + "]/div[1]/div[1]/label[1]").getText();
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

		sum_photo = sum_photo
				+ getPricePhoto(".//*[@id='booking_form']/div[3]/div[2]/div/table/tbody/tr/td[1]/ul/li[", 2, 2)
				+ getPricePhoto(".//*[@id='booking_form']/div[3]/div[2]/div/table/tbody/tr/td[3]/ul/li[", 3, 5)
				+ getPricePhoto(".//*[@id='booking_form']/div[3]/div[2]/div/table/tbody/tr/td[5]/ul/li[", 2, 4)
				+ getPricePhoto(".//*[@id='booking_form']/div[3]/div[2]/div/table/tbody/tr/td[7]/ul/li[", 2, 6);

		sum = Integer.parseInt(getAttributeElement(".//*[@id='book_option_cost']", "data-value"));
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
		xpath_cell = xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div/div/span[2]";
		String feecellclass = getAttributeElement(xpath_cell, "class");

		if ("discount".equals(feecellclass)) {

			return -300;
		} else {
			if ("new even-early".equals(feecellclass)) {

				return 500;
			}
		}
		return 0;

	}

	public Boolean checkTitleDateMessageTable() throws InterruptedException {
		String title = findXpath(".//*[@id='choose-shop-and-date']/article/div/div[6]/div[2]/span[1]").getText();
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
					return true;

				}

			} else {
				getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します";
				if (getted_message.equals(text_message)) {
					return true;

				}
			}
		}

		return false;
	}

	public Boolean getCheckMessageOneDateTable(String xpath_cell, String text_message, String text_price_message,
			int num, int pr, int li_message) throws InterruptedException {
		text_message = (findXpath((".//*[@id='choose-shop-and-date']/article/div/div[6]/div[2]/span[3]/ul/li" + "["
				+ Integer.toString(li_message) + "]"))).getText();
		pr = getPriceDate(xpath_cell);
		earDiscountFee += pr * num;// get early or discount fee to check total
									// price
		if (pr != 0) {
			text_price_message = (findXpath((".//*[@id='choose-shop-and-date']/article/div/div[6]/div[2]/span[3]/ul/li["
					+ Integer.toString(li_message) + "]/span"))).getText();
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

		// check title message date table first
		if (!checkTitleDateMessageTable())
			return false;

		if (count_cell == 1) {
			xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indrFirstSelectedDate)
					+ "]";
			if (!getCheckMessageOneDateTable(xpath_cell, text_message, text_price_message, numPerson, pr, li_message)) {
				return false;
			}
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

				if (!getCheckMessageOneDateTable(xpath_cell, text_message, text_price_message, num_cell, pr,
						li_message)) {
					return false;
				}

				li_message++;
			}
		}

		num_lost = numPerson - num_lost;
		xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indrLastSeletedDate) + "]";
		if (!getCheckMessageOneDateTable(xpath_cell, text_message, text_price_message, num_lost, pr, li_message)) {
			System.out.println("Message date is wrong :" + text_message);
			return false;
		}

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
					driver.findElement(By.cssSelector("#plans_12_persons_" + Integer.toString(i) + " #option_cost"))
							.getAttribute("data-value"))
					+ priceDress;

			if (sum_price_1_person != Integer.parseInt(
					driver.findElement(By.cssSelector("#plans_12_persons_" + Integer.toString(i) + " #person_amount"))
							.getAttribute("data-value"))) {
				System.out.println("Total price of person " + i + "is wrong");
				return -1;
			}

			// add total price of 1 person
			sum_price += sum_price_1_person;
		}

		// add with photo price
		sum_price += Integer.parseInt(getAttributeElement(".//*[@id='book_option_cost']", "data-value"));
		// add early fee or discount fee
		sum_price += earDiscountFee;

		// compare total price
		if (sum_price != Integer.parseInt(findCss("#total_cost").getAttribute("data-value"))) {
			System.out.println("Total price is Wrong");
			return -1;
		}
		// get and compare with total price after tax of price
		sum_price_after_tax = (int) (sum_price * 1.08);
		taxpr = findCss("#total_cost_tax").getText();
		taxpr = taxpr.replace("￥", "");
		taxpr = taxpr.replace(",", "");
		price_after_tax = Integer.parseInt(taxpr);
		if (sum_price_after_tax != price_after_tax) {
			System.out.println("Price after tax is wrong");
			return -1;
		}
		// price after pay for web

		return sum_price;
	}

	public int checkTotalPricePayWebBooking() throws InterruptedException {
		// check price pay web
		int totalpriceweb = totalprice - priceDress * numPerson + priceDressWeb * numPerson;
		Assert.assertEquals("[FAIL]:check total price pay web booking", Boolean.TRUE,
				totalpriceweb == (Integer.parseInt(findCss("#total_cost_reduced").getAttribute("data-value"))));
		// check price tax pay web
		int totalpricewebtax = (int) (totalpriceweb * 1.08);
		String ttpricetax = findCss("#total_cost_reduced_tax").getText();
		ttpricetax = ttpricetax.replace("￥", "");
		ttpricetax = ttpricetax.replace(",", "");
		ttpricetax = ttpricetax.trim();
		Assert.assertEquals("[FAIL:check total price pay web tax booking]", Boolean.TRUE,
				Integer.toString(totalpricewebtax).equals(ttpricetax));

		return totalpriceweb;
	}

	public Boolean getRowOptionDetailOnePerson(int order_person) throws InterruptedException {
		String s = ".//*[@id='plans_12_persons_" + Integer.toString(order_person) + "']/div[2]/div[1]/div[2]/div[2]/p";
		List<WebElement> rowoptions = driver.findElements(By.xpath(s));
		int r = rowoptions.size();
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

	public void getoptionDetailList() throws InterruptedException {
		// get option
		for (int i = 0; i < numPerson; i++) {
			getRowOptionDetailOnePerson(i);
		}

		for (int i = 1; i <= 4; i++) {
			getRowphotoDetailOneTable(i);
		}

		if (!(optionBookingList.size() == optionDetailList.size() && optionBookingList.containsAll(optionDetailList))) {
			System.out.println(optionBookingList);
			System.out.println(optionDetailList);
			System.out.println("Detail of booking dress is wrong");
		}

	}

	public void getInfoCustomer() throws InterruptedException {
		// get they in booking page

		nameCustomer = getAttributeElement(".//*[@id='Book_rep_name']", "value");
		emailCustomer = getAttributeElement(".//*[@id='Book_rep_email']", "value");
		phoneCustomer = getAttributeElement(".//*[@id='Book_rep_tel01']", "value");
		addressCustomer = getAttributeElement(".//*[@id='Book_rep_addr01']", "value");
		postercodeCustomer = getAttributeElement(".//*[@id='Book_rep_postal_code']", "value");
		birthdayCustomer = getAttributeElement(".//*[@id='select2-Book_birthday_year-container']", "title") + "/"
				+ getAttributeElement(".//*[@id='select2-Book_birthday_month-container']", "title") + "/"
				+ getAttributeElement(".//*[@id='select2-Book_birthday_day-container']", "title");
		dateBooking = "2016/"
				+ findXpath(".//*[@id='choose-shop-and-date']/article/div/div[6]/div[2]/span[1]").getText().trim();
		shopNameDetail = getSelectedShop();
		if (earDiscountFee != 0) {
			feeEarlyDiscountBooking = findXpath(".//*[@id='span_early_fee']/span").getText().trim() + "("
					+ Integer.toString(numEarlyDiscount) + "名様分" + ")";

		}

		totalPriceBooking = findXpath(".//*[@id='total_cost']").getText().trim();
		totalPriceBookingPayweb = findXpath(".//*[@id='total_cost_reduced']").getText().trim();
		totalPriceBookingTax = findXpath(".//*[@id='total_cost_tax']").getText().trim();
		totalPriceBookingTaxWeb = findXpath(".//*[@id='total_cost_reduced_tax']").getText().trim();

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

		date_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[1]/span").getText().trim();
		// convert datebook 2016/06/09->2016/6/9
		date_detail = convertDateDetailString(date_detail);

		numPerson_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[2]/span").getText().trim();
		shopname_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[3]/span").getText().trim();
		// because if date is not in early fee time , it is not show in booking
		// or detail page
		if (earDiscountFee != 0) {
			fee_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[4]/span").getText().trim();
		}
		namecus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[1]/div[2]").getText().trim();
		emailcus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[3]/div[2]").getText().trim();
		phonecus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[4]/div[2]").getText().trim();
		postcode_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[5]/div[2]").getText().trim();
		addresscus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[6]/div[2]").getText().trim();
		birthcus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[7]/div[2]").getText().trim();

		totalPriceDetail = findXpath(".//*[@id='total_cost']").getText().trim();
		totalPriceDetailPayWeb = findXpath(".//*[@id='total_cost_reduced']").getText().trim();
		totalPriceDetailTax = findXpath(".//*[@id='total_cost_tax']").getText().trim();
		totalPriceDetailTaxWeb = findXpath(".//*[@id='total_cost_reduced_tax']").getText().trim();

		if (!dateBooking.equals(date_detail)) {
			return false;
		}
		String num = Integer.toString(numPerson) + " 名様";
		if (!num.equals(numPerson_detail)) {
			System.out.println("number person of customer in detail page is wrong " + numPerson_detail);
			return false;
		}
		if (!shopNameDetail.equals(shopname_detail)) {
			System.out.println("shop name customer in detail page is wrong " + shopname_detail);
			System.out.println(shopNameDetail);
			return false;

		}
		if (earDiscountFee != 0) {
			if (!feeEarlyDiscountBooking.equals(fee_detail)) {
				System.out.println("fee early discount customer in detail page  is wrong :" + feeEarlyDiscountBooking);
				return false;
			}
		}
		if (!nameCustomer.equals(namecus_detail)) {
			System.out.println("name customer in detail page  is wrong :" + namecus_detail);
			return false;
		}
		if (!emailCustomer.equals(emailcus_detail)) {
			System.out.println("email customer in detail page is wrong :" + emailcus_detail);
			return false;
		}
		if (!phoneCustomer.equals(phonecus_detail)) {
			System.out.println("phone customer in detail page is wrong :" + phonecus_detail);
			return false;
		}
		if (!postercodeCustomer.equals(postcode_detail)) {
			System.out.println("postcode  customer in detail page is wrong :" + postcode_detail);
			return false;
		}
		if (!addressCustomer.equals(addresscus_detail)) {
			System.out.println("address customer in detail page is wrong :" + addresscus_detail);
			return false;
		}
		if (!birthdayCustomer.equals(birthcus_detail)) {
			System.out.println("birthday customer in detail page  is wrong :" + birthcus_detail);
			return false;
		}

		if (!totalPriceBooking.equals(totalPriceDetail)) {
			System.out.println("total price customer in detail page  is wrong :" + totalPriceDetail);
			return false;
		}
		if (!totalPriceBookingPayweb.equals(totalPriceDetailPayWeb)) {
			System.out.println("total price payment customer in detail page  is wrong :" + totalPriceDetailPayWeb);
			return false;
		}
		if (!totalPriceBookingTax.equals(totalPriceDetailTax)) {
			System.out.println("total price tax in detail page  is wrong :" + totalPriceDetailTax);
			return false;
		}
		if (!totalPriceBookingTaxWeb.equals(totalPriceDetailTaxWeb)) {
			System.out.println(
					"total price tax pay for web of customer in detail page  is wrong :" + totalPriceDetailTaxWeb);
			return false;
		}

		return true;

	}

}