package utils;

import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.testng.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.framework.LogListener;
import com.yzt.testcase.IPSTest;

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

	private static Logger logger = Logger.getLogger(DataProviders.class);

//	private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator
//			+ "test" + File.separator + "resource" + File.separator + "testdata" + File.separator;	
	
	private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator
			+ "test" + File.separator + "resource" + File.separator + "remote.data" + File.separator;	
	
	public DataProviders(){}
	
	public DataProviders(String classname, String methodname) {
		testdataInit();
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
		testdataInit();
		int dotNum = classname.indexOf(".");

		if (dotNum > 0) {
			classname = classname.substring(classname.lastIndexOf(".") + 1, classname.length());
		}

		String filename = FILE_PATH + classname + ".xlsx";
		File file = new File(filename);
		logger.info("======Excel file path : " + filename + "=======");
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
	
	private void testdataInit() {
//		String localPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
//				+ File.separator + "resource" + File.separator + "remote.data";
//		File localPathFileConfig = new File(localPath + File.separator + ".git");
//		String localPath = "C:\\workspace\\1111";
//		String localPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
//				+ File.separator + "resource" + File.separator + "testdata";
		String remotePath = "http://git.1ziton.com/vivi.zhang/remote.data.git";
		String username = "zhangwei@1zitong";
		String password = "1qaz2wsx";
		String branch = "master";
		File localPathFile = new File(FILE_PATH);
		try {
			if (!localPathFile.exists()) {
				gitClone(remotePath, branch, FILE_PATH, username, password);
			} else {
				gitPull(remotePath, branch, FILE_PATH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gitClone(String remotePath, String branch, String localPath, String username, String password) {
		File localPathFile = new File(localPath);
		UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(
				username, password);
		try {
			Git.cloneRepository().setURI(remotePath).setBranch(branch).setDirectory(localPathFile)
					.setCredentialsProvider(usernamePasswordCredentialsProvider).call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gitPull(String remotePath, String branch, String localPath) {
		try {
			File localPathFile = new File(localPath);
			Git git = new Git(new FileRepository(localPath + ".git"));
			git.pull().setRemoteBranchName(branch).call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
