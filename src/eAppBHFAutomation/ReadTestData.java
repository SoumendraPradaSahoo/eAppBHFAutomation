package eAppBHFAutomation;

import bhfUtility.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadTestData {

	//Method to Get the Total No of Cases to Execute
	public static int getTestCases(String fileName) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestCase");
		Iterator<Row> iterator = TestCase_Sheet.iterator();
		int rowCount = 0;
		while (iterator.hasNext()) 
		{
			rowCount++;
			iterator.next();
		}
		workbook.close();
		excelFile.close();
		Log.info("Total no of case: " + (rowCount-1));
		return rowCount-1;
	}

	//Method to Get if Particular test case needs to be executed
	public static HashMap<String, String> getExecutable(String fileName, int row_num)
	{
		FileInputStream excelFile;
		try {
			excelFile = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet("TestCase");
			HashMap<String, String> testcaseExecutable = new LinkedHashMap<String, String>();
			workbook.close();
			excelFile.close();
			Row currentRow = TestCase_Sheet.getRow(row_num);
			Cell testCaseNo = currentRow.getCell(1);
			Cell currentCell = currentRow.getCell(2);
			if (currentCell.getStringCellValue().equalsIgnoreCase("Yes")) 
			{

				testcaseExecutable.put("testCase", testCaseNo.getStringCellValue());
				testcaseExecutable.put("executable", currentCell.getStringCellValue());


				Log.info(testcaseExecutable);
				return testcaseExecutable;
			}
			else
			{

				testcaseExecutable.put("testCase", testCaseNo.getStringCellValue());
				testcaseExecutable.put("executable", "No");


				Log.info(testcaseExecutable);
				return testcaseExecutable;

			}
		} catch (Exception e) {
			Log.error("Error in getExecutable");
			Log.error(e);
			return null;
		}
	}

	//Method to get the Coloumn no of the test case to be executed
	public static int getTestCaseColumnNo(String fileName, String testcaseno) throws IOException 
	{
		try{
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet("TestData");
			workbook.close();
			excelFile.close();
			int columnNo=0;
			Cell currentCell;
			Iterator<Cell> cellIter = TestCase_Sheet.getRow(1).cellIterator();
			while (cellIter.hasNext()) 
			{
				currentCell = (Cell) cellIter.next();
				if (currentCell.getStringCellValue().equalsIgnoreCase(testcaseno))
				{
					return columnNo;
				}
				columnNo++;
			}
			return 0;
		}
		catch(Exception e)
		{
			Log.error("Error in getTestCaseColumnNo");
			Log.error(e);
			return 0;
		}
	}

	//Method to get the total no of rows in the test data sheet
	public static int getTotalNoOfRows(String fileName) throws IOException 
	{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestData");
		workbook.close();
		excelFile.close();
		Iterator<Row> iterator = TestCase_Sheet.iterator();
		int rowCount = 0;
		while (iterator.hasNext()) 
		{
			rowCount++;
			iterator.next();
		}
		return rowCount-1;
	}

	//Method to get the test data of the test case to be executed
	public static HashMap<String, String> getTestCaseData(String fileName, int testcaseno, int RowNo) throws IOException 
	{
		try{

			HashMap<String, String> testData = new LinkedHashMap<String, String>();
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet("TestData");
			workbook.close();
			excelFile.close();
			Row currentRow = TestCase_Sheet.getRow(RowNo);

			testData.put("ScreenName", currentRow.getCell(0).getStringCellValue());
			if (currentRow.getCell(1) != null){	
				testData.put("FieldName", currentRow.getCell(1).getStringCellValue());
			}else
				testData.put("FieldName","");
			testData.put("Step", currentRow.getCell(2).getStringCellValue());
			testData.put("Data", "");
			try{
				if (currentRow.getCell(testcaseno) != null){
					switch (currentRow.getCell(testcaseno).getCellTypeEnum()) {
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(currentRow.getCell(testcaseno))) {
							DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
							Date date = currentRow.getCell(testcaseno).getDateCellValue();
							testData.put("Data",dateFormat.format(date));
						} else {
							Double data = currentRow.getCell(testcaseno).getNumericCellValue();
							if ((data - data.intValue() == 0.00) || (data - data.intValue() == 0.0)) {
							testData.put("Data", String.valueOf(data.intValue()));
							} else {
								testData.put("Data", String.valueOf(data));}
						}
						break;
					case STRING:
						testData.put("Data",currentRow.getCell(testcaseno).getStringCellValue());
						break;
					case BOOLEAN :
						testData.put("Data", String.valueOf(currentRow.getCell(testcaseno).getBooleanCellValue()));
						break;
					case FORMULA :
						FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
						CellValue cellValue = evaluator.evaluate(currentRow.getCell(testcaseno)); 
						//System.out.println(cellValue.getStringValue());
						testData.put("Data", getStringValueFromExcel(cellValue));
						break;
					default:
						//System.out.println(currentRow.getCell(testcaseno).getCellTypeEnum());
						break;

					}
				}}
			catch (Exception e) {
				Log.error("Error in getting test data. DataType other than Numeric, String, Boolean, Formula");
				Log.error(e);
			}
			Log.info("Test Case Data");
			Log.info(testData);
			return testData;
		}
		catch (Exception e){
			Log.error("Error in getTestCaseData");
			Log.error(e);
			return null;
		}
	}

	public static String getStringValueFromExcel(CellValue cellValue) {
		String tempString="";
		switch (cellValue.getCellTypeEnum()) {
		case NUMERIC:
			//tempString= String.valueOf(cellValue.getNumberValue());
			Double data = cellValue.getNumberValue();
			if ((data - data.intValue() == 0.00) || (data - data.intValue() == 0.0)) {
				tempString =  String.valueOf(data.intValue());
			} else {
				tempString =  String.valueOf(data);
				}
			break;
		case STRING:
			tempString= cellValue.getStringValue();
			break;
		case BOOLEAN :
			tempString= String.valueOf(cellValue.getBooleanValue());
			break;
		default:
			break;
		}
		return tempString;		
	}
}
