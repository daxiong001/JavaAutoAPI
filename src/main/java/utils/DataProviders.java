package utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import exception.customsException;

/**
 * Excel放在testdata目录下
 * </p>
 * Excel命名方式：测试类名.xlsx
 * </p>
 * Excel的sheet命名方式：测试方法名
 * </p>
 * Excel第一行为Map键值
 * 
 * @ClassName: DataProvider
 * @Description: TODO(读取Excel数据)
 */
public class DataProviders implements Iterator<Object> {
	private Workbook book = null;
	private Sheet sheet = null;
	private int rowNum = 0;
	private int currentRowNo = 0;
	private int columnNum = 0;
	private List<String> columnName = Lists.newArrayList();

	private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "test" + File.separator + "resource" + File.separator + "testdata" + File.separator;

	public DataProviders(String classname, String methodname) {
		int dotNum = classname.indexOf(".");

		if (dotNum > 0) {
			classname = classname.substring(classname.lastIndexOf(".") + 1, classname.length());
		}

		String filename = FILE_PATH + classname + ".xlsx";
		File file = new File(filename);

		if (file.exists() && file.isFile()) {
			try {
				book = new XSSFWorkbook(file);
				sheet = book.getSheet(methodname);
				rowNum = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
				Row row = sheet.getRow(0);
				columnNum = row.getLastCellNum() - row.getFirstCellNum();
				for (Cell cell : row) {
					columnName.add(cell.toString());
				}
				currentRowNo++;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("unable to read Excel data");
			}
		} else {
			throw new customsException(classname + ".xlsx 文件不存在，请确认文件已存在！");
		}
	}
	
	public DataProviders(String classname) {
		int dotNum = classname.indexOf(".");

		if (dotNum > 0) {
			classname = classname.substring(classname.lastIndexOf(".") + 1, classname.length());
		}

		String filename = FILE_PATH + classname + ".xlsx";
		File file = new File(filename);

		if (file.exists() && file.isFile()) {
			try {
				book = new XSSFWorkbook(file);
				sheet = book.getSheet(classname);
				rowNum = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
				Row row = sheet.getRow(0);
				columnNum = row.getLastCellNum() - row.getFirstCellNum();
				for (Cell cell : row) {
					columnName.add(cell.toString());
				}
				currentRowNo++;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("unable to read Excel data");
			}
		} else {
			throw new customsException(classname + ".xlsx 文件不存在，请确认文件已存在！");
		}
	}

	public boolean hasNext() {
		if (this.rowNum == 0 || this.currentRowNo >= this.rowNum) {
			try {
				book.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		} else {
			if (sheet.getRow(this.currentRowNo).getCell(0) == null) {
				return false;
			}
			return true;
		}
	}

	public Map<String, String> next() {
		Map<String, String> data = Maps.newHashMap();
		Row row = sheet.getRow(this.currentRowNo);
		for (int i = 0; i < columnNum; i++) {
			data.put(this.columnName.get(i), row.getCell(i) == null ? "" : row.getCell(i).toString());
		}
		this.currentRowNo++;
		return data;
	}

}
