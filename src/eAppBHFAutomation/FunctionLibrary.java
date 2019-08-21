package eAppBHFAutomation;

import bhfUtility.Log;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FunctionLibrary {
	static int TimeOutSeconds; //Time to wait for the elements to be available
	static WebDriver driver;
	//static String[] identifiers;
	static HashMap<String, String> identifiers = new LinkedHashMap<String, String>();
	static By by;
	static By byClientMsg;
	static String variableLocator;
	static String ScreenShotPath;
	static String TestCaseID;
	static int TestStepNo;
	static String ScreenName;
	static String FieldName;
	static int error_count;
	//static WebElement wbElement;
	
	//Login Function
	public static void login(String browser_type, String driver_path,String url, String uname, String pwd, int timeout)
	{  
		TimeOutSeconds = timeout;
		if (browser_type.equalsIgnoreCase("Chrome"))
		{
			System.setProperty("webdriver.chrome.driver", driver_path + "/chromedriver.exe");
			driver = new ChromeDriver();
		}
		if (browser_type.equalsIgnoreCase("IE"))
		{
			System.setProperty("webdriver.ie.driver", driver_path + "/IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		}
		
		driver.manage().timeouts().pageLoadTimeout(TimeOutSeconds,TimeUnit.SECONDS);
		//driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);

		try
		{		
			driver.get(url);
			driver.manage().window().maximize();
			Log.info("Launch Console Page Title: " + driver.getTitle());
			//System.out.println("Launch Console Page Title: " + driver.getTitle());
			Thread.sleep(2000);  // Let the user actually see something!
			//System.out.println(driver.findElement(By.xpath("//*[@id='index_box']/div[@class='logo']/div/span")).getText());
			if ((driver.findElement(By.xpath("//*[@id='index_box']/div[@class='logo']/div/span")).getText().equals("Welcome to: Brighthouse e-App"))) {
			Log.info("Launch Console displayed..");	
			}
			else {
				throw new NoSuchElementException("Application not displayed");
			}
			
			Set<String> windowHandles = driver.getWindowHandles();
			if (windowHandles.size()>1) {
				Log.info("Login Page is opened automatically. Changing drive to Login Console");
				Log.info("-----Open Windows Titles-----");
				for(String winHandle : windowHandles){
				//System.out.println(winHandle);
				driver.switchTo().window(winHandle);
				Log.info(driver.getTitle());
			}}
			else {
				Log.info("Login Page is not opened automatically. Clicking lauch button in Launch Console to open Login Page.");
				new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(By.id("launchBtn")));
				driver.findElement(By.id("launchBtn")).click();
				for(String winHandle : windowHandles){
					driver.switchTo().window(winHandle);
				}
			}
			
			Log.info("Driver changed to Login Console");
			Log.info("Login Console Page Title: " + driver.getTitle());
			waitForAjax();
			new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
			driver.manage().window().maximize();
			driver.findElement(By.name("username")).sendKeys(uname);
			driver.findElement(By.name("password")).sendKeys(pwd);
			driver.findElement(By.name("_eventId__logon")).click();
		}
		catch (Exception e){
			driver.close();
			driver.quit();
			throw new NoSuchElementException("Application not displayed");
		}

	}

	//Logout Function
	public static void logout() {
		String[] currHandle= new String[5];
		int i=0;
		//System.out.println("Went to logout java");
		Log.info("Went to logout java");
		for(String winHandle : driver.getWindowHandles()){
			currHandle[i++]=winHandle;
		}
		driver.switchTo().window(currHandle[1]);

		try{
			new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.id("logout")))).click();
			//System.out.println("Logout is clicked");
			Log.info("Logout is clicked");
			//Handle Popup	
			Alert alert = driver.switchTo().alert();
			alert.accept();
			driver.switchTo().window(currHandle[0]);
			driver.quit();
		}
		catch (Exception e){
			Log.error("Error in Logout. Forcefully closing  the browser");
			Log.error(e);
			driver.close();
			driver.quit();
			
		}
	}

	//FreshCase - Closing the already opened window
	public static void freshCase() {
		String[] currHandle = new String[driver.getWindowHandles().size()];
		int i=0;
		//System.out.println("Went to FreshCase");
		Log.info("Went to FreshCase");
		for(String winHandle : driver.getWindowHandles()){
			currHandle[i++]=winHandle;
		}

		if (i>2)
			driver.close();	
		driver.switchTo().window(currHandle[1]);
		/*WebElement cancelBtn = null;
		try{
			System.out.println("In Try block in Fresh Case");
           //cancelBtn = driver.findElement(By.xpath("//input[@type='button' and @value='Cancel']")); 
           cancelBtn =  new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='button' and @value='Cancel']")));
           cancelBtn.click();
           waitForAjax();
        }
        catch(Exception e){
        	System.out.println("In Catch block in Fresh Case");
        	if (!(cancelBtn == null)) {
        		System.out.println("Cancel button is not NULL. Performing new login");
        		Log.error("Error in freshCase in FunctionLibrary class. Not able to click cancel button");
    			e.printStackTrace();
    			Log.error(e.toString());   
    			driver.close();	
    			driver.quit();
    			AutomationDriver.login();
    			
        	}	
        }*/
	}

	public static void executeStep(HashMap<String, String> testData, int currentRow, int columnNo, String identifier_fileName, boolean freshCase) throws IOException 
	{
		//Cleaning additional window for fresh case
		if (freshCase)
			freshCase();

		ScreenName = testData.get("ScreenName");
		FieldName = testData.get("FieldName");
		String step = testData.get("Step");
		String data = testData.get("Data");
		variableLocator = data;
		
		try {
			if (ScreenName.equals("") || FieldName.equals("")) {
				//error_count++;
				Report.PutFailWithoutScreenShot("Screen Name or Field name not provided for " + TestCaseID + " Step No. " + TestStepNo);
				Log.info("Screen Name or Field name not provided for " + TestCaseID + " Step No. " + TestStepNo);	
				return;
			}
			else {
				if (AutomationDriver.allLocators.containsKey(ScreenName + "." + FieldName)) {
			//identifiers = ReadPageManager.getLocators(identifier_fileName, ScreenName, FieldName);
				identifiers = (AutomationDriver.allLocators).get(ScreenName + "." + FieldName);
				Log.info("Identifier for " + FieldName + " in Page " + ScreenName);
				Log.info(identifiers);
				}
				else {
					Report.PutFailWithoutScreenShot("Not able to find identifier " + ScreenName + "." + FieldName
				+ " for " + TestCaseID + " Step No. " + TestStepNo);
					Log.info("Not able to find identifier " + ScreenName + "." + FieldName
							+ " for " + TestCaseID + " Step No. " + TestStepNo);	
					return;
				}
			}
		}catch (Exception e)
		{
			Log.error("Error in getting identifier in executeStep in FunctionLibrary class for " + TestCaseID + " Step No. " + TestStepNo);
			Log.error(e);
			return;
		}


		try {
			by = getByClass(identifiers.get("Identifier"), identifiers.get("Locator"));//setting by for field
			byClientMsg = getByClass(identifiers.get("Client_Side_Message_Identifier"), identifiers.get("Client_Side_Message_Locator"));//setting by for corresponding client side message
		}catch (Exception e){
			Log.error("Error in getByClass in executeStep in FunctionLibrary class for " + TestCaseID + " Step No. " + TestStepNo);
			Log.error(e);
			return;}

		switch (step.toUpperCase())
		{
		case "SETVALUE":
			setValue(identifiers.get("Field_Type"), data);
			break;
		case "GETVALUE":
			getValue(identifiers.get("Field_Type"), currentRow, columnNo);
			break;
		case "CLICK":
			clickButton();
			break;
		case "VERIFYCLIENTMESSAGE":
			try {
				verifyClientMsg(identifiers.get("Field_Type"), data);
			} catch (IOException e) {
				Log.error("Error in verifyClientMsg Function in FunctionLibrary Class: " + e.toString());
				Log.error(e);
			}
			break;
		case "VERIFYSERVERMESSAGE":
		try {
			verifyServerMsg(data);
		} catch (IOException e) {
			Log.error("Error in verifyServerMsg Function in FunctionLibrary Class: " + e.toString());
			Log.error(e);
		}
		break;
		case "VERIFYVALUE":
			verifyValue(identifiers.get("Field_Type"), data);
			break;
		case "VERIFYENABLED":
			verifyEnabled(data);
			break;
		case "VERIFYVISIBLE":
		verifyVisible(data);
		break;
		case "VERIFYPOLICYSTATUS":
		verifyPolicyStatus(data);
		break;
		case "SELECTFUND":
			selectFund(data);
			break;
		case "VERIFYFUNDNAME":
			verifyFundName(data);
			break;
		case "SELECTPLAN":
			selectPlan(data);
			break;
		case "OPENPOLICYBYNAME":
			if (ScreenName.equalsIgnoreCase("IN PROGRESS")){
				openPolicy("IN PROGRESS","name",data);}
			if (ScreenName.equalsIgnoreCase("SUBMITTED")){
				openPolicy("SUBMITTED","name",data);}	
			break;
		case "OPENPOLICYBYPOLICYNO":
			if (ScreenName.equalsIgnoreCase("IN PROGRESS")){
				openPolicy("IN PROGRESS","policy",data);}
			if (ScreenName.equalsIgnoreCase("SUBMITTED")){
				openPolicy("SUBMITTED","policy",data);}
			break; 
		default:
			//error_count++;
			Report.PutFail("Undefined action item  found: '" + step + "' for test case: " + TestCaseID + " and StepNo: " + TestStepNo);
			Log.info("Undefined action item  found: '" + step + "' for test case: " + TestCaseID + " and StepNo: " + TestStepNo);
			break;
		}

	}

	public static void setValue(String fieldtype, String data) throws IOException
	{
		try {
			WebElement wbElement;
			Select temp_ddlb;
			List<WebElement> wbElementList;

			switch (fieldtype.toUpperCase()) {
			case "TEXTBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				wbElement.sendKeys(data);
				wbElement.sendKeys(Keys.TAB);
				waitForAjax();
				break;
			case "DDLB":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				temp_ddlb= new Select(wbElement);	
				temp_ddlb.selectByVisibleText(data);
				waitForAjax();
				wbElement.sendKeys(Keys.TAB);
				//pressTab();
				break;
			case "RADIOOPTION":
				wbElementList= new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(driver.findElements(by)));
				setRadioOptions(wbElementList, data);
				waitForAjax();
				break;
			case "CHECKBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
				if (data.equalsIgnoreCase("YES")){//Checking if Select or not to select
					if ( !(wbElement.isSelected()) )
					{
						wbElement.click();
					}}
				if (data.equalsIgnoreCase("NO")){//Checking if Select or not to select
					if (wbElement.isSelected())
					{
						wbElement.click();
					}
				}
				waitForAjax();
				break;
			case "MULTISELECT-CHECKBOXES":
				WebElement tempElement;
				wbElementList = driver.findElements(by);
				//int list_size = wbElementList.size();
				ListIterator<WebElement> itr = wbElementList.listIterator();
				while (itr.hasNext()) {
					if (!(itr.next().isDisplayed())) {
						itr.remove();
					}
				}	
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(wbElementList.get(0)));
				if (data.equalsIgnoreCase("Check all")) {
					tempElement = wbElement.findElement(By.xpath("ancestor::div//span[text()='Check all']"));
					tempElement.click();
				}else if (data.equalsIgnoreCase("Uncheck all")) {
					tempElement = wbElement.findElement(By.xpath("ancestor::div//span[text()='Uncheck all']"));
					tempElement.click();
				}else
				{
					String[] multipleCB = data.split(",");
					//wbElementList = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
					Map<String, WebElement> listMap = new HashMap<String, WebElement>();
					for (WebElement ele:wbElementList) {
						String keyValue = ele.findElement(By.xpath("following-sibling::span")).getText();
						listMap.put(keyValue, ele);
					}
					for(String str: multipleCB) {
						if (listMap.containsKey(str.trim())){
							if (!listMap.get(str.trim()).isSelected()) {
							listMap.get(str.trim()).click();
							}
						}else {
							//error_count++;
							Report.PutFail("Error in putting " + str.trim() + " in " + ScreenName + "-" + FieldName);
						}
					}
				}
				break;
					
			default:
				break;

			}}
		catch (Exception e)
		{
			//error_count++;
			Log.error("Error in setValue for field type '" + fieldtype + "' and data '"+ data + "' in FunctionLibrary class");
			Log.error(e);
			Report.PutFail("Error in putting " + data + " in " + ScreenName + "-" + FieldName );
		}

	}

	public static void getValue(String fieldtype, int currentRow, int columnNo) throws IOException
	{
		try {
			WebElement wbElement;
			Select temp_ddlb;
			List<WebElement> wbElementList;
			String tempdata = "";
			switch (fieldtype.toUpperCase()) {
			case "TEXTBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata = wbElement.getAttribute("value");
				break;
			case "DDLB":
				temp_ddlb= new Select(new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by)));	
				tempdata = temp_ddlb.getFirstSelectedOption().getText();
				break;
			case "RADIOOPTION":
				wbElementList= new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(driver.findElements(by)));
				tempdata = getRadioOptions(wbElementList);
				break;
			case "CHECKBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
				if (wbElement.isSelected())
					tempdata = "Yes";
				else
					tempdata = "No";
				break;
			case "LABEL":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata=wbElement.getText();
				break;
			case "LINK":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata=wbElement.getText();
				break;
			case "BUTTON":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata = wbElement.getAttribute("value");
				break;
			default:
				break;
			}

			Log.info("Fetching test data '" + tempdata + "' from " + FieldName + " in " + ScreenName  
			+ " into cell (" + currentRow + "," + columnNo + ")");
			WriteTestData.setTestData(currentRow, columnNo , tempdata);
			Report.PutInfo("Fetching test data '" + tempdata + "' from " + FieldName + " in " + ScreenName  
					+ " into cell (" + currentRow + "," + columnNo + ") is successfull");
		}		

		catch (Exception e)
		{
			//error_count++;
			Log.error("Error in getValue for field type '" + fieldtype + "' and row id (" + currentRow + "," + columnNo + ") in FunctionLibrary class");
			Log.error(e);
			Report.PutFail("Error in reading from field '" + FieldName + "' from screen '" + ScreenName + 
					"', Check row id (" + currentRow + "," + columnNo + ")");
		}
	}

	public static void clickButton() throws IOException
	{
		try{
			WebElement wbElement;
			Set<String> no_of_windows_old = driver.getWindowHandles();
			wbElement = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
			if (wbElement.isEnabled()) {
			/*new Actions(driver).moveToElement(wbElement).perform();
			waitForAjax();*/
			wbElement.click();
			Set<String> no_of_windows_new = driver.getWindowHandles();
			//Switch to new page if new page opens
			if(no_of_windows_new.size() != no_of_windows_old.size()) {
			for(String winHandle : driver.getWindowHandles()){
				//System.out.println(winHandle);
				driver.switchTo().window(winHandle);
			}}
			waitForAjax();
		}
			else {
				//error_count++;
				Report.PutFail("Error in button click for field " + ScreenName + "-" + FieldName + " Button not enabled.");
			}
			}
		
		catch(Exception e)
		{
			//error_count++;
			Log.error("Error in clickButton in FunctionLibrary class");
			Log.error(e);
			Report.PutFail("Error in button click for field " + ScreenName + "-" + FieldName);
		}
	}

	public static void verifyClientMsg(String fieldtype, String message) throws IOException
	{
		String report_text = "";
		String actual_message = "";
		WebElement wbElement, wbElementField = null;
		report_text = "Verification of client side message for " + FieldName + 
				" in screen " + ScreenName + ", Expected: '" + message + "' ; Actual: '" ;
		try{
			wbElementField = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
			//wbElement = new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOfElementLocated(byClientMsg));
			//wbElement = new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOf((wbElementField.findElement(By.xpath("following-sibling::div[@class='validationMsg']/label")))));
			
			switch (fieldtype.toUpperCase()) {
			case "RADIOOPTION":
				wbElement = new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOf((wbElementField.findElement(By.xpath("parent::*/following-sibling::div[@class='validationMsg']/label")))));
				break;
			default:
				wbElement = new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOf((wbElementField.findElement(By.xpath("following-sibling::div[@class='validationMsg']/label")))));
				break;
			}
			
			waitForAjax();
			actual_message = wbElement.getText();
			report_text = report_text + actual_message + "'";

			if (actual_message.equals(message)) {
				scrollIntoWebElementMethod(wbElementField);
				Report.PutPass(report_text);
				//System.out.println("Last Name Pass"); 
			}
			else
			{
				//error_count++;
				scrollIntoWebElementMethod(wbElementField);
				Report.PutFail(report_text);
				//System.out.println("Last Name Fail");
			}

		}
		
		catch(Exception e){
			if (message=="") {
				if (wbElementField != null) {
					scrollIntoWebElementMethod(wbElementField);
					Report.PutPass("Verification of client side message for " + FieldName + 
							" in screen " + ScreenName + ", Expected: No Messsage, Actual: No Message");		
				}}
			else {
				//error_count++;
				Log.error("Error in verifyClientMsg in FunctionLibrary class " + e.toString());
				Log.error("Error in verifying Client Side message '" + message + "'");
				Report.PutFail("Error in verifying Client Side message for field " + ScreenName + "-" + FieldName);
				Log.error(e);
			}
		}
	}

	public static void verifyServerMsg(String message) throws IOException
	{
		String report_text = "";
		String actual_message = "";
		List<WebElement> actual_ser_msg;
		//System.out.println("Inside server msg verification. Expected: "+ message);
		int count=0;
		report_text = "Verification of server side message for field '" + FieldName + "' from screen '" + ScreenName + "'. Expected: '" + message + "' ; Actual: '" ;
		try{
			actual_ser_msg = driver.findElements(By.xpath("//div[@id='centerContent']/div[@id='eastDiv']//div[@id='Messages']/ul/li"));
			for(WebElement element:actual_ser_msg) {
				actual_message=element.getText();
				//System.out.println("Inside WebElement: " + actual_message);
				if (message.equals(actual_message)){
					scrollIntoWebElementMethod(element);
					if (count==0) {
					report_text = report_text + actual_message + "'";}
					count++;
				}
			}

			if(count==1) {
				Report.PutPass(report_text);	
			}
			else if (count > 1)
			{
				//error_count++;
				Report.PutFail(report_text + ". More than one message found.");
				
			}
			else if((message.trim()).equals("") && actual_ser_msg.size()==0 )
			{
				//error_count++;
				Report.PutPass(report_text);
				
			}
			else if (count==0)
			{
				//error_count++;
				Report.PutFail(report_text + "'. No such message found.");
				
			}

		}

		catch(NoSuchElementException e){
			if (message=="")
				Report.PutPass("Verification of server side message for field '" + FieldName + "' from screen '" 
			+ ScreenName + "'; Expected: No Messsage, Actual: No Message");	
		}
		catch(Exception e) {
				//error_count++;
				Log.error("Error in verifyServerMsg in FunctionLibrary class ");
				Log.error(e);
				Log.error("Error in verifying Server Side message '" + message + "'");
				Report.PutFail("Error in verifying Server Side message for field " + ScreenName + "-" + FieldName);
		}
	}
	
	public static void verifyValue(String fieldtype, String value) throws IOException
	{
		String report_text;
		report_text = "Verification of field value for " + FieldName +
				" in  Screen " + ScreenName + " , Expected: " + value + " , Actual: " ;
		try {
			WebElement wbElement;
			Select temp_ddlb;
			List<WebElement> wbElementList;
			String tempdata = "";
			switch (fieldtype.toUpperCase()) {
			case "TEXTBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata = wbElement.getAttribute("value");
				break;
			case "DDLB":
				temp_ddlb= new Select(new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by)));	
				tempdata = temp_ddlb.getFirstSelectedOption().getText();
				break;
			case "RADIOOPTION":
				wbElementList= new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(driver.findElements(by)));
				tempdata = getRadioOptions(wbElementList);
				break;
			case "CHECKBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
				if (wbElement.isSelected())
					tempdata = "Yes";
				else
					tempdata = "No";
				break;
			case "LABEL":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata=wbElement.getText();
				break;
			default:
				break;
			}
			report_text=report_text+tempdata;

			if (tempdata.equalsIgnoreCase(value)){
				Report.PutPass(report_text);}
			else {
				Report.PutFail(report_text);
			}
		}		

		catch (Exception e)
		{
			//error_count++;
			Log.error("Error in verifyValue in FunctionLibrary class");
			Log.error(e);
			Report.PutFail("Error in verifying value for field '" + FieldName + "' from screen '" + ScreenName);
		}
	}

	public static void verifyEnabled(String value) throws IOException
	{
		try{
			List<WebElement> wbElement;
			wbElement = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
			int enabled_count=0;
			int disabled_count=0;
			for(WebElement wb:wbElement) {
				if (wb.isEnabled())
					enabled_count++;
				else
					disabled_count++;	

			}
			if(value.equalsIgnoreCase("YES")) {
				if (disabled_count==0)
					Report.PutPass("Verification of enable/disable for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Enabled; Actual: Enabled.");
				else
					Report.PutFail("Verification of enable/disable for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Enabled; Actual: Disabled.");
			}
			if(value.equalsIgnoreCase("NO")) {
				if (enabled_count==0)
					Report.PutPass("Verification of enable/disable for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Disabled; Actual: Disabled.");
				else
					Report.PutFail("Verification of enable/disable for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Disabled; Actual: Enabled.");
			}

		}
		catch(Exception e)
		{
			//error_count++;
			Log.error("Error in verifyEnabled in FunctionLibrary class");
			Log.error(e);
			Report.PutFail("Error in enable/disable verification for field '" + FieldName + "' in screen '" + ScreenName + "'");
		}
	}

	public static void verifyVisible(String value) throws IOException
	{
		try{
			List<WebElement> wbElement;
			//wbElement = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
			wbElement = driver.findElements(by);
			int visible_count=0;
			int invisible_count=0;
			for(WebElement wb:wbElement) {
				if (wb.isDisplayed())
					visible_count++;
				else
					invisible_count++;	

			}
			if(value.equalsIgnoreCase("YES")) {
				if (invisible_count==0)
					Report.PutPass("Verification of visiblity for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Visible; Actual: Visible.");
				else
					Report.PutFail("Verification of visiblity for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Visible; Actual: Invisible.");
			}
			if(value.equalsIgnoreCase("NO")) {
				if (visible_count==0)
					Report.PutPass("Verification of visiblity for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Invisible; Actual: Invisible.");
				else
					Report.PutFail("Verification of visiblity for '"+ FieldName + "' in screen '" + 
							ScreenName + "'. Expected: Invisible; Actual: Visible.");
			}

		}
		catch(Exception e)
		{
			//error_count++;
			Log.error("Error in verifyVisible in FunctionLibrary class");
			Log.error(e);
			Report.PutFail("Error in visiblity verification for field '" + FieldName + "' in screen '" + ScreenName + "'");
		}
	}
	
	//selectFund
	public static void selectFund(String value) throws IOException
	{
		String[] temp;
		if (value.contains(",")){
			temp=value.split(",");
			String fundId = temp[0].trim();
			String fundPercentage = temp[1].trim();
			
			try{
				WebElement fundField;
				variableLocator = fundId;
				by = getByClass(identifiers.get("Identifier"), identifiers.get("Locator"));
				fundField =  new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				fundField.sendKeys(fundPercentage);
				fundField.sendKeys(Keys.TAB);
				waitForAjax();
				}
			catch(Exception e)
			{  //error_count++;
				Log.error("Error in selectFund in FunctionLibrary class");
				Log.error(e);
				Report.PutFail("Error in Fund Selection for fund Id: " + fundId + " in screen '" + ScreenName + "'");
			}
			
		}
		else
		{	//error_count++;
			Report.PutFail("Test data error for fund Selection at " + FieldName + "' in screen '" + ScreenName + "Expected data in format (fundId, fund%) but got " + value);
		}
	}
	
	//selectFund
		public static void verifyFundName(String value) throws IOException
		{
			String[] temp;
			String report_text;
			if (value.contains(",")){
				temp=value.split(",");
				String fundId = temp[0].trim();
				String fundName = temp[1].trim();
				report_text = "Verification of fund name for " + fundId +
						" in  Screen " + ScreenName + " , Expected: '" + fundName + "' , Actual: '" ;
				try{
					WebElement fundField;
					Select fundDDLB;
					variableLocator = fundId;
					by = getByClass(identifiers.get("Identifier"), identifiers.get("Locator"));
					fundField =  new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
					fundDDLB = new Select(fundField);
					String actualFundName = fundDDLB.getFirstSelectedOption().getText();
					report_text = report_text + actualFundName + "'";
					if (actualFundName.equalsIgnoreCase(fundName)){
						Report.PutPass(report_text);}
					else {
						Report.PutFail(report_text);
					}
					}
				catch(Exception e)
				{  //error_count++;
					Log.error("Error in verifyFundName in FunctionLibrary class");
					Log.error(e);
					Report.PutFail("Error in Fund Name Verification for fund Id: " + fundId + " in screen '" + ScreenName + "'");
				}
				
			}
			else
			{	//error_count++;
				Report.PutFail("Test data error for fund Name Verification at " + FieldName + "' in screen '" + ScreenName + "Expected data in format (fundId, fundName) but got " + value);
			}
		}
	
	
	//Verify policy Status. value should be in format (PolicyNumber, Status)
	@SuppressWarnings("deprecation")
	public static void verifyPolicyStatus(String value) throws IOException
	{
		String[] temp;
		if (value.contains(",")){
			temp=value.split(",");
			String polNumber = temp[0].trim();
			String polStatus = temp[1].trim();
			
			try{
				WebElement wbElement;
				variableLocator = polNumber;
				by = getByClass(identifiers.get("Identifier"), identifiers.get("Locator"));
				
				WebElement refreshBtn =  driver.findElement(By.xpath("//table/tbody/tr/td[@title='Reload Grid']/div[text()='Refresh']"));
				refreshBtn.click();
				waitForAjax();
				
				//WebElement tempWbElement = driver.findElement(by);
				if (driver.findElements(by).size()==0 || polNumber.equals("")){
					//error_count++;
						Report.PutFail("Verification of PolicyStatus for '"+ polNumber + "' in screen '" + 
								ScreenName + "'. Failed to find the Policy Number");
				}
				else {
				Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
							.withTimeout(15, TimeUnit.MINUTES)
							.pollingEvery(10, TimeUnit.SECONDS)
							.ignoring(NoSuchElementException.class);
							
				//wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				wbElement = wait.until(new Function<WebDriver, WebElement>(){
					 public WebElement apply(WebDriver driver) {
						WebElement refreshBtn =  driver.findElement(By.xpath("//table/tbody/tr/td[@title='Reload Grid']/div[text()='Refresh']"));
						refreshBtn.click();
						waitForAjax();
						Log.info("Waiting for policy# " + polNumber + " Status to be changed to " + polStatus);
						//System.out.println("Waiting for Status to be changed to " + polStatus);
						WebElement tempWbElement = driver.findElement(by);
						String actualStatus;
						actualStatus = tempWbElement.getText();
						if(polStatus.equalsIgnoreCase(actualStatus) || (!("Acknowledged".equalsIgnoreCase(polStatus)) && !("Failed".equalsIgnoreCase(polStatus)))) {
							return tempWbElement;}	
						else 
						return null;}
						
				});
				String actualStatus;
				wbElement = driver.findElement(by);
				scrollIntoWebElementMethod(wbElement);
				highLighterMethod(wbElement);
				actualStatus = wbElement.getText();
				if(polStatus.equalsIgnoreCase(actualStatus)) {
						Log.info("Policy# " + polNumber + " Final Policy Status: " + actualStatus);
						Report.PutPass("Verification of PolicyStatus for '"+ polNumber + "' in screen '" + 
								ScreenName + "'. Expected: " + polStatus + ";" + " Actual: " + actualStatus);
						}
					else 
					{
						//error_count++;
						Log.info("Policy# " + polNumber + " Final Policy Status: " + actualStatus);
						Report.PutFail("Verification of PolicyStatus for '"+ polNumber + "' in screen '" + 
							ScreenName + "'. Expected: " + polStatus + ";" + " Actual: " + actualStatus);
					}	
			}}
			catch(Exception e)
			{
				//error_count++;
				Log.error("Error in verifyPolicyStatus in FunctionLibrary class");
				Log.error(e);
				Report.PutFail("Error in PolicyStatus verification for Policy: "+ polNumber + " in screen '" + ScreenName + "'");
			}
			
		}
		else
		{	//error_count++;
			Report.PutFail("Test data error for verifyPolicyStatus at " + FieldName + "' in screen '" + ScreenName + "Expected data in format (policynumber, status) but got " + value);
		}
		
	}
	
	//Highlight a webElement
	public static void highLighterMethod (WebElement element){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
	}
	
	//Scroll to a webElement
	public static void scrollIntoWebElementMethod (WebElement element){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);", element);
	}
	
	//Open application by Name/Policy Number. Works for both In-Progress and Submitted Index
	public static void openPolicy(String screenName,String byNameorPolicy, String data) throws IOException {
		List<WebElement> nameLink = new LinkedList<WebElement>();
		if (screenName.equals("IN PROGRESS")){
			//Finding element by Name where, click operations will be performed
			if (byNameorPolicy.equals("name")){
				nameLink = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
						By.xpath("//td[@aria-describedby='list2_clientName']/a[text()='" + data + "']")));	
				//new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
			}
			//Finding element by policy No. Name needs to be found where, click operations will be performed
			if (byNameorPolicy.equals("policy")){
				nameLink = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
						By.xpath("//td[@aria-describedby='list2_policyNumber' and text()=' " + data + 
						"']/preceding-sibling::td[@aria-describedby='list2_clientName']/a")));
			}

		}
		else if (screenName.equals("SUBMITTED")){
			if (byNameorPolicy.equals("name")){
				//This needs to be changed. BHF doesn't have a link in client name in Submitted Index.
				//For Ameritas need to change the xpath to 
				////td[@aria-describedby='submittedIndexGrid_clientName' and text()='Sahoo II, Soumendra']/a
				nameLink = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
						By.xpath("//td[@aria-describedby='submittedIndexGrid_clientName' and text()='" + data + "']")));
			}
			if (byNameorPolicy.equals("policy")){
				nameLink = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
						By.xpath("//td[@aria-describedby='submittedIndexGrid_policyNumber' and text()='" + 
						data + "']/preceding-sibling::td[@aria-describedby='submittedIndexGrid_clientName']")));
			}
		}else
		{
			Report.PutFailWithoutScreenShot("Invalid Page provided to Open Policy. Policy can be opened in Submitted or In Progress View. "
					+ "Screen Name Provided: " + screenName);
			Log.info("Data error in test case: " + TestCaseID + ", Step: " + TestStepNo);
			Log.info("Invalid Page provided to Open Policy. Policy can be opened in Submitted or In Progress View. "
					+ "Screen Name Provided: " + screenName);
			return;

		}
		if (nameLink.size()==0) {
			Report.PutFail("Not able to find policy with provided " + byNameorPolicy + " in " + screenName + " Index. Data provided: " + data);
		}
		else
		{	//Multiple elements can be found if searching by Name. hence we will open only 1st element.
			Set<String> no_of_windows_old = driver.getWindowHandles();
			if (nameLink.get(0).isEnabled()) {
				nameLink.get(0).click();
				waitForAjax();
				Set<String> no_of_windows_new = driver.getWindowHandles();
				if(no_of_windows_new.size() != no_of_windows_old.size()) {
					for(String winHandle : driver.getWindowHandles()){
						//System.out.println(winHandle);
						driver.switchTo().window(winHandle);
					}
				}else
				{
					Report.PutFail("Error in opening the policy with provided " + byNameorPolicy + " in " + screenName + " Index. Data provided: " + data +
							". The link is not enable.");	
				}
			}
		}

	}
	
	//selectPlan
	public static void selectPlan(String planName) throws IOException
	{
			try{
				WebElement refreshBtn = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath("//table/tbody/tr/td[@title='Reload Grid']/div[text()='Refresh']")));
				refreshBtn.click();
				waitForAjax();
				List<WebElement> wbElement;
				WebElement nextNavigation, pageNo;
				Boolean nextEnabled=false;
				nextNavigation = driver.findElement(By.xpath("//*[@id='next_planListPager']"));
				wbElement =  driver.findElements(by);
				if (!nextNavigation.getAttribute("class").contains("ui-state-disabled")){
					nextEnabled=true;
				}
			while(wbElement.size()==0 && nextEnabled) {
				pageNo = driver.findElement(By.xpath("//*[@id='input_planListPager']/input"));
				Log.info(planName + " not found in Page: " + pageNo.getAttribute("value") + " in Product Grid. Navigating to next page" );
				nextNavigation.click();
				waitForAjax();
				nextNavigation = driver.findElement(By.xpath("//*[@id='next_planListPager']"));
				//Log.info("Class Attribute: " + nextNavigation.getAttribute("class"));
				//Log.info("Enabled: " + nextNavigation.isEnabled());
				if (!nextNavigation.getAttribute("class").contains("ui-state-disabled")){
					nextEnabled=true;
				}else
				{
					nextEnabled=false;
				}
				wbElement =  driver.findElements(by);
			}
			wbElement =  driver.findElements(by);
			if (wbElement.size()==0) {
				Log.info("Not able to find '"+ planName + "' in '" + FieldName + "' in '" + ScreenName + "' screen");
				Report.PutFail("Not able to find '"+ planName + "' in '" + FieldName + "' in '" + ScreenName + "' screen");	
			}
			else if (wbElement.size() > 1) {
				Log.info("More than one '"+ planName + "' found in '" + FieldName + "' in '" + ScreenName + "' screen");
				Report.PutFail("More than one '"+ planName + "' found in '" + FieldName + "' in '" + ScreenName + "' screen");		
				}
			else {
				scrollIntoWebElementMethod(wbElement.get(0));
				wbElement.get(0).click();
				highLighterMethod(wbElement.get(0));
				Report.PutPass("Selection of '"+ planName + "' in '" + FieldName + "' in '" + ScreenName + "' screen is successfull.");	
			}
			}
			catch(Exception e)
			{
				//error_count++;
				Log.error("Error in selectPlan in FunctionLibrary class");
				Log.error(e);
				Report.PutFail("Error in Plan Selection for plan: '"+ planName + "' in '" + FieldName + "' in '" + ScreenName + "' screen");
			}			
	}
		
	//Setting ByClass
	public static By getByClass(String identifier, String locator){

		By temp = null;
		if (locator.contains("+")) {
			String[] tempString = locator.split("\\+");
			//System.out.println("tempString length " + tempString.length);

			if (tempString.length == 2)
				locator=tempString[0] + variableLocator;
			if (tempString.length == 3)
				locator=tempString[0] + variableLocator + tempString[2];
		}	
		switch (identifier.toUpperCase())
		{
		case "NAME":
			temp = By.name(locator);
			break;
		case "ID":
			temp = By.id(locator);
			break;
		case "XPATH":
			temp = By.xpath(locator);
			break;
		default:
			break;

		}
		return temp;
	}

	//Wait method for Ajax call to be completed
	public static void waitForAjax() {
		new WebDriverWait(driver, TimeOutSeconds).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				JavascriptExecutor js = (JavascriptExecutor) d;
				return (Boolean) js.executeScript("return jQuery.active == 0");
			}
		});
	}

	//Method to set the Radio Options
	public static void setRadioOptions(List<WebElement> radio_element, String dataValue) throws IOException
	{
		WebElement temp;
		if (dataValue != "") {
			try{
				for(int i=0;i<radio_element.size();i++){
					radio_element.get(i).getLocation();	
					temp = radio_element.get(i).findElement(By.xpath("following-sibling::label"));
					//System.out.println("Value of radio element " + temp.getText());
					if (temp.getText().equalsIgnoreCase(dataValue))
					{
						pressTab();
						radio_element.get(i).click();
						//System.out.println("Radio option is clicked throuhg webdriver");
						return;
					}}
				Report.PutFail("Not able to select the radio option as " + dataValue + " for " + FieldName + " in screen " + ScreenName);
			}
			catch(Exception e)
			{
				//error_count++;
				Log.error("Error in setRadioOptions in FunctionLibrary class");
				Log.error(e);
				Report.PutFail("Not able to select the radio option as " + dataValue + " for " + FieldName + " in screen " + ScreenName);
			}}
	}

	public static String getRadioOptions(List<WebElement> radio_element)
	{
		WebElement temp;
		try{
			for(int i=0;i<radio_element.size();i++){
				if (radio_element.get(i).isSelected()) {
					radio_element.get(i).getLocation();	
					temp = radio_element.get(i).findElement(By.xpath("following-sibling::label"));
					//System.out.println("Radio Option is " + temp.getText());
					return temp.getText();
				}
			}

			//System.out.println("None of the Radio Option is selected");
			Log.info("None of the Radio Option is selected. getRadioOptions Function");
			return "";
		}

		catch(Exception e)
		{
			//error_count++;
			Log.error("Error in getRadioOptions in FunctionLibrary class");
			Log.error(e);
			return "";
		}

	}

	public static void setScreenShotPath(String screenshotPath){
		ScreenShotPath = screenshotPath;
	}


	public static void setTestCaseNoandStep(String TestCaseId, int stepNo){
		TestCaseID = TestCaseId;
		TestStepNo = stepNo;
	}

	public static String captureScreen(){
		String ScreenShotFileName;
		//Setting the Time Instance for the File Name
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		String temp = dateFormat.format(date);
		temp = temp.replace("/", "_");
		temp = temp.replace(" ", "_");
		temp = temp.replace(":", ""); //Report File Name ends
		ScreenShotFileName = TestCaseID + "_" + TestStepNo + "_" + temp + ".png";
		try {
			File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(src, new File(ScreenShotPath + "\\" + ScreenShotFileName));
			return ScreenShotPath + "\\" + ScreenShotFileName;
		}
		catch(Exception e){
			Log.error("Error in captureScreen in FunctionLibrary class: " + e.toString());
			Log.error(e);
			return "";
		}
	}

	public static void pressTab(){
		Actions mouse = new Actions(driver);
		mouse.sendKeys(Keys.TAB).build().perform();
		waitForAjax();
	}
}

