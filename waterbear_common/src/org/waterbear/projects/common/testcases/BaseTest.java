package org.waterbear.projects.common.testcases;

import java.util.HashMap;

import net.sf.sahi.client.Browser;

import org.apache.log4j.Logger;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.msg.Bundles;
import org.waterbear.core.msg.Bundles.Bundle;
import org.waterbear.projects.common.TestDataManager;

public abstract class BaseTest {
	protected Logger log = Logger.getLogger(BaseTest.class);

	private boolean isXStreamEnabled = Boolean
			.parseBoolean(org.waterbear.projects.common.ProjectConfiguration
					.getInstance().getProperty("xstream.enabled"));

	protected Browser getBrowser() {
		return WaterBearConfiguration.instance.getBrowser();
	}

	protected String getString(Bundle bundle, String key) {
		return Bundles.getString(bundle, key);
	}

	/**
	 * 
	 * @param persistenceRequired
	 *            Indicates if the test data should be generated to a XML file
	 *            and load test data from the XML file later. If False, it means
	 *            the test data is system independent so it is not needed to
	 *            persist it a XML file.
	 */
	protected HashMap loadTestData(boolean persistenceRequired) {
		if (!isXStreamEnabled) {
			return prepareTestData();
		} else {
			if (persistenceRequired) {
				final String fileName = getClass().getName();
				HashMap dataMap = TestDataManager.getInstance()
						.loadTestDataForTestClass(fileName);
				if (dataMap == null) {
					TestDataManager.getInstance().persistTestData(fileName,
							prepareTestData());
					dataMap = TestDataManager.getInstance()
							.loadTestDataForTestClass(fileName);
				}
				return dataMap;
			} else {
				return prepareTestData();
			}
		}
	}

	protected abstract HashMap prepareTestData();

}
