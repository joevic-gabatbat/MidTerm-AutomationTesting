package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CommonFunctions {
	
	static WebDriver driver;
	static ExtentTest test;
	static ExtentReports report;
	static ConfigFileReader config = new ConfigFileReader();
	
	// This will get the date and time in yyyyMMddhhmmss format
	static String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	
	// This is for launching the browser by getting the browser type in config file
	public static WebDriver launchBrowser() {
		String browserName = config.getSpecificUrlProperties("browser");
		if (browserName.equalsIgnoreCase("Chrome")) {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
		} else if (browserName.equalsIgnoreCase("Safari")) {
			WebDriverManager.safaridriver().setup();
			driver = new SafariDriver();
		}
		return driver;
	}
	
	// This will generate the html for reports
	public static ExtentTest generateExtentReports() {
		File file = new File(dateName);
		file.mkdir();
		report = new ExtentReports(dateName + "/MidTermExecutionReport.html");
		test = report.startTest("Mid Term Demo");
		return test;
	}
	
	// This will close Extent and flush reports
	public static void closeExtentReports() {
		report.endTest(test);
		report.flush();
	}
	
	// This will capture a screenshot of the defect and log a status fail and attach the screenshot as proof
	public static String captureScreenShots() throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir") + "/MidTermFailedTestsScreenshots/" + "ScreenShots" + dateName
				+ ".png";
		test.log(LogStatus.FAIL, "Test Failed" + test.addScreenCapture(destination));
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}
	
}
