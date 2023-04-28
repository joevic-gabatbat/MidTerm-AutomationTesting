package tests;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import objectRepository.GetTitle;
import utils.CommonFunctions;
import utils.ConfigFileReader;
import utils.ReadDataFromExcel;

public class TestCases {

	WebDriver driver;
	ConfigFileReader config = new ConfigFileReader();
	ExtentTest test;
	String excelPath = "./ExcelData/TestData.xlsx";
	String sheetName = "TestData - TestCase";

	@BeforeSuite
	public void beforeSuite() {
		// Launch browser
		driver = CommonFunctions.launchBrowser();
		test = CommonFunctions.generateExtentReports();
	}

	@SuppressWarnings("deprecation")
	@BeforeTest
	public void beforeTest() {
		// Get Url, establish time for implicit wait and set window to max
		String url = config.getSpecificUrlProperties("edurekaUrl");
		int implicitWait = config.getWaits("implicitWait");
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get(url);
		test.log(LogStatus.INFO, "Launch Url : " + url);
	}

	@AfterTest
	public void afterTest() throws IOException {
		// Close browser after all test regardless if there are failed tests
		test.log(LogStatus.INFO, "Close the browser Chrome");
		driver.close();
		driver.quit();
	}

	@AfterSuite
	public void afterClass() {
		// This will close Extent and flush reports
		CommonFunctions.closeExtentReports();
	}

	@Test(priority = 1)
	public void validateLaunch() {
		try {
			// This will check for the image of the edureka for verification
			WebElement edurekaImage = driver.findElement(By.xpath("//div[@class='navbar-headerw']//img"));
			if (edurekaImage.isDisplayed()) {
				test.log(LogStatus.PASS, "Application is launched successfully.");
			}
		} catch (Exception e) {
			test.log(LogStatus.FAIL, "Application launched failed. \n" + e);
		}

	}

	@Test(priority = 2)
	public void getRegisterModal() {
		
		// Find sign up button
		driver.findElement(By.xpath("//span[@data-button-name='Signup']")).click();
		
		// Read the excel file, get username and contact number
		ReadDataFromExcel excel = new ReadDataFromExcel();
		String username = excel.getDataFromExcel(excelPath, sheetName, 1, "Username");
		String contactNum = excel.getDataFromExcel(excelPath, sheetName, 1, "Contact Number");
		
		// Find input and send username
		WebElement inputEmail = driver.findElement(By.xpath("//input[@type='text' and @id='sg_popup_email']"));
		inputEmail.sendKeys(username);
		
		// Find input and send contact number
		WebElement inputContactNum = driver.findElement(By.xpath("//input[@type='tel' and @id='sg_popup_phone_no']"));
		inputContactNum.sendKeys(contactNum);
		
		// Click submit button
		driver.findElement(By.xpath("//button[@class='clik_btn_log btn-block signup-new-submit']")).click();

		try {
			// Check if credentials is existing
			WebElement emailErrorMsg = driver.findElement(By.xpath("//p[@class='errormsg_vd' and @id='emailError']"));
			if (emailErrorMsg.isDisplayed()) {
				test.log(LogStatus.INFO, "Account already registered.");
			}
		} catch (Exception e) {
			// Skip to Account Verification after registration
			test.log(LogStatus.SKIP, "Account registered, skip to account validation. \n" + e);
		}

	}

	@Test(priority = 3)
	public void getLoginModal() {
		try {
			// Check if credentials is existing
			WebElement emailErrorMsg = driver.findElement(By.xpath("//p[@class='errormsg_vd' and @id='emailError']"));
			if (!emailErrorMsg.isDisplayed()) {
				//This will skip getLoginModal since after a successful sign up, account automatically signed in
				throw new SkipException("GetLoginModal Skip: Account already registered.");
			} else {
				Thread.sleep(10000);
				
				// Find login button and click
				driver.findElement(By.xpath("//span[@class='login-vd']")).click();
				
				// Find sign up title
				WebElement loginModalTitle = driver
						.findElement(By.xpath("//h4[@class='modal-title signup-new-title']"));
				
				// Check if sign up title is displayed
				if (loginModalTitle.isDisplayed()) {
					test.log(LogStatus.INFO, "Login modal is now displayed.");
				} else {
					test.log(LogStatus.FAIL, "Login modal is not displayed.");
					Assert.assertTrue(false);
				}
			}
		} catch (Exception e) {
			//This will skip getLoginModal since after a successful sign up, account automatically signed in
			test.log(LogStatus.SKIP, "GetLoginModal Skip: Account already registered." + e);
		}

	}

