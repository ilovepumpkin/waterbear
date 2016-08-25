package org.waterbear.projects.common.utils.cli;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.FieldNotFoundException;
import org.waterbear.core.utils.WBStringUtils;

/**
 * 
 * If a key appears several times in a sub table, the first one is still shown
 * as is. From the second, the key is added an index. So all keys are shown like
 * "key","key_0","key_1",etc.
 * 
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class GenericCliVerticalTable implements CliVerticalTable {

	private List<HashMap<String, String>> subTabs = new ArrayList<HashMap<String, String>>();

	public GenericCliVerticalTable(String cliOutput, String delim) {
		if (cliOutput == null || cliOutput.trim().equals("")) {
			throw new AutomationException("The data string passed in is blank.");
		}
		String[] temp = cliOutput.trim().split("\n");

		HashMap<String, String> subDataMap = new HashMap<String, String>();

		HashMap<String, Integer> dupKeyMap = new HashMap<String, Integer>();

		for (int i = 0; i < temp.length; i++) {
			String lineStr = temp[i].trim();
			String line[] = lineStr.split(delim);

			if (lineStr.isEmpty()) {
				subTabs.add(new HashMap<String, String>(subDataMap));
				subDataMap.clear();
				dupKeyMap.clear();
			} else {
				String key = line[0];
				String value = line.length == 2 ? line[1] : "";

				if (subDataMap.containsKey(key)) {
					if (!dupKeyMap.containsKey(key)) {
						dupKeyMap.put(key, 0);
					}
					int index = dupKeyMap.get(key);
					dupKeyMap.put(key, index + 1);
					key = key + "_" + index;
				}
				subDataMap.put(key, value);

				if (i == temp.length - 1) {
					subTabs.add(new HashMap<String, String>(subDataMap));
				}
			}
		}
	}

	private GenericCliVerticalTable(HashMap<String, String> map) {
		subTabs.add(map);
	}

	/* (non-Javadoc)
	 * @see org.waterbear.projects.svc.utils.cli.ICliVerticalTable#subTab(int)
	 */
	@Override
	public CliVerticalTable subTab(int index) {
		return new GenericCliVerticalTable(subTabs.get(index));
	}

	/* (non-Javadoc)
	 * @see org.waterbear.projects.svc.utils.cli.ICliVerticalTable#property(java.lang.String)
	 */
	@Override
	public String property(String propertyName) {
		HashMap<String, String> map0 = subTabs.get(0);
		if (!map0.containsKey(propertyName)) {
			throw new FieldNotFoundException("The property [" + propertyName
					+ "] was not found.");
		}
		return map0.get(propertyName);
	}

	/* (non-Javadoc)
	 * @see org.waterbear.projects.svc.utils.cli.ICliVerticalTable#assertPropertyValue(java.lang.String, java.lang.String)
	 */
	@Override
	public void assertPropertyValue(String propertyName, String expected) {
		assertEquals(property(propertyName), expected);
	}

	/* (non-Javadoc)
	 * @see org.waterbear.projects.svc.utils.cli.ICliVerticalTable#properties(java.lang.String[])
	 */
	@Override
	public String[] properties(String[] propertyNames) {
		HashMap<String, String> map0 = subTabs.get(0);
		String rowData[] = new String[propertyNames.length];
		for (int i = 0; i < propertyNames.length; i++) {
			if (!map0.containsKey(propertyNames[i])) {
				throw new AutomationException("The property ["
						+ propertyNames[i] + "] was not found.");
			}
			rowData[i] = map0.get(propertyNames[i]);
		}
		return rowData;
	}

}
