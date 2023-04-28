package utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadDataFromExcel {

	String filePath;
	FileInputStream fis = null;
	XSSFWorkbook wb;
	static XSSFSheet sheet;
	HashMap<String, Integer> colNums = null;
	int rowCount = 0;
	
	// This will get the data in the excel file based on the parameters
	public String getDataFromExcel(String excelFilePath, String sheetName, int rowNumber, String colName) {
		
		// ./Data/TestData.xlsx
		ReadDataFromExcel excel = new ReadDataFromExcel(excelFilePath);
		
		// Getting the sheet
		excel.setSheet(sheetName);

		// Getting the value based on the row and given column name
		String desiredValue = excel.getCellData(rowNumber, colName);
		excel.closeFile();
		return desiredValue;
	}
	
	public ReadDataFromExcel() {
		// Blank constructor
	}
	
	// This will find the excel file
	public ReadDataFromExcel(String filePath) {
		try {
			this.filePath = filePath;
			FileInputStream fis = new FileInputStream(new File(this.filePath));
			wb = new XSSFWorkbook(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// This will read the sheet on the file based on the provided sheet name
	public void setSheet(String sheetName) {
		sheet = wb.getSheet(sheetName);
		populateColumns();
		rowCount = sheet.getLastRowNum();
	}

	// This will count all the Rows that has a value and add 1 since row 0 is considered as headers for column
	public int getRowCount() {
		return rowCount + 1;
	}

	// Populate the Column Names with Column Numbers using HashMap
	public void populateColumns() {
		colNums = new HashMap<String, Integer>();

		int colIndex = 0;
		Row row = sheet.getRow(0);
		Iterator<Cell> cells = row.cellIterator();

		while (cells.hasNext()) {
			Cell cell = cells.next();
			String colName = cell.getStringCellValue();
			colNums.put(colName, colIndex);
			colIndex++;
		}
	}

	// This will check for the Column Number based on Column Name
	public int getColNumber(String colName) {
		return colNums.get(colName);
	}

	// This will read cell dynamically based on Column Names
	public String getCellData(int rowNum, String colName) {
		String cellValue = "";
		int colNum = getColNumber(colName);
		cellValue = getCellData(rowNum, colNum);
		return cellValue;
	}

	// This will read a specific cell on the selected sheet
	public String getCellData(int rowNum, int colNum) {
		String cellValue = "";
		try {
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(colNum);

			if (cell.getCellType() == CellType.STRING) {
				cellValue = cell.getStringCellValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellValue;
	}

	// This will close FileInputStream and Workbook to prevent memory leak
	public void closeFile() {
		try {
			if (fis != null) {
				fis.close();
				wb.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
