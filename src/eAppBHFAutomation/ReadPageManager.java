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
						Log.error(e);
					}

					try{
						if (currentRow.getCell(5)!=null) {
							Client_Side_Message_Locator = currentRow.getCell(5).getStringCellValue();}}
					catch (Exception e){
						Log.error("Error in getting Client_Side_Message_Locator in getLocators in ReadPageManager Class");
						Log.error(e);
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
			Log.error(e);
			return null;	
		}
	}

	public static HashMap<String, LinkedHashMap<String, String>> getAllLocators(String fileName) throws IOException 
	{
		String sheetname;
		HashMap<String, LinkedHashMap<String, String>> allLocators = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		excelFile.close();
		
		try{
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			XSSFSheet TestCase_Sheet = workbook.getSheetAt(i);
			sheetname = TestCase_Sheet.getSheetName();
			Row currentRow;
			Iterator<Row> iterator = TestCase_Sheet.iterator();
			while (iterator.hasNext()) 
			{
				LinkedHashMap<String, String> locators = new LinkedHashMap<String, String>();
				String Filed_Name = "";
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
				currentRow = iterator.next();
				
				try{
					if (currentRow.getCell(0)!=null) {
						Filed_Name = currentRow.getCell(0).getStringCellValue();}}
				catch (Exception e){
					Log.error("Error in getting Field Name in getLocators in ReadPageManager Class for Screen: " + sheetname);
					Log.error(e);
				}
				
				try{
					if (currentRow.getCell(1)!=null) {
						Field_Type = currentRow.getCell(1).getStringCellValue();}}
				catch (Exception e){
					Log.error("Error in getting Field Type in getLocators in ReadPageManager Class for Screen: " + sheetname);
					Log.error(e);
				}
				
				try{
					if (currentRow.getCell(2)!=null) {
						Identifier = currentRow.getCell(2).getStringCellValue();}}
				catch (Exception e){
					Log.error("Error in getting Filed Idetifier in getLocators in ReadPageManager Class for Screen: " + sheetname);
					Log.error(e);
				}
				
				try{
					if (currentRow.getCell(3)!=null) {
						Locator = currentRow.getCell(3).getStringCellValue();}}
				catch (Exception e){
					Log.error("Error in getting Filed Locator in getLocators in ReadPageManager Class for Screen: " + sheetname);
					Log.error(e);
				}
				
					try{
						if (currentRow.getCell(4)!=null) {
							Client_Side_Message_Identifier = currentRow.getCell(4).getStringCellValue();}}
					catch (Exception e){
						Log.error("Error in getting Client_Side_Message_Identifier in getLocators in ReadPageManager Class");
						Log.error(e);
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
					Log.info("Locator for " + Filed_Name + " in page " + sheetname + ": " + sheetname + "." + Filed_Name);
					allLocators.put(sheetname + "." + Filed_Name, locators);
					Log.info(locators);
				}
			}	
			workbook.close();
			/*System.out.println("-------------Getting sample locator --------------");
			System.out.println(allLocators.get("Annuitant.Suffix"));
			for (Map.Entry<String, LinkedHashMap<String, String>> entry:allLocators.entrySet()) {
				System.out.println(entry.getKey() );
				System.out.println(entry.getValue() );
			}*/
			
			return allLocators;
		}
		
		catch (Exception e){
			Log.error("Error in ReadPageManager Class");
			Log.error(e);
			workbook.close();
			return null;	
		}
	}
}