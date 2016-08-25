package org.waterbear.projects.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.projects.common.tools.ctd.CTDDataManager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TestDataManager {
	private static TestDataManager instance;
	private HashMap testClassMap;
	private XStream xstream;
	private String testDataDir;

	protected static final Logger log = Logger.getLogger(TestDataManager.class);

	private TestDataManager() {
		testClassMap = new HashMap();
		xstream = new XStream(new DomDriver());
		initTestDataDir();

	}

	public static TestDataManager getInstance() {
		if (instance == null) {
			instance = new TestDataManager();
		}
		return instance;
	}

	public HashMap loadTestDataForTestClass(String fileName) {
		fileName = fileName + ".xml";
		FileInputStream fis = null;
		try {
			File file = new File(testDataDir, fileName);
			if (!file.exists()) {
				return null;
			}
			fis = new FileInputStream(file);
			HashMap dataMap = (HashMap) xstream.fromXML(fis);
			log.info(fileName + " is loaded successfully.");
			return dataMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new AutomationException(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new AutomationException(e);
			}
		}

	}

	public void persistTestData(String fileName, HashMap dataMap) {
		fileName = fileName + ".xml";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(testDataDir, fileName));
			xstream.toXML(dataMap, fos);
			log.info(fileName + " is persisted successfully.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new AutomationException(e);
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new AutomationException(e);
			}
		}

	}

	private void initTestDataDir() {
		String userDir = System.getProperty("user.dir");
		String defaultTestDataDir = new File(userDir, "testdata")
				.getAbsolutePath();
		testDataDir = defaultTestDataDir;

		String userSpecifiedDir = ProjectConfiguration.getInstance()
				.getProperty("testdata.dir");
		if (userSpecifiedDir != null && userSpecifiedDir.trim().length() > 0) {
			if (userSpecifiedDir.startsWith("/")
					|| userSpecifiedDir.contains(":")) {
				// this is a absolute path
				testDataDir = userSpecifiedDir;
			} else {
				// this is a relative path
				testDataDir = new File(userDir, userSpecifiedDir)
						.getAbsolutePath();
			}
		}

		File f = new File(testDataDir);
		if (!f.exists()) {
			boolean successFlag = f.mkdirs();
			if (!successFlag) {
				throw new AutomationException(
						"Failed to create the test data directory ["
								+ testDataDir + "]");
			}
		}

		log.info("The test data directory is " + testDataDir);
	}

	private File getUploadDataDir() {
		return new File(testDataDir, "upload");
	}

	public File getUploadFile(String filePath) {
		File file = new File(getUploadDataDir(), filePath);
		return file;
	}

	public String getUploadFilePath(String filePath) {
		File file = getUploadFile(filePath);
		return file.getAbsolutePath().replace('\\', '/');
	}

	public String getFilepath(String filePath) {
		File file = new File(testDataDir, filePath);
		return file.getAbsolutePath().replace('\\', '/');
	}

	public Object[][] loadCTDData(Class beanClazz, String modelPath,
			String csvPath) {

		String modelFilePath = getFilepath(modelPath);
		String csvFilePath = getFilepath(csvPath);

		return new CTDDataManager(modelFilePath).loadData(beanClazz,
				csvFilePath);
	}
}
