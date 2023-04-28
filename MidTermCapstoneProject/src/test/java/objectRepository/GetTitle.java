package objectRepository;

import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetTitle {

	WebDriver driver;
	ExtentTest test;
	
	// Constructor
	public GetTitle(WebDriver driver, ExtentTest test) {
		this.driver = driver;
		this.test = test;
	}
	
	// This is get the title of a page then log status based on the given parameters
	public void getTitlePage(String expectedTitle, String logMsg) {
		String title = driver.getTitle();
		if (title.contains(expectedTitle)) {
			test.log(LogStatus.PASS, logMsg + " successfully. \nTitle contains: " + title);
		} else {
			test.log(LogStatus.FAIL, logMsg + " failed. \nTitle does not contains: " + title);
		}
	}
}
