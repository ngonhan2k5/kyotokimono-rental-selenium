package com.examples.seleniumrc;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.examples.seleniumrc.util.PropertyReader;

public class AllLink {

	WebDriver driver;
	String currentURL = null;

	@Before
	public void setUp() throws Exception {
		String driverUrl = PropertyReader.getValue("chromedriver");
		System.setProperty("webdriver.chrome.driver", driverUrl);
		driver = new ChromeDriver();
		driver.manage().window().maximize();

	}

	@Test
	public void checkBookingKimono() throws InterruptedException {

		openLinkOnePage("https://kyotokimono-rental.com");

	}

	public void openLinkOnePage(String linkpage) throws InterruptedException {

		driver.get(linkpage);
		List<WebElement> all_links_webpage = driver.findElements(By.tagName("a"));
		System.out.println("Total no of links Available: " + all_links_webpage.size());
		int k = all_links_webpage.size();
		System.out.println("List of links Available: ");
		for (int i = 0; i < k; i++) {

			if (!(all_links_webpage.get(i).getText().isEmpty())) {

				all_links_webpage.get(i).click();
				// waitForPageLoaded(currentURL);
				currentURL = driver.getCurrentUrl();
				System.out.println(currentURL);
				driver.navigate().back();
				// waitForPageLoaded(currentURL);
				all_links_webpage = driver.findElements(By.tagName("a"));

			}
		}
	}

	public void waitForPageLoaded(String previousurl) throws InterruptedException {
		String currenturl = driver.getCurrentUrl();
		while (previousurl.equals(currenturl)) {
			currenturl = driver.getCurrentUrl();
			Thread.sleep(100);
		}

	}
}