	@Test(priority = 4)
	public void loginMethod() {
		try {
			// Check if credentials is existing
			WebElement emailErrorMsg = driver.findElement(By.xpath("//h4[@class='modal-title signup-new-title']"));
			if (!emailErrorMsg.isDisplayed()) {
				//This will skip loginMethod since after a successful sign up, account automatically signed in
				throw new SkipException("LoginMethod Skip: Account already registered.");
			} else {
				
				// Read the excel file, get username and password
				ReadDataFromExcel excel = new ReadDataFromExcel();
				String username = excel.getDataFromExcel(excelPath, sheetName, 1, "Username");
				String password = excel.getDataFromExcel(excelPath, sheetName, 1, "Password");
				
				// Find input and send username
				WebElement inputEmail = driver.findElement(By.xpath("//input[@type='email' and @id='si_popup_email']"));
				inputEmail.sendKeys(username);
				
				// Find input and send password
				WebElement inputPassword = driver
						.findElement(By.xpath("//input[@type='password' and @id='si_popup_passwd']"));
				inputPassword.sendKeys(password);
				
				// Find submit button and click
				driver.findElement(By.xpath("//button[@class='clik_btn_log btn-block']")).click();
				
				Thread.sleep(10000);

				// Validate the page by getting the title
				String stringMethod = "Login";
				String getTitle = driver.getTitle();
				String expectedTitle = "Instructor-Led Online Training with 24X7 Lifetime Support | Edureka";
				if (!getTitle.equals(null)) {
					GetTitle gt = new GetTitle(driver, test);
					gt.getTitlePage(expectedTitle, stringMethod);
				} else {
					test.log(LogStatus.FAIL, "Title is null.");
				}
			}
		} catch (Exception e) {
			//This will skip getLoginModal since after a successful sign up, account automatically signed in
			test.log(LogStatus.SKIP, "LoginMethod Skip: Account already registered." + e);
		}

	}

	@Test(priority = 5)
	public void moveToMyProfile() throws Exception {
		// Find profile button and click
		driver.findElement(By.xpath("//span[@class='webinar-profile-name']")).click();
		
		// Find My Profile button and click
		driver.findElement(By.xpath("//a[@data-button-name='My Profile']")).click();

		Thread.sleep(10000);
		
		// Validate the page by getting the title
		String stringMethod = "Verify My Profile";
		String getTitle = driver.getTitle();
		String expectedTitle = "My Profile | Edureka";
		if (!getTitle.equals(null)) {
			GetTitle gt = new GetTitle(driver, test);
			gt.getTitlePage(expectedTitle, stringMethod);
		} else {
			test.log(LogStatus.FAIL, "Title is null.");
		}

	}

	@Test(priority = 6)
	public void topicsOfInterest() throws Exception {
		try {
			// This will close the Ads when it is visible
			WebElement adsButton = driver.findElement(By.xpath("//button[@class='No thanks']"));
			if (adsButton.isDisplayed()) {
				adsButton.click();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Codes below will still execute regardless if there is ads or not
			
			// Read excel file and get topic
			ReadDataFromExcel excel = new ReadDataFromExcel();
			String topic = excel.getDataFromExcel(excelPath, sheetName, 1, "Topic");
			
			// Find Topics of Interest button and click
			driver.findElement(By.xpath("//a[contains(text(), 'Topics of Interest')]")).click();
			
			// Find Add button and click
			driver.findElement(By.xpath("//button[@class='btn btn-add-more']")).click();
			
			// Find topic based on the data retrieved from the excel file and concat then click
			driver.findElement(By.xpath("//label[contains(text(), '" + topic + "')]")).click();
			
			// Find Save button and click
			driver.findElement(By.xpath("//button[@class='btn btn-default btn-lg btn-save pull-right btn_save']"))
					.click();
			
			// Validate that the newly added topic is successful
			WebElement topicLabel = driver.findElement(By.xpath("//label[contains(text(), '" + topic + "')]"));
			if (topicLabel.isDisplayed()) {
				test.log(LogStatus.PASS, "Topic added successfully with title: " + topic);
			} else {
				test.log(LogStatus.FAIL, "Topic add fail.");
			}
		}

	}
}
