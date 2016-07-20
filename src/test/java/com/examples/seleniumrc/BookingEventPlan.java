package com.examples.seleniumrc;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.examples.seleniumrc.util.PropertyReader;
import com.thoughtworks.selenium.webdriven.commands.GetAttribute;

public class BookingEventPlan {

	WebDriver driver;
	int num_person;
	WebElement DE;
	String shopname;
	String name_customer, email_customer, phone_customer, address_customer, birth_customer, date_booking, shop_name,
			postercode_customer, fee_eardis_booking;
	int price_dress, price_dress_web, num_ear_dis = 0;
	int ear_dis_fee = 0;
	List OptionBookingList = new ArrayList();
	List OptionDetailList = new ArrayList();
	String totalprice_booking, totalprice_detail, totalprice_booking_payweb, totalprice_detail_payweb,
			totalprice_booking_tax, totalprice_detail_tax, totalprice_booking_tax_web, totalprice_detail_tax_web;
	int totalprice;
	int indcFirstSelectedDate, indrFirstSelectedDate;
	//
	String iddress;
	int normalpricedress, salepricedress;
	int sum = 0;
	String titleMessageDateAllPage,MessageDateAllPage;

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

	@Test
	public void checkBookingKimonoStandard() throws InterruptedException {
		System.out.println(
				"*********************************Test kimono standard*******************************************");

		// click event plan
		clickButtonCss("[href='#travel-tab']");

		//// click event tab homogi
		clickButtonCss("[data-title='訪問着一覧']");
		Thread.sleep(4000);

		// choose shop and dress
		chooseShopAndDress("kyoto", 1);
		Thread.sleep(2000);
		// choose date
		chooseRanDomDate();
		// check message date
		checkMessageDateTable();
		// check shop name
		checkShopNameProductPage();
		// check price
		checkpricedress();

		// //click next button
		clickButtonCss(".btn");
		
		//check information in cart page
		checkcartpage();

		System.out
				.println("***************************************end test********************************************");

	}

	public void	checkcartpage() throws InterruptedException {
		//check shop name 
		String getshopname=findCss(".shop-name").getText().trim();
		Assert.assertEquals("Fail-check shop name in cart page",Boolean.TRUE,getshopname.equals(shopname));
		
		//check message date in cart page
		String getTitleMessage=findCss("#allocate-info-time").getText();
		Assert.assertEquals("Fail-check title message date in cart page",Boolean.TRUE,getTitleMessage.equals(titleMessageDateAllPage));
		
		String getMessageDate=findCss("#allocate-info-hour-list li").getText();
		Assert.assertEquals("Fail-check message date in cart page",Boolean.TRUE,getMessageDate.equals(MessageDateAllPage));
		//choose date again to check 
		clickButtonCss("[data-collapse='#calendar']");
		chooseRanDomDate();
		clickButtonCss("[data-collapse='#calendar']");
		
		//check name and price all dress
			
		
	}
	public void checkpricedress() throws InterruptedException {
		String getNomalPriceDress = findCss(".price-small").getText().replace("￥","").replace(",","").replace("(税抜)","").trim();
		String getDiscountPriceDress = findCss(".price-sale").getText().replace("￥","").replace(",","").replace("(税抜)","").trim();
		System.out.println(getNomalPriceDress);
		System.out.println(normalpricedress);
		Assert.assertEquals("Fail-check nomal price dress ", Boolean.TRUE, getNomalPriceDress.equals(Integer.toString(normalpricedress)));
		Assert.assertEquals("Fail-check sale price dress ", Boolean.TRUE, getDiscountPriceDress.equals(Integer.toString(salepricedress)));

	}

	public void checkShopNameProductPage() throws InterruptedException {

		String shopnameproductpage = findCss(".shop-name").getText();
		System.out.println(shopname);
		Assert.assertEquals("Fail-check shop name in product page", Boolean.TRUE, shopnameproductpage.equals(shopname));
	}

