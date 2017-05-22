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

public class TakuhaiBookingPC {

	WebDriver driver;
	int numPerson;
	WebElement dateElement;
	String shopName;
	String dressName;
	String xpathDateElement, indrDate, indcDate;
	String dateDelivery,dateReturn;
	String iddress;
	int indrFirstSelectedDate, indrLastSeletedDate, indcFirstSelectedDate, indcLastSeletedDate;
	String nameCustomer, emailCustomer, phoneCustomer, addressCustomer, birthdayCustomer, dateBooking, shopNameDetail,
			postercodeCustomer, feeEarlyDiscount;
	int priceDress;
	int totalPrice;
	int countDress=0;
	int lostDayFee=0;
	int sumPriceDress=0;

	@Before
	public void setUp() throws Exception {
		String driverUrl = PropertyReader.getValue("chromedriver");
		System.setProperty("webdriver.chrome.driver", driverUrl);
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://kyotokimono-rental.com/takuhai");
		Thread.sleep(4000);

	}

	// @After
	// public void tearDown() throws Exception {
	// driver.quit();
	// }

	@Test
	public void checkBookingKimonoStandard() throws InterruptedException {
		System.out.println(
				"*********************************Test kimono standard*******************************************");

		// click tab 1 of takuhai
		findCss(".dp-flex.scene-list li:nth-child(2) a").click();

		Thread.sleep(2000);
		
		//choose dress
		chooseOneDress(4);
		countDress++;
		int days=10;
		//choose date 
		chooseDateTable(days);
		//check information in product page
		checkProductPage(days);
		clickButtonCss(".btn");
		//check information in cart page
		checkCartPage();
		//input information customer
		inputInformationCustomer();
		clickButtonCss("#cart_confirm");
		
		System.out
				.println("***************************************end test********************************************");

	}
	public void inputInformationCustomer() throws InterruptedException {
		findCss("#TakuhaiBook_rep_name").sendKeys("thuy test");
		findCss("#TakuhaiBook_rep_kana").sendKeys("092636367373");
		findCss("#TakuhaiBook_rep_email").sendKeys("nhatthuy.we@gmail.com");
		findCss("#TakuhaiBook_rep_tel01").sendKeys("097873874287");
		findCss("#TakuhaiBook_rep_postal_code").sendKeys("3875843758");
		Select dropdownstate = new Select(driver.findElement(By.cssSelector("#TakuhaiBook_rep_prefecture")));
		dropdownstate.selectByIndex(2);
		findCss("#TakuhaiBook_rep_addr01").sendKeys("bui thi xuan");
		findCss("#TakuhaiBook_rep_addr02").sendKeys("nguyen thien thuat");
		clickButtonCss("#Delivery_other_add_flag");
		
		findCss("#Delivery_dlv_name").sendKeys("duc");
		findCss("#Delivery_dlv_tel").sendKeys("09783578785");
		findCss("#Delivery_dlv_postal_code").sendKeys("327525");
		Select dropdownstate2 = new Select(driver.findElement(By.cssSelector("#Delivery_dlv_prefecture")));
		dropdownstate2.selectByIndex(2);
		findCss("#Delivery_dlv_addr01").sendKeys("nguyen thiminh khai");
		Select dropdowntime = new Select(driver.findElement(By.cssSelector("#Delivery_arrive_time_opt")));
		dropdowntime.selectByIndex(2);
	}
	public void checkCartPage() throws InterruptedException{
		String getDateDelivery=findCss(".element.selected_dates li span").getText();
		Assert.assertEquals("Fail-check date delivery in cart page"+"\n"+getDateDelivery+"\n"+dateDelivery, Boolean.TRUE,dateDelivery.equals(getDateDelivery));
		String getDateReturn=findCss(".element.selected_dates li:nth-child(2) span").getText();
		Assert.assertEquals("Fail-check date return in cart page"+"\n"+getDateReturn+"\n"+dateReturn, Boolean.TRUE,dateReturn.equals(getDateReturn));
		
		//check name dress
		String getName=findCss(".dp-flex.clearfix li:nth-child("+countDress+") h4").getText().trim();
		Assert.assertEquals("Fail-check name "+countDress+" in cart page"+"\n"+getName+"\n"+dressName, Boolean.TRUE,getName.equals(dressName));
		
		String getprice=findCss(".dp-flex.clearfix li:nth-child("+countDress+") .cost").getText().trim().replace(",","").replace("￥","");
		Assert.assertEquals("Fail-check price "+countDress+" in cart page"+"\n"+getprice+"\n"+priceDress, Boolean.TRUE,getprice.equals(Integer.toString(priceDress)));
		
		//check sum price
		String getSumPriceDress=findCss(".element.prices .first li:nth-child(1) span").getText().trim().replace(",","").replace("￥","");
		Assert.assertEquals("Fail-check total dress price in cart page"+"\n"+getSumPriceDress+"\n"+sumPriceDress, Boolean.TRUE,getSumPriceDress.equals(Integer.toString(sumPriceDress)));
		//check lost day fee
		String getLostDayFee=findCss(".element.prices .first li:nth-child(2) span").getText().trim().replace(",","").replace("￥","");
		Assert.assertEquals("Fail-check lost day fee in cart page"+"\n"+getLostDayFee+"\n"+lostDayFee, Boolean.TRUE,getLostDayFee.equals(Integer.toString(lostDayFee)));
		//check cart subtotal 
		String getCartSubtotal=findCss(".element.prices .first li:nth-child(5) span").getText().trim().replace(",","").replace("￥","");
		Assert.assertEquals("Fail-check cart subtotal  in cart page"+"\n"+getCartSubtotal, Boolean.TRUE,getCartSubtotal.equals(Integer.toString(sumPriceDress+lostDayFee)));
		//check tax
		String getTax=findCss(".element.prices .first li:nth-child(6) span").getText().trim().replace(",","").replace("￥","");
		Assert.assertEquals("Fail-check tax total price  in cart page"+"\n"+getTax, Boolean.TRUE,getTax.equals(Integer.toString((int)((sumPriceDress+lostDayFee)*0.08))));
		//check total price 
		String getTotal=findCss(".element.prices .second li:nth-child(1) span").getText().trim().replace(",","").replace("￥","");
		Assert.assertEquals("Fail-check  total price  in cart page"+"\n"+getTotal, Boolean.TRUE,getTax.equals(Integer.toString((int)((sumPriceDress+lostDayFee)*0.08))));
		
		
	}
	public void checkProductPage(int days) throws InterruptedException{
		
		if(days>4)
			checkLateFee(days);
		checkNameAndPriceDress();
		//get date 
		dateDelivery=findCss("[id='arrival-date-container'] .title").getText().replace("年","/").replace("月","/").replace("日","").trim();
		dateDelivery=dateDelivery.substring(0,dateDelivery.length()-3).trim();
		dateReturn=findCss("[id='return-date-container'] .title").getText().replace("年","/").replace("月","/").replace("日","").trim();
		dateReturn=dateReturn.substring(0,dateReturn.length()-3).trim();
		if(days>4)
			lostDayFee=(days-4)*1000;
	}
	
