package org.waterbear.projects.common.widgets.html;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.utils.asserter.TestStrategy;
import org.waterbear.core.utils.asserter.WBAsserter;
import org.waterbear.core.widgets.WebElement;

import net.sf.sahi.client.ElementStub;

public abstract class BasePropsTable extends HtmlTable {

	public BasePropsTable(ElementStub es) {
		super(es);
		// TODO Auto-generated constructor stub
	}

	public BasePropsTable(WebElement we) {
		super(we);
		// TODO Auto-generated constructor stub
	}

	public void assertProperyValueNotContains(String label,
			String... expectedTexts) {
		String propValue = readProperyValue(label);
		for (String expectedText : expectedTexts) {
			assertFalse("The property [" + label + "] value [" + propValue
					+ "] contains [" + expectedText + "]",
					propValue.indexOf(expectedText) != -1);
		}
	}

	public void assertProperyValueContains(String label,
			String... expectedTexts) {
		String propValue = readProperyValue(label);
		for (String expectedText : expectedTexts) {
			assertTrue("The property [" + label + "] value [" + propValue
					+ "] does not contain [" + expectedText + "]",
					propValue.indexOf(expectedText) != -1);
		}

	}

	public void assertProperyValue(String... pairs) {
		int argLen = pairs.length;
		if (argLen % 2 != 0) {
			throw new AutomationException(
					"The number of the paramters should be a even number. The format is: <label1>,<expectedValue1>,<label2>,<expectedValue2>,...");
		}

		for (int i = 0; i < argLen; i = i + 2) {
			String label = pairs[i];
			String expected = pairs[i + 1];
			assertEquals(expected, readProperyValue(label));
		}
	}

	public void assertProperyValueIngoreCase(String label, String expected) {
		assertEquals(expected.toLowerCase(), readProperyValue(label)
				.toLowerCase());
	}

	public void assertProperyValueStarts(String label, String expected) {
		assertTrue(readProperyValue(label).startsWith(expected));
	}

	public void assertPropertyValue(TestStrategy<String> ts, String label,
			String... args) {
		final String propValue = readProperyValue(label);
		WBAsserter.assertText(ts, propValue, args);
	}

	abstract String readProperyValue(String label);
}
