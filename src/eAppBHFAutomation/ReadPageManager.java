package eAppBHFAutomation;

import bhfUtility.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadPageManager {
	public static HashMap<String, String> getLocators(String fileName, String sheetname, String rowName) throws IOException 
	{
	HashMap<String, String> locators = new LinkedHashMap<String, String>();
	String Field_Type = "";	
	String Identifier = "";
	String Locator = "";
	String Client_Side_Message_Identifier = "";
	String Client_Side_Message_Locator = "";
	locators.put("Field_Type", Field_Type);
	locators.put("Identifier", Identifier);
	locators.put("Locator", Locator);
	locators.put("Client_Side_Message_Identifier", Client_Side_Message_Identifier);
	locators.put("Client_Side_Message_Locator", Client_Side_Message_Locator);
	try {
	
	FileInputStream excelFile = new FileInputStream(new File(fileName));
	XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
	excelFile.close();
	XSSFSheet TestCase_Sheet = workbook.getSheet(sheetname);
	if (TestCase_Sheet==null){
		Log.info("Invalid Screen Name Provided. The sheet with name '" + sheetname + "' doesn't exist");
		workbook.close();
		return locators;
	}
	int rowCount = 0;
	Row currentRow;
	Cell currentCell;
	Iterator<Row> iterator = TestCase_Sheet.iterator();
	while (iterator.hasNext()) 
	{
		currentRow = TestCase_Sheet.getRow(rowCount);
		currentCell = currentRow.getCell(0);
		if (currentCell.getStringCellValue().equalsIgnoreCase(rowName))
		{
			//System.out.println(currentCell.getStringCellValue());
			Field_Type = currentRow.getCell(1).getStringCellValue();
			Identifier = currentRow.getCell(2).getStringCellValue();
			Locator = currentRow.getCell(3).getStringCellValue();
			
			try{
				if (currentRow.getCell(4)!=null) {
			Client_Side_Message_Identifier = currentRow.getCell(4).getStringCellValue();}}
			catch (Exception e){
				Log.error("Error in getting Client_Side_Message_Identifier in getLocators in ReadPageManager Class");
				Log.error(e.toString());
			}
			
			try{
				if (currentRow.getCell(5)!=null) {
			Client_Side_Message_Locator = currentRow.getCell(5).getStringCellValue();}}
			catch (Exception e){
				Log.error("Error in getting Client_Side_Message_Locator in getLocators in ReadPageManager Class");
				Log.error(e.toString());
			}
			locators.put("Field_Type", Field_Type);
			locators.put("Identifier", Identifier);
			locators.put("Locator", Locator);
			locators.put("Client_Side_Message_Identifier", Client_Side_Message_Identifier);
			locators.put("Client_Side_Message_Locator", Client_Side_Message_Locator);
			workbook.close();
			Log.info("Locator for " + rowName + " in page " + sheetname);
			Log.info(locators);
			return locators;
		}
		rowCount++;
		iterator.next();
	}
	workbook.close();
	return locators;
	}
catch (Exception e){
	Log.error("Error in ReadPageManager Class");
	e.printStackTrace();
	Log.error(e.toString());
	return null;	
}
}
}