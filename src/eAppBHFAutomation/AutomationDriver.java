package eAppBHFAutomation;

import bhfUtility.Log;
import bhfUtility.SendEmail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.mail.MessagingException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*#####################################################################################################
Creator Name: Soumendra Prasad Sahoo
Company: DXC Technology
Date Created: 1st Aug 2019
Class: Automation Driver
Functionality: To drive the test automation
#####################################################################################################*/

public class AutomationDriver {
	static int testCaseNo;
	private static int TimeOutSeconds; // = 30; //Time to wait for the elements to be available
	private static String FILE_NAME; //= "D:/eApp Automation/eApp Test Data.xlsx"; //Path for Test Data
	private static String FieldIdentifier_Loc; //= "D:/eApp Automation/Object/PageIdentifier.xlsm"; //Path for Field Identifier Data
	private static String Report_Path; //= "D:/eApp Automation/Reports"; //Path for the report file to be generated
	private static String Screenshot_Path; //= "D:/eApp Automation/Screenshots"; //Path for the screenshots to be generated
	private static String Driver_Path; //= "C:/Users/ssahoo43/D_Drive/Drivers"; //Path for the Chrome/IE Drivers folder
	private static String eApp_URL; //= "http://20.15.86.50:8080/eApps/"; //Eapp URL
	private static String agent_ID; //= "ssahoo43"; //Agent ID
	private static String agent_PWD; //= "vilink"; //Password
	private static String browser_type; //Chrome or IE
	private static int no_failed_steps_skip_case; //2; Case execution will terminate if no of steps are failed > given no.
	private static String send_Email_Notification; //E-mail Notification Indicator
	static int ColumnNo;
	static boolean loginSuccessfull;
	static HashMap<String, String> testData = new LinkedHashMap<String, String>();
	static HashMap<String, String> testResult = new LinkedHashMap<String, String>();
	static HashMap<String, Integer> totalRows = new LinkedHashMap<String, Integer>();
	static HashMap<String, LinkedHashMap<String, String>> allLocators = new LinkedHashMap<String, LinkedHashMap<String, String>>();
	static HashMap<Integer, LinkedHashMap<String, String>> testSuite = new LinkedHashMap<Integer, LinkedHashMap<String, String>>();

