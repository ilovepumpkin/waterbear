package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;

/**
 * <img src="textarea.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "setValue")
public class Textarea extends Textbox {

	public Textarea(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Object value) {
		log("_setValue", es.toString(), value.toString());
		es.focus();
		assertTrue(es + " is supposed to be enabled but not", isEnabled());
		assertFalse(es + " is not supposed to be readOnly but readOnly now",
				isReadonly());
		getBrowser().textarea(0).in(es).setValue(value.toString());
	}

	public String getValue() {
		// return es.getValue();
		return getBrowser().textarea(0).in(es).getValue();
	}

	public void assertTextExists(String... expectedTexts) {
		String value = getValue();
		for (int i = 0; i < expectedTexts.length; ++i) {
			String expectedText = expectedTexts[i];
			assertTrue("The textarea value [" + value
					+ "] does not contain the text [" + expectedText + "].",
					value.contains(expectedText));
		}
	}
}
