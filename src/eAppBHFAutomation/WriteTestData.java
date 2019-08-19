package eAppBHFAutomation;
import bhfUtility.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WriteTestData {
	//Method to set the data into the test case
	static String testDataSheetName;
	public static void setTestData(int rowid, int coloumnid, String DataValue) throws Exception 
	{
		try{
			//Reading Properties file
			Properties p = new Properties();
			p=ReadConfigFile.getObjectRepository();

			String TESTCASE_FILENAME = p.getProperty("TestData_Filename");
			FileInputStream excelFile = new FileInputStream(new File(TESTCASE_FILENAME));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet(testDataSheetName);
			excelFile.close();
			Row currentRow;
			Cell currentCell;
			currentRow = TestCase_Sheet.getRow(rowid);
			
			if (currentRow.getCell(coloumnid) != null){
				currentCell = currentRow.getCell(coloumnid);
			}
			else {
				currentCell = currentRow.createCell(coloumnid);
			}
			//Setting the cell type to Text
			DataFormat fmt = workbook.createDataFormat();
		    CellStyle textStyle = workbook.createCellStyle();
		    textStyle.setDataFormat(fmt.getFormat("@"));
		    currentCell.setCellStyle(textStyle);
		    
			//currentCell.setCellType(Cell.CELL_TYPE_STRING);
			currentCell.setCellValue(DataValue);
			FileOutputStream outputStream = new FileOutputStream(new File(TESTCASE_FILENAME));
			workbook.write(outputStream);
			workbook.close();
			outputStream.close();
		}
		catch (Exception e){
			Log.error("Error in setTestData in WriteTestData class");
			Log.error(e);
			throw new Exception(e);
		}
	}
}
