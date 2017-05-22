package com.examples.seleniumrc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	int numPerson = 0;
	int normalpricedress, salepricedress;
	int earlyDiscountFee = 0;
	int totalPrice = 0, getPriceTax, PriceTax;
	int indcFirstSelectedDate, indrFirstSelectedDate, indrLastSelectedDate, indcLastSelectedDate;
	List optionBookingList = new ArrayList();
	List optionDetailList = new ArrayList();
	List informationDressProductList = new ArrayList();
	List informationDressCartList = new ArrayList();
	List messageDateProductpageList = new ArrayList();
	String shopname;
	String iddress;
	String titleMessageDateAllPage;
	String dressName;

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

		Thread.sleep(1000);
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
	public void checkMain() throws InterruptedException {
		System.out.println(
				"*********************************Test kimono standard*******************************************");

		// click event plan
		clickButtonCss("[href='#travel-tab']");

		// clickButtonCss("[data-title='訪問着一覧']");
		// Thread.sleep(4000);
		// click event seijin
		clickButtonCss("[data-title='訪問着一覧']");
		Thread.sleep(4000);

		numPerson++;
		// choose shop and dress
		String nameShop;
		nameShop = "kyoto";
		// nameShop = "gionshio";
		// nameShop = "osaka";
		// nameShop = "tokyo";
		chooseShopAndDress(nameShop, 2);// a is a first dress in list
		Thread.sleep(2000);
		
		
		
		chooseRanDomDate();
		checkProductPage();
		Thread.sleep(1000);

		optionBookingList.add(Integer.toString(totalPrice));
		int taxTotalPrice = (int) (Math.round((double) totalPrice * 1.08));
		optionBookingList.add(Integer.toString(taxTotalPrice));

		// input information of customer
		inputCustomerInfomation();

		clickButtonCss("#booking_confirm");
		Thread.sleep(3000);

		// get detail page
		getDetailList();

		// check cart confirm page
		checkCartConfirmPage();

		System.out
				.println("***************************************end test********************************************");

	}

	

	public void checkProductPage() throws InterruptedException {

		// check message date
		checkMessageDateTable();

		// check price
		checkpricedress();

		// get information of dress
		getInfoDressProductPage();

		// //click next button
		clickButtonCss(".btn");
		Thread.sleep(5000);

		// choose option
		chooseOption();
		// check option price
		checkPriceOptionTable();

		// check photo price
		checkPricePhotoTable();

		// check information in cart page
		checkcartpage();

		// check total price
		checkTotalPriceBooking();

	}

	public void compareTwoList(List<String> l1, List<String> l2) throws InterruptedException {
		Assert.assertEquals("Fail-size of two list\n" + l1 + "\n" + l2, Boolean.TRUE, l1.size() == l2.size());
		for (int i = 0; i < l1.size(); i++) {
			Assert.assertEquals("Fail-check item " + i + " of two list: " + "\n" + l1 + "\n" + l2, Boolean.TRUE,
					l1.get(i).equals(l2.get(i)));
		}

	}

	public void getDetailList() throws InterruptedException {
		// get name dress
		// get account of dress
		int rowDetailList = getRowTable(".box-padding.show-info-detail .wrap-item-product");
		for (int i = 1; i <= rowDetailList; i++) {
			optionDetailList.add(findCss(
					".box-padding.show-info-detail .wrap-item-product:nth-child(" + Integer.toString(i) + ") .name")
							.getText().replace("\n", ""));
			optionDetailList
					.add(findCss(".box-padding.show-info-detail .wrap-item-product:nth-child(" + Integer.toString(i)
							+ ") .price-large").getText().replace("￥", "").replace(",", "").replace("(税抜)", "").trim());
			// get information of dress
			int rowinfodress = getRowTable(".box-padding.show-info-detail .wrap-item-product:nth-child("
					+ Integer.toString(i) + ") .property li");
			for (int j = 1; j <= rowinfodress; j++) {
				optionDetailList
						.add(findCss(".box-padding.show-info-detail .wrap-item-product:nth-child(" + Integer.toString(i)
								+ ") .property li:nth-child(" + Integer.toString(j) + ")").getText().replace("\n", ""));
			}
			// get option
			int rowOption = getRowTable(".wrap-item-product:nth-child(" + Integer.toString(i) + ") .box-option p");
			if (rowOption > 0) {
				for (int j = 1; j <= rowOption; j++) {
					optionDetailList.add(
							findCss(".wrap-item-product:nth-child(" + Integer.toString(i) + ") .box-option p:nth-child("
									+ Integer.toString(j) + ")").getText().replace("\n", "").replace(",", ""));

				}
			}

		}
		// get photo
		int rowPhotoList = getRowTable(".wrap-option-list .group-opt");
		for (int k = 1; k <= rowPhotoList; k++) {
			int rowphototable = getRowTable(
					".wrap-option-list .group-opt:nth-child(" + Integer.toString(k) + ") .clearfix");
			for (int l = 1; l <= rowphototable; l++) {
				optionDetailList.add((findCss(".wrap-option-list .group-opt:nth-child(" + Integer.toString(k)
						+ ") .clearfix:nth-child(" + Integer.toString(l) + ")").getText()).replaceAll(",", "")
								.replace("\n", ""));
			}

		}
		optionDetailList.add(findCss(".wrap-option-list .total-opt.clearfix span ").getText().replace("\n", "")
				.replace(",", "").replace("￥", ""));
		optionDetailList
				.add(findCss("#total_cost_reduced").getText().replace("\n", "").replace("￥", "").replace(",", ""));
		optionDetailList
				.add(findCss(".reduced-tax span").getText().replace("\n", "").replace("￥", "").replace(",", ""));

		// get information customer
		int rowInCustomer = getRowTable(".form-h li");
		for (int i = 1; i <= rowInCustomer; i++) {
			optionDetailList
					.add(findCss(".form-h li:nth-child(" + Integer.toString(i) + ") span").getText().replace("\n", ""));
		}

	}

	public void checkCartConfirmPage() throws InterruptedException {
		// check date time
		String datetime = findCss(".book_time.clearfix span").getText().trim();
		Assert.assertEquals("FAIL-check date time on cart confirm page", Boolean.TRUE,
				titleMessageDateAllPage.contains(datetime.substring(0, 4)));
		Assert.assertEquals("FAIL-check date time on cart confirm page", Boolean.TRUE,
				titleMessageDateAllPage.contains(datetime.substring(5, 7)));
		Assert.assertEquals("FAIL-check date time on cart confirm page", Boolean.TRUE,
				titleMessageDateAllPage.contains(datetime.substring(8, 10)));
		Assert.assertEquals("FAIL-check date time on cart confirm page", Boolean.TRUE,
				titleMessageDateAllPage.contains(datetime.substring(11, 13)));

		// check number of dress
		String getNumberDress = findCss(".box-padding.shop-date.clearfix p:nth-child(2) span").getText();
		Assert.assertEquals("FAIL-check number on cart confirm page", Boolean.TRUE,
				getNumberDress.equals(Integer.toString(numPerson) + " 商品"));
		// check shop name
		String getshopName = findCss(".box-padding.shop-date.clearfix p:nth-child(3) span").getText().trim();
		Assert.assertEquals("FAIL-check shop name on cart confirm page", Boolean.TRUE, getshopName.equals(shopname));
		System.out.println(optionBookingList);
		System.out.println(optionDetailList);
		compareTwoList(optionBookingList, optionDetailList);

	}

	public int getPricePhoto(String s) throws InterruptedException {
		int sumpr = 0, sum1Choose;
		int value = Integer.parseInt(findCss(s + " select").getAttribute("data-last-val").trim());
		int pr = Integer.parseInt(findCss(s + " select").getAttribute("data-value"));
		if (value > 0) {
			if (s.equals("[data-id='62']") || s.equals("[data-id='63']") || s.equals("[data-id='64']")) {
				sum1Choose = (value - 1) * (pr - 300) + pr;
			} else {
				sum1Choose = value * pr;
			}
			String item = findCss(s + " label").getText();
			if (!"1".equals(Integer.toString(value))) {
				item += " x" + value;
				// if customer choose amount more than 1 it is printed
				// amount in detail page
			}
			item += "￥" + Integer.toString(sum1Choose);
			optionBookingList.add(item);
			sumpr += sum1Choose;
		}
		return sumpr;
	}

	public void checkPricePhotoTable() throws InterruptedException {

		int getPhotoPrice = 0;
		getPhotoPrice += getPricePhoto("[data-id='58']") + getPricePhoto("[data-id='59']")
				+ getPricePhoto("[data-id='60']") + getPricePhoto("[data-id='61']") + getPricePhoto("[data-id='62']")
				+ getPricePhoto("[data-id='63']") + getPricePhoto("[data-id='64']") + getPricePhoto("[data-id='65']")
				+ getPricePhoto("[data-id='68']") + getPricePhoto("[data-id='69']");

		int sumphoto = Integer.parseInt(findCss("#book_option_cost").getAttribute("data-value"));
		System.out.println("total price of photo " + getPhotoPrice);
		Assert.assertEquals("Fail-check sum photo on cart page ", Boolean.TRUE, sumphoto == getPhotoPrice);
		optionBookingList.add(Integer.toString(sumphoto));

	}

	public int getPriceTableOption(String pricetext) throws InterruptedException {

		WebElement a = findCss(pricetext);
		int pr = Integer.parseInt(a.getAttribute("data-value"));
		return pr;
	}

	public int getPriceOptionOneTable(String s, int beg, int end) throws InterruptedException {
		String b, linkOp, item1, item2;
		int sum = 0, j;

		for (j = beg; j <= end; j++) {

			b = Integer.toString(j);
			linkOp = s + b + ") input";

			if (verifyChecked(linkOp)) {
				sum = sum + getPriceTableOption(linkOp);
				// add item checked in list option
				item1 = findCss(s + b + ") label").getText().trim();
				if ("前撮り".equals(item1)) {
					item2 = " "
							+ findCss(".coming-of-age-day-use-day span:nth-child(2)").getText().replace("(", "")
									.replace(")", "").trim()
							+ "￥" + getAttributeElementCSS(s + b + ") input", "data-value");
				} else
					item2 = "￥" + getAttributeElementCSS(s + b + ") input", "data-value");
				item1 = item1 + item2;
				optionBookingList.add(item1);

			}
		}
		return sum;
	}

	public int getRowTableDate(String s) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.cssSelector(s));
		int r = rows.size() - 1;// because it add a last row that is finished
								// head tag
		return r;
	}

	public void checkPriceOptionTable() throws InterruptedException {
		int sum = 0;
		int optionTable1 = getRowTableDate("#product-" + iddress + " tbody td:nth-child(1) li");
		int optionTable2 = getRowTableDate("#product-" + iddress + " tbody td:nth-child(3) li");
		int optionTable3 = getRowTableDate("#product-" + iddress + " tbody td:nth-child(5) li");
		int optionTable4 = getRowTableDate("#product-" + iddress + " tbody td:nth-child(7) ul:nth-child(1) li");
		int optionTable5 = getRowTableDate("#product-" + iddress + " tbody td:nth-child(7) ul:nth-child(2) li");

		sum += getPriceOptionOneTable("#product-" + iddress + " tbody td:nth-child(1) li:nth-child(", 2, optionTable1)
				+ getPriceOptionOneTable("#product-" + iddress + " tbody td:nth-child(3) li:nth-child(", 3,
						optionTable2)
				+ getPriceOptionOneTable("#product-" + iddress + " tbody td:nth-child(5) li:nth-child(", 3,
						optionTable3)
				+ getPriceOptionOneTable("#product-" + iddress + " tbody td:nth-child(7) ul:nth-child(1) li:nth-child(",
						2, optionTable4)
				+ getPriceOptionOneTable("#product-" + iddress + " tbody td:nth-child(7) ul:nth-child(2) li:nth-child(",
						2, optionTable5);
		String sumCartPage = findCss("#product-" + iddress + " #option_cost").getAttribute("data-value");
		System.out.println("price option table of dress id=" + iddress + " is:" + sum);
		Assert.assertEquals("Fail-check price option table od dress have id=" + iddress, Boolean.TRUE,
				sum == Integer.parseInt(sumCartPage));

	}

	public void chooseOption() throws InterruptedException {
		System.out.println("id" + iddress);
		clickButtonCss("#product-" + iddress + " tbody td:nth-child(3) li:nth-child(3) label ");
		// clickButtonCss("#product-"+iddress+" tbody td:nth-child(5)
		// li:nth-child(3) label");

		// choose take photo for graduated dress kimono

		clickButtonCss("[data-id='32'] label ");
		Thread.sleep(2000);

		// choose photos
		clickButtonCss("[id='book_options[59]SelectBoxItText']");
		clickButtonCss("[id='book_options[59]SelectBoxItOptions'] li:nth-child(2)");

		clickButtonCss("[id='book_options[60]SelectBoxItText']");
		clickButtonCss("[id='book_options[60]SelectBoxItOptions'] li:nth-child(3)");

		clickButtonCss("[id='book_options[61]SelectBoxItText']");
		clickButtonCss("[id='book_options[61]SelectBoxItOptions'] li:nth-child(4)");

	}

	public void getInfoDressProductPage() throws InterruptedException {
		int rows = getRowTable(".property li") - 1;
		for (int i = 1; i <= rows; i++) {
			String s = findCss(".property li:nth-child(" + Integer.toString(i) + ")").getText().replace("\n", "");
			informationDressProductList.add(s);
			optionBookingList.add(s);
		}
	}

	public void checkcartpage() throws InterruptedException {

		// check message date in cart page
		String getTitleMessage = findCss("#allocate-info-time").getText();
		Assert.assertEquals("Fail-check title message date in cart page", Boolean.TRUE,
				getTitleMessage.equals(titleMessageDateAllPage));
		Thread.sleep(200);

		// compare two list message date
		List messageDateCartPage = new ArrayList();
		int rowsMessage = getRowTable(".wrap-shop-date.clearfix li");
		System.out.println(rowsMessage);
		for (int i = 1; i <= rowsMessage; i++) {
			messageDateCartPage.add(findCss(".wrap-shop-date.clearfix li:nth-child(" + Integer.toString(i) + ")")
					.getText().trim().replace("\n", ""));
		}

		compareTwoList(messageDateProductpageList, messageDateCartPage);
		Thread.sleep(2000);
		// count id dress
		List<WebElement> rows = driver.findElements(By.cssSelector(".person_detail"));
		int accountDress = rows.size();
		Thread.sleep(2000);
		// check number of dress
		String numberOfDress = findCss(".total-price").getText().trim();
		System.out.println("number of dress=" + accountDress);
		Assert.assertEquals("Fail-check number of dress in cart page", Boolean.TRUE,
				numberOfDress.equals(Integer.toString(accountDress)));

		// check shop name
		String getshopname = findCss(".shop-name").getText().trim();
		Assert.assertEquals("Fail-check shop name in cart page\n" + shopname + "\n" + getshopname, Boolean.TRUE,
				getshopname.equals(shopname));

		// check price of dress
		checkPriceOfDress();

		// check information of dress
		checkInfoDress();

	}

	public void checkInfoDress() throws InterruptedException {
		int rows = getRowTable("#product-" + iddress + " .property li");
		for (int i = 1; i <= rows; i++) {
			String s = findCss("#product-" + iddress + " .property li:nth-child(" + Integer.toString(i) + ")").getText()
					.replace("\n", "");
			informationDressCartList.add(s);
		}

		System.out.println(informationDressProductList);
		System.out.println(informationDressCartList);

		compareTwoList(informationDressProductList, informationDressCartList);

	}

	public void checkPriceOfDress() throws InterruptedException {
		String price2 = findCss(".person-list #product-" + iddress + " .price-large").getText().replace("(税抜)", "")
				.trim();
		String namedress2 = findCss(".person-list #product-" + iddress + " .title-pro-list").getText();
		int rows = getRowTable(".product-summary-list li");
		for (int i = 1; i <= rows; i++) {
			String namedress1 = findCss(".product-summary-list li:nth-child(" + Integer.toString(i) + ") .product-name")
					.getText();
			if (namedress2.contains(namedress1)) {
				String price1 = findCss(".product-summary-list li:nth-child(" + Integer.toString(i) + ") .price")
						.getText();
				Assert.assertEquals("Fail-price of dress in cart page" + price1 + "and" + price2, Boolean.TRUE,
						price1.equals(price2));
				return;
			}

		}

	}

	public void checkpricedress() throws InterruptedException {
		// String getNomalPriceDress =
		// findCss(".price-small").getText().replace("￥", "").replace(",", "")
		// .replace("(税抜)", "").trim();
		String getDiscountPriceDress = findCss(".price-sale").getText().replace("￥", "").replace(",", "")
				.replace("(税抜)", "").trim();
		// System.out.println(getNomalPriceDress);
		System.out.println(getDiscountPriceDress);
		// Assert.assertEquals("Fail-check nomal price dress ", Boolean.TRUE,
		// getNomalPriceDress.equals(Integer.toString(normalpricedress)));
		Assert.assertEquals("Fail-check sale price dress ", Boolean.TRUE,
				getDiscountPriceDress.equals(Integer.toString(salepricedress)));

	}

	public void chooseShopAndDress(String nameOfShop, int indexdress) throws InterruptedException {

		// choose shop
		Select chooseshop = new Select(driver.findElement(By.cssSelector("[id=shop_id]")));
		if ("kyoto".equals(nameOfShop)) {
			shopname = "フォーマル 京都タワー店";
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
		Thread.sleep(7000);
		// click dress
		int row = getRowTable(".list.dp-flex li");
		chooseDress(row, indexdress);

	}

	public void chooseDress(int amountdress, int indexdress) throws NumberFormatException, InterruptedException {

		Assert.assertEquals("[FAIL:Shop have not dress", Boolean.TRUE, amountdress > 0);

		// get id dress
		iddress = findCss(".list.dp-flex li:nth-child(" + Integer.toString(indexdress) + ") img").getAttribute("p_id");
		dressName = findCss(".list.dp-flex li:nth-child(" + Integer.toString(indexdress) + ") a:nth-child(2)").getText()
				.replace("\n", "");
		optionBookingList.add(dressName);

		// get price dress
		normalpricedress = Integer.parseInt(
				findCss(" li:nth-child(" + indexdress + ") .price a").getText().replace(",", "").replace("円", ""));
		String saleprice = findCss(" li:nth-child(" + indexdress + ") .price a").getText().replace("\n", "")
				.replace(",", "").replace("円", "");
		optionBookingList.add(saleprice);
		salepricedress = Integer.parseInt(saleprice);

		totalPrice += salepricedress;
		clickButtonCss(".list.dp-flex li:nth-child(" + Integer.toString(indexdress) + ") img");

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

			return null;
		}

	}

	public WebElement findCss(String idButton) throws InterruptedException {
		try {
			WebElement itemElement = driver.findElement(By.cssSelector(idButton));
			return itemElement;
		} catch (Exception ex) {
			System.out.println("Not found for find button:" + idButton);

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
	public Boolean verifyChecked(String idButton) throws InterruptedException {
		String str = getAttributeElementCSS(idButton, "data-checked");
		if (str == null) {
			return false;
		}
		if ("1".equals(str)) {
			return true;
		}
		return false;
	}

	// get attribute by create a element and find xpath
	public String getAttributeElementXpath(String s, String attb) throws InterruptedException {
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

		String DateText, parentClass, xpathDateElement, indrDate, indcDate;
		WebElement dateElement;
		int alreadyClick = 0;

		int row = getRowTable("#choose-date tbody tr") - 1;

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
						+ "]/div";

				dateElement = findXpath(xpathDateElement + "/div");

				DateText = dateElement.getText();

				if (DateText.contains("-")|| DateText.contains("×") || DateText.contains("☎")) {
					// nothing
				} else {
					if (alreadyClick == 20) {
						scrollAndClickXpath(xpathDateElement, 500);
					}
					if (assertDate())
						continue;
					else {
						alreadyClick++;
						Thread.sleep(200);
						if ("hour selected".equals(getAttributeElementXpath(xpathDateElement, "class"))) {
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

		int dem = 0;
		while (dem <= 2) {
			dem++;
			clickButtonXpath(".//*[@id='page-next']");
			Thread.sleep(3000);
		}
		while (!chooseRanDomDateOneTable()) {
			clickButtonXpath(".//*[@id='page-next']");
			Thread.sleep(3000);
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

	public int getRowTable(String s) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.cssSelector(s));
		int r = rows.size();// because it add a last row that is finished
							// head tag
		return r;
	}

	public int getRowTableXpath(String s) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.xpath(s));
		int r = rows.size();// because it add a last row that is finished
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
		xpath_cell = xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]";
		String feecellclass = getAttributeElement(xpath_cell, "class");

		if ("discount".equals(feecellclass)) {

			return -300;
		} else {
			if ("early".equals(feecellclass)) {

				return 500;
			}
		}
		return 0;// normal

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
		Assert.assertEquals("Fail:check title message date\n" + getTiltleMessage + "\n" + titleMessage, Boolean.TRUE,
				getTiltleMessage.equals(titleMessage));
		titleMessageDateAllPage = titleMessage;

	}

	public int getSelectedCellDateTable() throws InterruptedException {
		int count_cell = 0, i, j;
		int row = getRowTableXpath(".//*[@id='choose-date']/div[2]/div/table/tbody/tr");
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
					indrLastSelectedDate = i;
					indcLastSelectedDate = j;
				}

			}
		}
		return count_cell;
	}

	public void checkOneMessageDateTable(String xpath_cell, String text_message, String text_price_message,
			int num_cell, int pr) throws InterruptedException {

		String xpath_selected, getted_message, hour_cell;
		int price_cell;

		xpath_selected = xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]/div";
		hour_cell = getAttributeElement(xpath_selected, "data-time_hour");

		if (pr > 0) {

			price_cell = num_cell * pr;
			checkMessageEarlyDiscountPriceDate(price_cell, text_price_message);
			getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します" + text_price_message;
			Assert.assertEquals("Fail-check message date " + getted_message, Boolean.TRUE,
					getted_message.equals(text_message));
		} else {

			if (pr < 0) {

				price_cell = num_cell * pr;
				checkMessageEarlyDiscountPriceDate(price_cell, text_price_message);
				getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します" + text_price_message;

				Assert.assertEquals("Fail-check message date " + getted_message, Boolean.TRUE,
						getted_message.equals(text_message));

			} else {
				getted_message = hour_cell + " から" + Integer.toString(num_cell) + " 名様のお着付けを開始します";

				Assert.assertEquals("Fail-check message date " + getted_message, Boolean.TRUE,
						getted_message.equals(text_message));
			}
		}

	}

	// xpath cell with xpath to 'tr[x]'
	public void getCheckOneMessageDateTable(String xpath_cell, String text_message, String text_price_message, int num,
			int pr, int li_message) throws InterruptedException {

		text_message = findCss("#allocate-info-hour-list li:nth-child(" + Integer.toString(li_message) + ")").getText();
		pr = getPriceDate(xpath_cell);
		earlyDiscountFee += pr;// get early or discount fee to check total

		// price
		if (pr != 0) {
			text_price_message = findCss("#allocate-info-hour-list span").getText();
		} else {
			text_price_message = "";

		}
		checkOneMessageDateTable(xpath_cell, text_message, text_price_message, num, pr);

	}

	public void checkMessageDateTable() throws InterruptedException {

		// check title message date table first
		checkTitleDateMessageTable();

		int count_cell, li_message = 1, pr = 0;
		String xpath_cell, text_message = "", text_price_message = "";
		count_cell = getSelectedCellDateTable();

		System.out.println("--------------Message date table--------------");
		// check title message date table first
		checkTitleDateMessageTable();
		earlyDiscountFee = 0;

		if (count_cell == 2) {
			xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indrFirstSelectedDate)
					+ "]";

			getCheckOneMessageDateTable(xpath_cell, text_message, text_price_message, numPerson, pr, li_message);

		} else {
			for (int i = indrFirstSelectedDate; i < indrLastSelectedDate; i++) {

				xpath_cell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(i) + "]";
				if (getAttributeElement(xpath_cell, "class").equals("thead")) {
					continue;
				}
				if ("hour selected".equals(getAttributeElement(
						xpath_cell + "/td[" + Integer.toString(indcFirstSelectedDate) + "]" + "/div", "class"))) {

					getCheckOneMessageDateTable(xpath_cell, text_message, text_price_message, 1, pr, li_message);
					li_message++;

				}
			}
		}

		System.out.println("--------------End message date table--------------");
		// get list message for test cart page
		int rowsMessageProductPage = getRowTable("#allocate-info-hour-list li");
		for (int i = 1; i <= rowsMessageProductPage; i++) {
			if (count_cell == 2) {
				messageDateProductpageList.clear();
			}
			messageDateProductpageList
					.add(findCss("#allocate-info-hour-list li:nth-child(" + Integer.toString(i) + ")").getText());
		}

	}

	public void inputCustomerInfomation() throws InterruptedException {
		WebElement ele = findCss(("#HighendBook_rep_name"));
		String name = "thuy test";
		ele.sendKeys(name);
		optionBookingList.add(name);

		ele = findCss(("#HighendBook_rep_kana"));
		String phonetic = "8898988787867676";
		ele.sendKeys(phonetic);
		optionBookingList.add(phonetic);

		ele = findCss(("#HighendBook_rep_email"));
		String mail = "nhatthuyld@gmail.com";
		ele.sendKeys(mail);
		optionBookingList.add(mail);

		ele = findCss(("#HighendBook_rep_tel01"));
		String phone = "46363636363";
		ele.sendKeys(phone);
		optionBookingList.add(phone);

		ele = findCss(("#HighendBook_rep_postal_code"));
		String postcode = "1234567890";
		ele.sendKeys(postcode);
		optionBookingList.add(postcode);

		ele = findCss(("#HighendBook_rep_addr01"));
		String address1 = "nguyen thien thuat";
		ele.sendKeys(address1);
		optionBookingList.add(address1);

		ele = findCss(("#HighendBook_rep_addr02"));
		String address2 = "bui thi xuan";
		ele.sendKeys(address2);
		optionBookingList.add(address2);
		// click to choose birthday

		clickButtonCss("#select2-HighendBook_birthday_year-container");
		clickButtonCss("#HighendBook_birthday_year option:nth-child(2)");

		clickButtonCss("#select2-HighendBook_birthday_month-container");
		clickButtonCss("#HighendBook_birthday_month option:nth-child(4)");

		clickButtonCss("#select2-HighendBook_birthday_day-container");
		clickButtonCss("#HighendBook_birthday_day option:nth-child(5)");

		String year = findCss("#select2-HighendBook_birthday_year-container").getText().trim();
		String month = findCss("#select2-HighendBook_birthday_month-container").getText().trim();
		String day = findCss("#select2-HighendBook_birthday_day-container").getText().trim();
		optionBookingList.add(year + "/" + month + "/" + day);

		Select dropdownbox = new Select(driver.findElement(By.cssSelector("#HighendBook_source_id")));
		dropdownbox.selectByIndex(3);

	}

	// check total price on booking page
	public void checkTotalPriceBooking() throws InterruptedException {

		String taxpr;
		// check total of 1 person
		int sumOptionPrice = Integer.parseInt(
				driver.findElement(By.cssSelector("#product-" + iddress + " #option_cost")).getAttribute("data-value"));

		// add total price of 1 person
		totalPrice += sumOptionPrice;

		// add with photo price
		totalPrice += Integer.parseInt(findCss("#book_option_cost").getAttribute("data-value"));

		// add early fee or discount fee
		totalPrice += earlyDiscountFee;

		// compare total price
		Assert.assertEquals("Fail-check Total price all dress is wrong " + totalPrice, Boolean.TRUE,
				totalPrice == Integer.parseInt(findCss("#total_cost_reduced").getAttribute("data-value")));

		// get and compare with total price after tax of price
		getPriceTax = (int) (totalPrice * 1.08);
		taxpr = findCss("#total_cost_reduced_tax").getText().replace("￥", "").replace(",", "");
		PriceTax = Integer.parseInt(taxpr);
		System.out.println("total price after tax=" + getPriceTax);
		Assert.assertEquals("Fail-check Total price after tax  of dress " + iddress + "is wrong ", Boolean.TRUE,
				getPriceTax == PriceTax);

	}

}