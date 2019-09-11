package eAppBHFAutomation;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
//import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import bhfUtility.Log;

public class Report {
	static ExtentHtmlReporter report;
	static ExtentReports logger;
	static ExtentTest test;
	
	public static void InitializeReport(String filename_path) {
		//System.out.println("Initializing the report");
		String reportfilename = filename_path;
			//Setting the Report File Name
			 DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		     Date date = new Date();
		     String temp = dateFormat.format(date);
		     temp = temp.replace("/", "_");
		     temp = temp.replace(" ", "_");
		     temp = temp.replace(":", ""); //Report File Name ends
		     reportfilename = filename_path + "/Execution Report " + temp + ".html";
		report = new ExtentHtmlReporter(reportfilename);
		report.loadXMLConfig(new File("Object/ReportConfig.xml"));
		logger = new ExtentReports();	
		logger.attachReporter(report);
		logger.setSystemInfo("OS", "win10");
		logger.setSystemInfo("Language", "en");
		logger.setSystemInfo("Host Name", "Soumendra");
	}
	
	public static void CreateTest(String testcaseno) {
		test = logger.createTest("Test Case: "+ testcaseno);
	}
	
	public static void BuildReport() {
	logger.flush();
	//System.out.println("Report publishing completed");
	Log.info("Report publishing completed");
	}
	
	public static void PutInfo(String message) {
	test.log(Status.INFO, message);	
		}
	
	public static void PutPass(String message) throws IOException {
		
		String reportPath = FunctionLibrary.captureScreen();
		if (reportPath.equals(""))
		test.log(Status.PASS, message);
		else
		test.log(Status.PASS, message, MediaEntityBuilder.createScreenCaptureFromPath(reportPath).build());	
	}
	
	public static void PutFail(String message) throws IOException {
		FunctionLibrary.error_count++;
		String reportPath = FunctionLibrary.captureScreen();
		if (reportPath.equals(""))
		test.log(Status.FAIL, message);
		else
		test.log(Status.FAIL, message, MediaEntityBuilder.createScreenCaptureFromPath(reportPath).build());
	}
	public static void PutFailWithoutScreenShot(String message) throws IOException {
		FunctionLibrary.error_count++;
		test.log(Status.FAIL, message);		
	}
		
}