	public void checkNameAndPriceDress() throws InterruptedException{
		String namedress=findCss(".name-price.clearfix .name").getText().replace(",","").replace("\n","");
		Assert.assertEquals("Fail-check name dress in product page\n"+namedress+"\n"+dressName, Boolean.TRUE,namedress.equals(dressName));
		String getprice="￥"+Integer.toString(priceDress)+" (税込 ￥"+Integer.toString((int)(priceDress*1.08))+")";
		String pricedress=findCss(".name-price.clearfix .price").getText().replace(",","");
		Assert.assertEquals("Fail-check price dress in product page"+"\n"+getprice+"\n"+pricedress, Boolean.TRUE,pricedress.equals(getprice));
		
	}
	public void checkLateFee(int days) throws InterruptedException{
		String dateFee=findCss("[id='delay_fee']").getText().replace("\n","").replace(",","");
		String lostDay=Integer.toString(days-4);
		String lateFee=lostDay+"000";
		int tax=(int) (Integer.parseInt(lateFee)*1.08);
		String lateFeeTax=Integer.toString(tax);
		String getDateFee="延滞料金 ("+lostDay+"日間) ￥"+lateFee+" (税込 ￥"+lateFeeTax+")";
		Assert.assertEquals("Fail-check late fee in product page:"+getDateFee, Boolean.TRUE,dateFee.equals(getDateFee));
	}
	public void chooseDateTable(int days) throws InterruptedException{
		clickButtonCss(".day.in-out.clearfix label");
		Thread.sleep(200);
		clickButtonCss("[id='product_datepicker'] .datepickerBlock:last-child .datepickerGoNext");
		Thread.sleep(1000);
		String tddelivery="5";
		String trdelivery="4";
		clickButtonCss("[id='product_datepicker'] .datepickerBlock:last-child .datepickerDays tr:nth-child("+trdelivery+") td:nth-child("+tddelivery+")");
		String  tdreturn;
		int tr;
		String trreturn=trdelivery;
		if(Integer.parseInt(tddelivery)+days-1>7)
		{
			 tr=Integer.parseInt(trdelivery)+1;
			 tdreturn=Integer.toString(Integer.parseInt(tddelivery)+days-7-1);
			 trreturn=Integer.toString(tr);
			
		}
		else
		{
			tdreturn=Integer.toString(Integer.parseInt(tddelivery)+days-1);
		}
		clickButtonCss("[id='product_datepicker'] .datepickerBlock:last-child .datepickerDays tr:nth-child("+trreturn+") td:nth-child("+tdreturn+")");
		
	}

	public void chooseOneDress(int indexdress) throws InterruptedException{
		
		
		iddress = findCss(".list.dp-flex li:nth-child(" + Integer.toString(indexdress) + ") img").getAttribute("p_id");
		priceDress=Integer.parseInt(findCss(".list.dp-flex li:nth-child(" + Integer.toString(indexdress) + ") .price").getText().replace("円","").replace("\n","").replace(",",""));
		dressName=findCss(".list.dp-flex li:nth-child(" + Integer.toString(indexdress) + ") .product-name").getText().replace("\n","");
		clickButtonCss(".list.dp-flex li:nth-child("+indexdress+") a");
		sumPriceDress+=priceDress;
	}

	

	public void waitForPageLoaded(String previousurl) throws InterruptedException {
		String currenturl = driver.getCurrentUrl();
		while (previousurl.equals(currenturl)) {
			currenturl = driver.getCurrentUrl();
			Thread.sleep(100);
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

	

	

}