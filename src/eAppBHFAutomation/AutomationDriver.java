package eAppBHFAutomation;

import bhfUtility.Log;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

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
	static int ColumnNo;
	static HashMap<String, String> testData = new LinkedHashMap<String, String>();

	public static void main(String[] args) throws IOException{
		//Read Property File
		Properties p = new Properties();
		try {
			p=ReadConfigFile.getObjectRepository();
		} catch (IOException e) {
			Log.error("Error in reading property file: " + e.toString());
			e.printStackTrace();
			return;
		}
		FILE_NAME = System.getProperty("user.dir")+"\\"+ p.getProperty("TestData_Filename");
		FieldIdentifier_Loc = System.getProperty("user.dir")+"\\"+ p.getProperty("FieldIdentifier_Filename");
		Report_Path = System.getProperty("user.dir")+"\\"+ p.getProperty("Report_Path");
		Screenshot_Path = System.getProperty("user.dir")+"\\"+ p.getProperty("Screenshot_Path");
		Driver_Path = p.getProperty("Driver_Path");
		browser_type = p.getProperty("Browser");
		eApp_URL = p.getProperty("URL");
		agent_ID = p.getProperty("agent_ID");
		agent_PWD = p.getProperty("agent_PWD");
		TimeOutSeconds = Integer.parseInt(p.getProperty("time_out_Second"));
		no_failed_steps_skip_case = Integer.parseInt(p.getProperty("no_failed_steps_to_skip_case"));
		//Property File Reading Ends

		//Login to application
		login();
		

		int total_TestCases = 0;
		//Get no of test cases to be executed
		try {
			total_TestCases = ReadTestData.getTestCases(FILE_NAME);
		} catch (IOException e) {
			Log.error("Error in calling ReadTestData.getTestCases in AutomationDriver class");
			e.printStackTrace();
			Log.error(e.getMessage());
			return;
		}

		//Get total no of rows in TestData sheet
		int totalnoofrows = 0;
		try{
			totalnoofrows= ReadTestData.getTotalNoOfRows(FILE_NAME);
		}
		catch (Exception e)
		{
			Log.error("Error in calling ReadTestData.getTotalNoOfRows in AutomationDriver class");
			e.printStackTrace();
			Log.error(e.getMessage());
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
			testCase = ReadTestData.getExecutable(FILE_NAME, i);
			String testCaseId = testCase.get("testCase");
			String testCaseExecutable = testCase.get("executable");
			boolean freshcase = true;
			FunctionLibrary.error_count = 0; //setting error count to 0 for fresh case
			int testcasecolumnno = -1;
			if (testCaseExecutable.equalsIgnoreCase("YES")) //Only test cases have Yes will be executed
			{
				int error_count_current_case;
				testcasecolumnno = ReadTestData.getTestCaseColumnNo(FILE_NAME, testCaseId);
				if (testcasecolumnno > 0) {
					Report.CreateTest(testCaseId);
					System.out.println("Starting Test Case " + testCaseId );
					Log.info("Starting Test Case " + testCaseId);
					int startRowOfTestCase = 2;
					Boolean continuestep = false;
					String data = "";
					do{				
						
						testData = ReadTestData.getTestCaseData(FILE_NAME, testcasecolumnno, startRowOfTestCase);
						
						data = testData.get("Data");
						int currentRowofTestCase = startRowOfTestCase;
						error_count_current_case=FunctionLibrary.error_count;
						if (!(data.trim().equalsIgnoreCase("END")))
							continuestep = true;
						else
							continuestep = false;
						if (error_count_current_case>no_failed_steps_skip_case) {
							continuestep = false;
							System.out.println("Terminating execution for Test Case " + testCaseId + " as error count exceed given count");
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
					System.out.println("Execution Completed for Test Case " + testCaseId );
					Log.info("Execution Completed for Test Case " + testCaseId);
				}
				else {
					System.out.println("Could not execute Test Case " + testCaseId + ". Test Data not found.");
					Log.info("Could not execute Test Case " + testCaseId + ". Test Data not found.");
				}
			}
		}

		//Building the report
		Report.BuildReport();
		Log.info("Report Building Completed");

		//logout of application
		try{
			Log.info("Porfiming Logout");
			FunctionLibrary.logout();
			Log.info("Logout Successfull");
		}
		catch (Exception e){
			e.printStackTrace();
			Log.error("Logout Unsuccessfull " + e.getMessage());
			return;}
	}
	
	public static void login() {
	//Login to Application
			try {
				Log.info("Initializing Browser for Automation");
				FunctionLibrary.login(browser_type, Driver_Path, eApp_URL, agent_ID, agent_PWD, TimeOutSeconds);
				Log.info("Browser Initialization Successfull");
			} catch (Exception e) {
				Log.error("Error in calling FunctionLibrary.login in AutomationDriver class");
				e.printStackTrace();
				Log.error(e.getMessage());
				return;
			}		//Login ends
	}
}