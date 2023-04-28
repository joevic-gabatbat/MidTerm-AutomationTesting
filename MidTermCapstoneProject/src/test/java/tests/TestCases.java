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
		// launch browser
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
			WebElement edurekaImage = driver.findElement(By.xpath("//div[@class='navbar-headerw']//img"));
			if (edurekaImage.isDisplayed()) {
				test.log(LogStatus.PASS, "Application is launched successfully.");
			}
		} catch (Exception e) {
			test.log(LogStatus.PASS, "Application launched failed. \n" + e);
		}

	}

	@Test(priority = 2)
	public void getRegisterModal() {

		driver.findElement(By.xpath("//span[@data-button-name='Signup']")).click();

		ReadDataFromExcel excel = new ReadDataFromExcel();
		String username = excel.getDataFromExcel(excelPath, sheetName, 1, "Username");
		String contactNum = excel.getDataFromExcel(excelPath, sheetName, 1, "Contact Number");

		WebElement inputEmail = driver.findElement(By.xpath("//input[@type='text' and @id='sg_popup_email']"));
		inputEmail.sendKeys(username);

		WebElement inputContactNum = driver.findElement(By.xpath("//input[@type='tel' and @id='sg_popup_phone_no']"));
		inputContactNum.sendKeys(contactNum);

		driver.findElement(By.xpath("//button[@class='clik_btn_log btn-block signup-new-submit']")).click();

		try {
			WebElement emailErrorMsg = driver.findElement(By.xpath("//p[@class='errormsg_vd' and @id='emailError']"));
			if (emailErrorMsg.isDisplayed()) {
				test.log(LogStatus.INFO, "Account already registered.");
			}
		} catch (Exception e) {
			test.log(LogStatus.SKIP, "Account registered, skip to account validation. \n" + e);
		}

	}

	@Test(priority = 3)
	public void getLoginModal() {
		try {
			WebElement emailErrorMsg = driver.findElement(By.xpath("//p[@class='errormsg_vd' and @id='emailError']"));
			if (!emailErrorMsg.isDisplayed()) {
				//This will skip getLoginModal since after a successful sign up, account automatically signed in
				throw new SkipException("GetLoginModal Skip: Account already registered.");
			} else {
				Thread.sleep(10000);
				driver.findElement(By.xpath("//span[@class='login-vd']")).click();

				WebElement loginModalTitle = driver
						.findElement(By.xpath("//h4[@class='modal-title signup-new-title']"));

				if (loginModalTitle.isDisplayed()) {
					test.log(LogStatus.INFO, "Login modal is now displayed.");
				} else {
					test.log(LogStatus.FAIL, "Login modal is not displayed.");
					Assert.assertTrue(false);
				}
			}
		} catch (Exception e) {
			test.log(LogStatus.SKIP, "GetLoginModal Skip: Account already registered." + e);
		}

	}

	@Test(priority = 4)
	public void loginMethod() {
		try {
			WebElement emailErrorMsg = driver.findElement(By.xpath("//h4[@class='modal-title signup-new-title']"));
			if (!emailErrorMsg.isDisplayed()) {
				//This will skip loginMethod since after a successful sign up, account automatically signed in
				throw new SkipException("LoginMethod Skip: Account already registered.");
			} else {
				ReadDataFromExcel excel = new ReadDataFromExcel();
				String username = excel.getDataFromExcel(excelPath, sheetName, 1, "Username");
				String password = excel.getDataFromExcel(excelPath, sheetName, 1, "Password");

				WebElement inputEmail = driver.findElement(By.xpath("//input[@type='email' and @id='si_popup_email']"));
				inputEmail.sendKeys(username);

				WebElement inputPassword = driver
						.findElement(By.xpath("//input[@type='password' and @id='si_popup_passwd']"));
				inputPassword.sendKeys(password);

				driver.findElement(By.xpath("//button[@class='clik_btn_log btn-block']")).click();

				Thread.sleep(10000);
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
			test.log(LogStatus.SKIP, "LoginMethod Skip: Account already registered." + e);
		}

	}

	@Test(priority = 5)
	public void moveToMyProfile() throws Exception {

		driver.findElement(By.xpath("//span[@class='webinar-profile-name']")).click();
		driver.findElement(By.xpath("//a[@data-button-name='My Profile']")).click();

		Thread.sleep(10000);
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
			// By using finally, codes below will still execute regardless if there is ads
			// or not
			ReadDataFromExcel excel = new ReadDataFromExcel();
			String topic = excel.getDataFromExcel(excelPath, sheetName, 1, "Topic");
			driver.findElement(By.xpath("//a[contains(text(), 'Topics of Interest')]")).click();
			driver.findElement(By.xpath("//button[@class='btn btn-add-more']")).click();
			driver.findElement(By.xpath("//label[contains(text(), '" + topic + "')]")).click();
			driver.findElement(By.xpath("//button[@class='btn btn-default btn-lg btn-save pull-right btn_save']"))
					.click();

			WebElement topicLabel = driver.findElement(By.xpath("//label[contains(text(), '" + topic + "')]"));
			if (topicLabel.isDisplayed()) {
				test.log(LogStatus.PASS, "Topic added successfully with title: " + topic);
			} else {
				test.log(LogStatus.FAIL, "Topic add fail.");
			}
		}

	}
}
