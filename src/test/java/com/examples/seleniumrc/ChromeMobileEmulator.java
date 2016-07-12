package com.examples.seleniumrc;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.examples.seleniumrc.util.PropertyReader;
import com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad;

public class ChromeMobileEmulator {
	
	WebDriver driver;
	@Test
	public void EmulatorDevice() throws InterruptedException {
		
		// here creating our first map for deviceName
		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", "Apple iPhone 5");
		
		//here creating the second map with key mobileEmulation
		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		
		//setting DesiredCapabilities for chrome
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		
		
		String driverUrl = PropertyReader.getValue("chromedriver");
		System.setProperty("webdriver.chrome.driver", driverUrl);
		driver = new ChromeDriver(capabilities);
		driver.manage().window().maximize();
		
		//opting mobile website...
		driver.get("https://kyotokimono-rental.com/reserve");
		//TimeLoadPage("https://kyotokimono-rental.com/reserve");
		waitForPageLoaded();
		driver.findElement(By.cssSelector(".kimono")).click();
		driver.findElement(By.xpath(".//*[@id='list_plans_1']")).click();
		driver.findElement(By.xpath(".//*[@id='list_plans_1']/option[2]")).click();
		driver.findElement(By.cssSelector("#add_plan")).click();
		
	}
	
	
	public void TimeLoadPage(String urlpage) throws InterruptedException {
		long start = System.currentTimeMillis();

		driver.get(urlpage);

		long finish = System.currentTimeMillis();
		long totalTime = finish - start; 
		System.out.println("Total Time for page load - "+totalTime); 
	}
	 public void waitForPageLoaded() {
	        ExpectedCondition<Boolean> expectation = new
	                ExpectedCondition<Boolean>() {
	                    public Boolean apply(WebDriver driver) {
	                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
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
}