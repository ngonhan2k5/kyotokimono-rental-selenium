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


public class CheckFeeOnHolidayKimono {

	WebDriver driver;
	int numPerson;
	String nameShop;
	List listHoliday = new ArrayList(Arrays.asList("2016/01/01", "2016/01/11", "2016/02/11", "2016/03/20", "2016/03/21",
			"2016/04/29", "2016/05/03", "2016/05/04", "2016/05/05", "2016/07/18", "2016/09/19", "2016/09/22",
			"2016/10/10", "2016/11/03", "2016/11/23", "2016/12/23", "2017/01/01", "2017/01/02", "2017/01/09",
			"2017/02/11", "2017/03/20", "2017/04/29", "2017/05/03", "2017/05/04", "2017/05/05", "2017/07/17",
			"2017/09/18", "2017/09/23", "2017/10/09", "2017/11/03", "2017/11/23", "2017/12/23"));
	@SuppressWarnings("unchecked")
	List listIdKimono = new ArrayList(
			Arrays.asList("1",
					"2",
					"26",
					"3",
					"39",
					"35",
					"36",
					"4",
					"6",
					"7",
					"37",
					"8",
					"40"));

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
	public void checkMain() throws InterruptedException {
		System.out.println(
				"*********************************Test kimono standard*******************************************");

		numPerson = 1;

		// nameShop = "kyoto";
		// nameShop = "gionshijo";
		// nameShop = "osaka";
		//nameShop = "tokyo";

		// nameShop = "kamakura";
		// nameShop = "kinkakuji";
		// nameShop = "shinkyogoku";
		// nameShop = "kiyomizuzaka";
		nameShop="kanazawa";
		
		checkDressHolidayFee(listIdKimono);

		System.out
				.println("***************************************end test********************************************");

	}

	public void checkDressHolidayFee(List L) throws InterruptedException {
		for (int i = 0; i < L.size(); i++) {
//			// click tab kimono
//			Thread.sleep(3000);
//			findCss(".kimono").click();
//			Thread.sleep(500);
			
			String idplan = (String) L.get(i);
			seLectNumberPerson(numPerson, idplan);
			System.out.println("checking plan "+idplan+".....");

			// click next button
			clickButtonCss("#add_plan");
			Thread.sleep(3000);

			seLectShop(nameShop);
			Thread.sleep(3000);

			checkFeeDateTable();
			backAndAssertMessage();
			
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
			System.exit(0);//stop process
			return null;
		}
	}

	public int getRowTableDate(String s) throws InterruptedException {
		List<WebElement> rows = driver.findElements(By.xpath(s));
		int r = rows.size() - 1;// because it add a last row that is finished
								// head tag
		return r;
	}

	public void waitForPageLoaded(String previousurl) throws InterruptedException {
		String currenturl = driver.getCurrentUrl();
		while (previousurl.equals(currenturl)) {
			currenturl = driver.getCurrentUrl();
			Thread.sleep(100);
		}

	}
	
	public void waitforXpathEleLoaded(String idButton) throws InterruptedException {
		while(driver.findElement(By.xpath(idButton))==null)
		{
			Thread.sleep(100);
		}
					
	}
	
	public void waitforCssEleLoaded(String idButton) throws InterruptedException {
		while(driver.findElement(By.cssSelector(idButton))==null)
		{
			Thread.sleep(100);
		}
					
	}


	// select a number person with argument
	public void seLectNumberPerson(int number, String idplan) throws InterruptedException {
		
		Thread.sleep(1000);
		clickButtonXpath(".//*[@id='list_plans_" + idplan + "SelectBoxIt']/span[3]");
		Thread.sleep(200);
		number++;
		clickButtonXpath(".//*[@id='list_plans_" + idplan + "SelectBoxItOptions']/li[" + Integer.toString(number)
				+ "]/a/span[2]");
		Thread.sleep(500);
	}