	public void chooseShopAndDress(String nameOfShop, int indexdress) throws InterruptedException {

		// choose shop
		Select chooseshop = new Select(driver.findElement(By.cssSelector("[id=shop_id]")));
		if ("kyoto".equals(nameOfShop)) {
			shopname = "京都駅前店";
			chooseshop.selectByIndex(1);
		} else if ("gionshio".equals(nameOfShop)) {
			shopname = "祇園四条店";
			chooseshop.selectByIndex(2);
		} else if ("osaka".equals(nameOfShop)) {
			shopname = "大阪大丸心斎橋店";
			chooseshop.selectByIndex(3);
		} else if ("tokyo".equals(nameOfShop)) {
			shopname = "東京浅草店";
			chooseshop.selectByIndex(4);
		}

		// click search
		clickButtonCss("[value='検索する']");
		Thread.sleep(7000);
		// click dress
		int row = getRowTableDate(".list.dp-flex li");
		chooseDress(row, indexdress);

	}

	public void chooseDress(int amountdress, int indexdress) throws NumberFormatException, InterruptedException {

		Assert.assertEquals("[FAIL:Shop have not dress", Boolean.TRUE, amountdress > 0);
		// get price dress
		normalpricedress = Integer.parseInt(findCss(" li:nth-child(" + indexdress + ") .price-small a").getText()
				.replace(",", "").replace("円", ""));
		salepricedress = Integer.parseInt(
				findCss(" li:nth-child(" + indexdress + ") .price-sale a").getText().replace(",", "").replace("円", ""));
		clickButtonCss("li:nth-child(" + Integer.toString(indexdress) + ") .btn-link");

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

	public void waitFindCSSElementLoaded(String idelement) throws InterruptedException {
		while (findCss(idelement) == null) {
			Thread.sleep(100);
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
	public Boolean verify_Checked(String idButton) throws InterruptedException {
		String str = get_Attribute_Element(idButton, "data-checked");
		if (str == null) {
			return false;
		}
		if ("1".equals(str)) {
			return true;
		}
		return false;
	}

	// get attribute by create a element and find xpath
	public String get_Attribute_Element(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.xpath(s));
		String result = e.getAttribute(attb);
		return result;
	}

	// click date in present table
	public Boolean chooseRanDomDateOneTable() throws InterruptedException {

		String DateText, parentClass, xpathDateElement, indrDate, indcDate;
		WebElement dateElement;

		int row = getRowTableDate("#choose-date tbody tr");

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

	// Click another week if have not select a date whole table
	public void chooseRanDomDate() throws InterruptedException {

		while (!chooseRanDomDateOneTable()) {
			clickButtonXpath(".//*[@id='page-next']");
			Thread.sleep(3000);
			chooseRanDomDateOneTable();
		}

	}

	public String getAttributeElement(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.xpath(s));
		String result = e.getAttribute(attb);
		return result;
	}

	public void scrollAndClickXpath(String idelement, int timesleep) throws InterruptedException {
		WebElement element = driver.findElement(By.xpath(idelement));
		String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
				+ "var elementTop = arguments[0].getBoundingClientRect().top;"
				+ "window.scrollBy(0, elementTop-(viewPortHeight/3));";

		((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);
		Thread.sleep(timesleep);
		element.click();
		Thread.sleep(200);
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

	public void chooseOption(int numberperson) throws InterruptedException {
		for (int i = 1; i <= numberperson; i++) {

			driver.findElement(By.cssSelector("label[for='choose_later_" + Integer.toString(i) + "']")).click();
			;
			Thread.sleep(200);

			clickButtonXpath(".//*[@id='plans_1_persons_" + Integer.toString(i - 1)
					+ "']/div/div[2]/div/table/tbody/tr/td[3]/ul/li[3]/label");
			Thread.sleep(200);

		}

		// choose photo book

		clickButtonXpath(".//*[@id='book_options[60]SelectBoxItText']");
		// Thread.sleep(200);

		clickButtonXpath(".//*[@id='book_options[60]SelectBoxItOptions']/li[6]/a");

		// Thread.sleep(200);

	}

	public int getPrice_TableOption(String pricetext) throws InterruptedException {

		WebElement a = findXpath(pricetext);
		int pr = Integer.parseInt(a.getAttribute("data-value"));
		return pr;
	}

	public int get_Total_Price_TableOption(String s, int beg, int end) throws InterruptedException {
		String b, linkOp, item1, item2;
		int sum = 0, j;

		for (j = beg; j <= end; j++) {

			b = Integer.toString(j);
			linkOp = s + b + "]/input";

			if (verify_Checked(linkOp)) {
				sum = sum + getPrice_TableOption(linkOp);
				// add item checked in list option
				item1 = findXpath(s + b + "]/label").getText();
				item2 = " ￥" + get_Attribute_Element(s + b + "]/input", "data-value");
				item1 = item1 + item2;
				OptionBookingList.add(item1);

			}
		}
		return sum;

	}

	public Boolean check_Price_OptionTable_person(int Locationperson) throws InterruptedException {
		String a;
		int sum = 0, SumTotal;
		a = Integer.toString(Locationperson);

		sum = sum
				+ get_Total_Price_TableOption(
						".//*[@id='plans_1_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[3]/ul/li[", 3, 5)
				+ get_Total_Price_TableOption(
						".//*[@id='plans_1_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[5]/ul/li[", 3, 5)
				+ get_Total_Price_TableOption(
						".//*[@id='plans_1_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[7]/ul[1]/li[", 2, 3)
				+ get_Total_Price_TableOption(
						".//*[@id='plans_1_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[7]/ul[2]/li[", 2, 10);

		SumTotal = getPrice_TableOption(
				".//*[@id='plans_1_persons_" + a + "']/div/div[2]/div/table/tbody/tr/td[7]/div/span");

		if (sum == SumTotal) {
			return true;
		} else {
			System.out.println("Price of Option table of person " + a + " is NOT exactly");
			return false;
		}

	}

	public Boolean check_Price_OptionTable(int numberperson) throws InterruptedException {
		for (int i = 0; i < numberperson; i++) {
			if (!check_Price_OptionTable_person(i)) {
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
			sum_1_choose = (Integer.parseInt(e.getAttribute("data-last-val"))
					* Integer.parseInt(e.getAttribute("data-value")));
			// add item to list to check with detail booking
			if ("book_options[60]".equals(e.getAttribute("id")) || !"0".equals(e.getAttribute("data-last-val"))) {
				item = findXpath(s + Integer.toString(i) + "]/div[1]/div[1]/label[1]").getText();
				if (e.getAttribute("data-last-val") != "1") {
					item += " x" + e.getAttribute("data-last-val");
					// if customer choose amount more than 1 it is printed
					// amount in detail page
				}
				item += " " + "￥" + Integer.toString(sum_1_choose);
				OptionBookingList.add(item);
			}
			sum += sum_1_choose;
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

		sum = Integer.parseInt(get_Attribute_Element(".//*[@id='book_option_cost']", "data-value"));
		if (sum == sum_photo) {
			return true;
		} else {
			System.out.println("Price of Photos table of customer is NOT exactly");
			return false;
		}
	}

	public int getRowTableDate(String s) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.cssSelector(s));
		int r = rows.size() - 1;// because it add a last row that is finished
								// head tag
		return r;
	}

	public void checkMessageEarlyDiscountPriceDate(int priceEarDisCell, String textMessage) {
		int priceMessage;
		if (priceEarDisCell > 0) {
			textMessage = textMessage.replace("(早朝料金:", "");
			textMessage = textMessage.replace("￥", "");
			textMessage = textMessage.replace(",", "");
			textMessage = textMessage.replace(")", "");
			textMessage = textMessage.trim();
			priceMessage = Integer.parseInt(textMessage);

			Assert.assertEquals("Fail:check message early discount price date", Boolean.TRUE,
					priceMessage == priceEarDisCell);
		} else if (priceEarDisCell < 0) {
			textMessage = textMessage.replace("(夕方割引 :", "");
			textMessage = textMessage.replace("￥", "");
			textMessage = textMessage.replace(",", "");
			textMessage = textMessage.replace(")", "");
			textMessage = textMessage.trim();
			priceMessage = Integer.parseInt(textMessage);

			Assert.assertEquals("Fail:check message early discount price date", Boolean.TRUE,
					priceMessage == priceEarDisCell);

		}

	}

	// xpath cell with xpath 'tr[x]'
	public int getPriceDate(String xpath_cell) throws InterruptedException {
		String trclass = findXpath(xpath_cell).getAttribute("class");
		if (!trclass.contains("even-early")) {
			return 0;
		}
		xpath_cell = xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div/div/span[2]";
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

	public void checkTitleDateMessageTable() throws InterruptedException {

		String getTiltleMessage = "";
		String titleMessage = findCss("#allocate-info-time").getText();
		String xpathSelectedCell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr["
				+ Integer.toString(indrFirstSelectedDate) + "]/td[" + Integer.toString(indcFirstSelectedDate) + "]";

		getTiltleMessage = findXpath(xpathSelectedCell + "/div").getAttribute("data-time_date");
		getTiltleMessage = getTiltleMessage.substring(0, 4) + "年" + getTiltleMessage.substring(5, 7) + "月"
				+ getTiltleMessage.substring(8) + "日 ";
		getTiltleMessage += findXpath(
				".//*[@id='choose-date']/div[2]/div/table/thead/tr/td[" + Integer.toString(indcFirstSelectedDate) + "]")
						.getText().replaceAll("[0-9]", "").replace("/", "");
		getTiltleMessage += " " + findXpath(xpathSelectedCell + "/div").getAttribute("data-time_hour");

		// check
		System.out.println("Title message:" + getTiltleMessage);
		Assert.assertEquals("Fail:check title message date", Boolean.TRUE, getTiltleMessage.equals(titleMessage));
		titleMessageDateAllPage=titleMessage;

	}

	// xpath cell with xpath to 'tr[x]'

	public void checkMessageDateTable() throws InterruptedException {

		// check title message date table first
		checkTitleDateMessageTable();

		// check message date
		String xpathSelectedCell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr["
				+ Integer.toString(indrFirstSelectedDate) + "]";
		int pr = getPriceDate(xpathSelectedCell);
		xpathSelectedCell += "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div";
		String hourCell = getAttributeElement(xpathSelectedCell, "data-time_hour");
		String gettedMessage, textMessage = findCss("#allocate-info-hour-list li").getText();
		String textPriceMessage="";
		if (pr != 0) {
			textPriceMessage = findCss("#allocate-info-hour-list span").getText();
		}
		checkMessageEarlyDiscountPriceDate(pr, textPriceMessage);
		if (pr > 0) {
			gettedMessage = hourCell + " から1 名様のお着付けを開始します" + textPriceMessage;

		} else {
			if (pr < 0) {

				gettedMessage = hourCell + " から1 名様のお着付けを開始します" + textPriceMessage;
			}

			else {
				gettedMessage = hourCell + " から1 名様のお着付けを開始します";

			}
		}
		System.out.println(gettedMessage);
		Assert.assertEquals("Fail:check message date table", Boolean.TRUE, gettedMessage.equals(textMessage));
		MessageDateAllPage=textMessage;

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
		// click to choose birthday

		clickButtonXpath(".//*[@id='select2-Book_birthday_year-container']");
		clickButtonXpath(".//*[@id='Book_birthday_year']/option[4]");

		clickButtonXpath(".//*[@id='select2-Book_birthday_month-container']");
		clickButtonXpath(".//*[@id='Book_birthday_month']/option[4]");

		clickButtonXpath(".//*[@id='select2-Book_birthday_day-container']");
		clickButtonXpath(".//*[@id='Book_birthday_day']/option[4]");

		clickButtonXpath(".//*[@id='Book_source_id']");
		clickButtonXpath(".//*[@id='Book_source_id']/option[3]");

	}

	// check total price on booking page
	public int checkTotalPriceBooking() throws InterruptedException {

		int sum_price = 0, sum_price_1_person = 0, price_after_tax, sum_price_after_tax;
		String taxpr;
		for (int i = 0; i < num_person; i++) {
			// check total of 1 person
			sum_price_1_person = Integer.parseInt(
					driver.findElement(By.cssSelector("#plans_1_persons_" + Integer.toString(i) + " #option_cost"))
							.getAttribute("data-value"))
					+ price_dress;

			if (sum_price_1_person != Integer.parseInt(
					driver.findElement(By.cssSelector("#plans_1_persons_" + Integer.toString(i) + " #person_amount"))
							.getAttribute("data-value"))) {
				System.out.println("Total price of person " + i + "is wrong");
				return -1;
			}

			// add total price of 1 person
			sum_price += sum_price_1_person;
		}

		// add with photo price
		sum_price += Integer.parseInt(get_Attribute_Element(".//*[@id='book_option_cost']", "data-value"));
		// add early fee or discount fee
		sum_price += ear_dis_fee;

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
		int totalpriceweb = totalprice - price_dress * num_person + price_dress_web * num_person;
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
		String s = ".//*[@id='plans_1_persons_" + Integer.toString(order_person) + "']/div[2]/div[1]/div[2]/div[2]/p";
		List<WebElement> rowoptions = driver.findElements(By.xpath(s));
		int r = rowoptions.size();
		String info;

		if (r <= 0)
			return false;
		for (int i = 1; i <= r; i++) {
			info = findXpath(s + "[" + Integer.toString(i) + "]/label").getText() + " "
					+ findXpath(s + "[" + Integer.toString(i) + "]/span").getText().replace(",", "");
			OptionDetailList.add(info);
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

			OptionDetailList.add(info);
		}
		return true;
	}

	public void getOptionDetailList() throws InterruptedException {
		// get option
		for (int i = 0; i < num_person; i++) {
			getRowOptionDetailOnePerson(i);
		}

		for (int i = 1; i <= 4; i++) {
			getRowphotoDetailOneTable(i);
		}

		if (!(OptionBookingList.size() == OptionDetailList.size() && OptionBookingList.containsAll(OptionDetailList))) {
			System.out.println(OptionBookingList);
			System.out.println(OptionDetailList);
			System.out.println("Detail of booking dress is wrong");
		}

	}

	public void getInfoCustomer() throws InterruptedException {
		// get they in booking page

		name_customer = get_Attribute_Element(".//*[@id='Book_rep_name']", "value");
		email_customer = get_Attribute_Element(".//*[@id='Book_rep_email']", "value");
		phone_customer = get_Attribute_Element(".//*[@id='Book_rep_tel01']", "value");
		address_customer = get_Attribute_Element(".//*[@id='Book_rep_addr01']", "value");
		postercode_customer = get_Attribute_Element(".//*[@id='Book_rep_postal_code']", "value");
		birth_customer = get_Attribute_Element(".//*[@id='select2-Book_birthday_year-container']", "title") + "/"
				+ get_Attribute_Element(".//*[@id='select2-Book_birthday_month-container']", "title") + "/"
				+ get_Attribute_Element(".//*[@id='select2-Book_birthday_day-container']", "title");
		date_booking = "2016/"
				+ findXpath(".//*[@id='choose-shop-and-date']/article/div/div[6]/div[2]/span[1]").getText().trim();
		shop_name = getSelectedShop();
		if (ear_dis_fee != 0) {
			fee_eardis_booking = findXpath(".//*[@id='span_early_fee']/span").getText().trim() + "("
					+ Integer.toString(num_ear_dis) + "名様分" + ")";

		}

		totalprice_booking = findXpath(".//*[@id='total_cost']").getText().trim();
		totalprice_booking_payweb = findXpath(".//*[@id='total_cost_reduced']").getText().trim();
		totalprice_booking_tax = findXpath(".//*[@id='total_cost_tax']").getText().trim();
		totalprice_booking_tax_web = findXpath(".//*[@id='total_cost_reduced_tax']").getText().trim();

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

		String date_detail, num_person_detail, shopname_detail, fee_detail = null, namecus_detail, emailcus_detail,
				phonecus_detail, addresscus_detail, postcode_detail, birthcus_detail;

		date_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[1]/span").getText().trim();
		// convert datebook 2016/06/09->2016/6/9
		date_detail = convertDateDetailString(date_detail);

		num_person_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[2]/span").getText().trim();
		shopname_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[3]/span").getText().trim();
		// because if date is not in early fee time , it is not show in booking
		// or detail page
		if (ear_dis_fee != 0) {
			fee_detail = findXpath(".//*[@id='booking_confirm']/div[1]/p[4]/span").getText().trim();
		}
		namecus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[1]/div[2]").getText().trim();
		emailcus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[3]/div[2]").getText().trim();
		phonecus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[4]/div[2]").getText().trim();
		postcode_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[5]/div[2]").getText().trim();
		addresscus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[6]/div[2]").getText().trim();
		birthcus_detail = findXpath(".//*[@id='booking_confirm']/div[3]/ul/li[7]/div[2]").getText().trim();

		totalprice_detail = findXpath(".//*[@id='total_cost']").getText().trim();
		totalprice_detail_payweb = findXpath(".//*[@id='total_cost_reduced']").getText().trim();
		totalprice_detail_tax = findXpath(".//*[@id='total_cost_tax']").getText().trim();
		totalprice_detail_tax_web = findXpath(".//*[@id='total_cost_reduced_tax']").getText().trim();

		if (!date_booking.equals(date_detail)) {
			return false;
		}
		String num = Integer.toString(num_person) + " 名様";
		if (!num.equals(num_person_detail)) {
			System.out.println("number person of customer in detail page is wrong " + num_person_detail);
			return false;
		}
		if (!shop_name.equals(shopname_detail)) {
			System.out.println("shop name customer in detail page is wrong " + shopname_detail);
			System.out.println(shop_name);
			return false;

		}
		if (ear_dis_fee != 0) {
			if (!fee_eardis_booking.equals(fee_detail)) {
				System.out.println("fee early discount customer in detail page  is wrong :" + fee_eardis_booking);
				return false;
			}
		}
		if (!name_customer.equals(namecus_detail)) {
			System.out.println("date detail customer in detail page  is wrong :" + namecus_detail);
			return false;
		}
		if (!email_customer.equals(emailcus_detail)) {
			System.out.println("email customer in detail page is wrong :" + emailcus_detail);
			return false;
		}
		if (!phone_customer.equals(phonecus_detail)) {
			System.out.println("phone customer in detail page is wrong :" + phonecus_detail);
			return false;
		}
		if (!postercode_customer.equals(postcode_detail)) {
			System.out.println("postcode  customer in detail page is wrong :" + postcode_detail);
			return false;
		}
		if (!address_customer.equals(addresscus_detail)) {
			System.out.println("address customer in detail page is wrong :" + addresscus_detail);
			return false;
		}
		if (!birth_customer.equals(birthcus_detail)) {
			System.out.println("birthday customer in detail page  is wrong :" + birthcus_detail);
			return false;
		}

		if (!totalprice_booking.equals(totalprice_detail)) {
			System.out.println("total price customer in detail page  is wrong :" + totalprice_detail);
			return false;
		}
		if (!totalprice_booking_payweb.equals(totalprice_detail_payweb)) {
			System.out.println("total price payment customer in detail page  is wrong :" + totalprice_detail_payweb);
			return false;
		}
		if (!totalprice_booking_tax.equals(totalprice_detail_tax)) {
			System.out.println("total price tax in detail page  is wrong :" + totalprice_detail_tax);
			return false;
		}
		if (!totalprice_booking_tax_web.equals(totalprice_detail_tax_web)) {
			System.out.println(
					"total price tax pay for web of customer in detail page  is wrong :" + totalprice_detail_tax_web);
			return false;
		}

		return true;

	}

}