	public static void main(String[] args) throws IOException{
		Log.info("-------------------------###############################################################------------------------");
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		Date date = new Date();  
		Log.info("Starting Automation: " + formatter.format(date) );
		//Read Property File
		Properties p = new Properties();
		try {
			p=ReadConfigFile.getObjectRepository();
		} catch (IOException e) {
			Log.error("Error in reading property file: " + e.toString());
			Log.error(e);
			return;
		}
		FILE_NAME = System.getProperty("user.dir")+"\\"+ p.getProperty("TestData_Filename");
		FieldIdentifier_Loc = System.getProperty("user.dir")+"\\"+ p.getProperty("FieldIdentifier_Filename");
		Report_Path = System.getProperty("user.dir")+"\\"+ p.getProperty("Report_Path");
		Screenshot_Path = System.getProperty("user.dir")+"\\"+ p.getProperty("Screenshot_Path");
		Driver_Path = System.getProperty("user.dir")+"\\" + p.getProperty("Driver_Path");
		browser_type = p.getProperty("Browser");
		eApp_URL = p.getProperty("URL");
		agent_ID = p.getProperty("agent_ID");
		agent_PWD = p.getProperty("agent_PWD");
		TimeOutSeconds = Integer.parseInt(p.getProperty("time_out_Second"));
		no_failed_steps_skip_case = Integer.parseInt(p.getProperty("no_failed_steps_to_skip_case"));
		send_Email_Notification = p.getProperty("send_email_notofication");
		
		//Property File Reading Ends

		//Checking Test Data File is already open or not. If not terminate the execution.
		File file = new File(FILE_NAME);
	    File sameFileName = new File(FILE_NAME);// try to rename the file with the same name
	    if(file.renameTo(sameFileName)){// if the file is renamed
	        Log.info("Test Data File is closed: " + FILE_NAME + ". Proceeding with execution.");
	    }else{// if the file didnt accept the renaming operation
	    	Log.info("Test Data File is not closed: " + FILE_NAME + ". Terminating execution.");
	    	return;
	    }//Test Data file checking ends
		
	    //Populating all Locators for all pages
	    Log.info("Reading Page manager and populating 'allLocators' map");
	    allLocators = ReadPageManager.getAllLocators(FieldIdentifier_Loc);
	    Log.info("'allLocators' map Populated with all values from Page Identifier Excel");
	    if (allLocators==null) {
	    	Log.error("Got some exception in reading the page identiofiers. Terminating execution.");
	    	return;
	    }
	   
	    //Terminating the code in middle
	   /*if (true){
		 return;  
	   }*/
	    
		//Login to application
		login();
		if (!loginSuccessfull){
			return;
		}

		int total_TestCases = 0;
		//Get no of test cases to be executed
		try {
			testSuite = ReadTestData.getTestCases(FILE_NAME);
			total_TestCases = testSuite.size();
		} catch (IOException e) {
			Log.error("Error in calling ReadTestData.getTestCases in AutomationDriver class");
			Log.error(e);
			return;
		}

		//Get total no of rows in all TestData sheet
		try{
			totalRows = ReadTestData.getTotalNoOfRows(FILE_NAME);
			//totalnoofrows= ReadTestData.getTotalNoOfRows(FILE_NAME);
		}
		catch (Exception e)
		{
			Log.error("Error in calling ReadTestData.getTotalNoOfRows in AutomationDriver class");
			Log.error(e);
			return;
		}

		//Initializing report in the 1st test case
		Log.info("Initialization Report");
		Report.InitializeReport(Report_Path);
		Log.info("Report Initialization Successfull");

		//Initializing Screenshot path in the 1st test case
		Log.info("Initializing Screenshot path");
		FunctionLibrary.setScreenShotPath(Screenshot_Path);
		Log.info("Screenshot Initialization Successfull");

		//Starting execution of test cases
		for(int i=1; i <= total_TestCases; i++)
		{
			
			HashMap<String, String> testCase = new LinkedHashMap<String, String>();
			testCase = testSuite.get(i);
			//testCase = ReadTestData.getExecutable(FILE_NAME, i);
			String testCaseId = testCase.get("TestCaseId");
			String testCaseExecutable = testCase.get("Executable");
			String testDataSheetName = testCase.get("TestDataSheet");
			WriteTestData.testDataSheetName = testDataSheetName;
			int totalnoofrows = totalRows.get(testDataSheetName);
			boolean freshcase = true;
			FunctionLibrary.error_count = 0; //setting error count to 0 for fresh case
			int testcasecolumnno = -1;
			if (testCaseExecutable.equalsIgnoreCase("YES")) //Only test cases having Yes will be executed
			{
				int error_count_current_case;
				testcasecolumnno = ReadTestData.getTestCaseColumnNo(FILE_NAME, testDataSheetName, testCaseId);
				if (testcasecolumnno > 0) {
					Report.CreateTest(testCaseId);
					//System.out.println("Starting Test Case " + testCaseId );
					Log.info("Starting Test Case " + testCaseId);
					int startRowOfTestCase = 2;
					Boolean continuestep = false;
					String data = "";
					do{				
						
						testData = ReadTestData.getTestCaseData(FILE_NAME, testDataSheetName, testcasecolumnno, startRowOfTestCase);
						data = testData.get("Data");
						int currentRowofTestCase = startRowOfTestCase;
						error_count_current_case=FunctionLibrary.error_count;
						if (!(data.trim().equalsIgnoreCase("END")))
							continuestep = true;
						else
							continuestep = false;
						if (error_count_current_case>no_failed_steps_skip_case) {
							continuestep = false;
							//System.out.println("Terminating execution for Test Case " + testCaseId + " as error count exceed given count");
							Log.info("Terminating execution for Test Case " + testCaseId + " as error count exceed given count");
						}
						if (!(data.equalsIgnoreCase("SKIP")) && continuestep)
						{
							FunctionLibrary.setTestCaseNoandStep(testCaseId, startRowOfTestCase); // Setting the TestCase id and row no in FuncationLibrary class
							FunctionLibrary.executeStep(testData, currentRowofTestCase,testcasecolumnno ,FieldIdentifier_Loc,freshcase);
							freshcase=false;
						}
						startRowOfTestCase++;
					} while ((continuestep) && (startRowOfTestCase <= totalnoofrows) );
					//System.out.println("Execution Completed for Test Case " + testCaseId );
					Log.info("Execution Completed for Test Case " + testCaseId);
					if (!(error_count_current_case > 0)) {
					testResult.put(testCaseId,"PASS");}
					else {
						testResult.put(testCaseId,"FAIL");	
					}
				}
				else {
					//System.out.println("Could not execute Test Case " + testCaseId + ". Test Data not found.");
					Log.info("Could not execute Test Case " + testCaseId + ". Test Data not found.");
					testResult.put(testCaseId,"DATA NOT FOUND");	
				}
			}else {
				Log.info(testCaseId + " is marked as " + testCaseExecutable + ". Skipping this case." );
				testResult.put(testCaseId,"SKIPPED");
			}
		}

		//Building the report
		Report.BuildReport();
		Log.info("Report Building Completed");

		//logout of application
		try{
			Log.info("Performing Logout");
			FunctionLibrary.logout();
			Log.info("Logout Successfull");
			
		}
		catch (Exception e){
			Log.error("Logout Unsuccessfull " + e.getMessage());
			Log.error(e);
			return;}
		
		//Send e-mail notification
		if (send_Email_Notification.equalsIgnoreCase("YES")) {
		try {
			Log.info("Sending e-mail of automation result....");
			SendEmail.send("ssahoo43@dxc.com", "ssahoo43@dxc.com", "Automation Test Result", testResult);
			Log.info("E-mail sent successfully");
		} catch (MessagingException e) {
			Log.error("Error in sending e-mail notification " + e.getMessage());
			Log.error(e);
			Log.error("E-mail sent - unsuccessfull");
		}	
		}
		
		date = new Date();  
		Log.info("Automation Execution Completed: " + formatter.format(date));
	}
	
	public static void login() {
	//Login to Application
			try {
				Log.info("Initializing Browser for Automation");
				FunctionLibrary.login(browser_type, Driver_Path, eApp_URL, agent_ID, agent_PWD, TimeOutSeconds);
				Log.info("Browser Initialization Successfull");
				loginSuccessfull=true;
			} catch (Exception e) {
				Log.error("Error in calling FunctionLibrary.login in AutomationDriver class. " + e.getMessage() );
				Log.error(e);
				loginSuccessfull=false;
			}		//Login ends
	}
}