	// get attribute by create a element and find xpath
	public String getAttributeElementXpath(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.xpath(s));
		String result = e.getAttribute(attb);
		return result;
	}

	public String getAttributeElementCss(String s, String attb) throws InterruptedException {
		WebElement e = driver.findElement(By.cssSelector(s));
		String result = e.getAttribute(attb);
		return result;
	}

	public Boolean check7month30(int indr, int indc) throws InterruptedException {
		String xpathcell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indr) + "]" + "/td["
				+ Integer.toString(indc) + "]/div";
		String holidaytext = getAttributeElementXpath(xpathcell, "data-time_date");
		if (holidaytext.equals("2016/7/30")) {
			return true;
		}
		return false;
	}

	public Boolean checkHoliday(int indr, int indc) throws InterruptedException {
		String xpathcell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indr) + "]" + "/td["
				+ Integer.toString(indc) + "]/div";
		String holidaytext = getAttributeElementXpath(xpathcell, "data-time_date");
		for (int i = 0; i < listHoliday.size(); i++) {
			String dateele = (String) listHoliday.get(i);
			if (holidaytext.equals(dateele)) {
				return true;
			}
		}

		return false;
	}

	public Boolean checkWeekend(int indr, int indc) throws InterruptedException {
		String xpathcell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(indr) + "]";
		String classthead = findXpath(
				".//*[@id='choose-date']/div[2]/div/table/thead/tr/td[" + Integer.toString(indc) + "]")
						.getAttribute("class");
		String classname = getAttributeElementXpath(xpathcell, "class");
		if ("odd odd-new even-early h0900  first-split ".equals(classname)
				|| "even even-new even-early h0930 ".equals(classname)) {
			if ("sat".equals(classthead) || "sun".equals(classthead)) {
				return true;
			}
		}

		return false;
	}

	// click date in present table
	public Boolean checkFeeOneDateTable() throws InterruptedException {
		int row = getRowTableDate(".//*[@id='choose-date']/div[2]/div/table/tbody/tr");
		for (int j = 1; j <= 7; j++) {

			for (int i = 1; i <= row; i++) {
				String parentClass = findXpath(
						".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(i) + "]")
								.getAttribute("class");

				if ("thead".equals(parentClass)) {
					continue;// if it is that continue with i++;
				}
				String xpathcell = ".//*[@id='choose-date']/div[2]/div/table/tbody/tr[" + Integer.toString(i) + "]"
						+ "/td[" + Integer.toString(j) + "]";
				//clickButtonXpath(xpathcell);
				//assertDate();
				if (checkWeekend(i, j) || checkHoliday(i, j)) {
					xpathcell += "/div/div";
					String textcell = findXpath(xpathcell).getText();
					if (!"×".equals(textcell) && !"-".equals(textcell) && !"☎".equals(textcell)) {
						xpathcell += "/span[2]";
						String fee = findXpath(xpathcell).getText().trim();

						if (!"+500円".equals(fee)) {
							System.out.println("Fail-cell is not fee" + xpathcell);
							return false;
						}
					}
				}
				if (check7month30(i, j)) {
					if (checkTokyoShop()) {
						xpathcell += "/div/div";
						String textcell = findXpath(xpathcell).getText();
						if (!"×".equals(textcell) && !"-".equals(textcell) && !"☎".equals(textcell)) {
							xpathcell += "/span[2]";
							String fee = findXpath(xpathcell).getText().trim();

							if ("-300円".equals(fee)) {
								System.out.println("Fail-cell is fee for 30/7 at kyoto shop" + xpathcell);
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	// Click another week if have not select a date whole table
	public void checkFeeDateTable() throws InterruptedException {

		clickButtonXpath(".//*[@id='page-next']");
		Thread.sleep(3000);
		int i = 1;
		while (i <= 65 && checkFeeOneDateTable()) {
			i++;
			clickButtonXpath(".//*[@id='page-next']");
			Thread.sleep(3000);
			//waitforCssEleLoaded("#choose-date");
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
		else if (shopname.equals("kanazawa")) {
		clickButtonCss("#choose-shop label[for='id-10-down']");
		return true;
	}

		return false;

	}

	public Boolean checkTokyoShop() {
		List<WebElement> firstAreaElements = driver.findElements(By.cssSelector("#choose-shop li"));
		for (WebElement element : firstAreaElements) {
			String liClass = element.getAttribute("class");
			if (liClass.contains("active") && liClass.contains("shop-8")) {
				return true;
			}
		}

		return false;
	}

	public void backAndAssertMessage() throws InterruptedException {
		// click back button
		findCss("#booking_back").click(); 
		// assert message

		assertNumberPerson();
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