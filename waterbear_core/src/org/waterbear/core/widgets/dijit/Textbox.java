package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;

/**
 * <img src="textbox.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "setValue")
public class Textbox extends DojoWidget {

	public Textbox(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void verify(Object... initStateValues) {
		assertWidgetData(initStateValues[0], getValue());
	}

	@Override
	public String getValue() {
		return getInputBox().getValue();
	}

	public String getPlaceHolderValue() {
		return getBrowser().span("dijitPlaceHolder dijitInputField").in(es)
				.getText();
	}

	public void assertPlaceHolderValue(String expected) {
		assertEquals(expected, getPlaceHolderValue());
	}

	@Override
	public void setValue(Object value) {
		log("_setValue", es.toString(), value.toString());
		getInputBox().focus();
		assertTrue(es + " is supposed to be enabled but not", isEnabled());
		assertFalse(es + " is not supposed to be readOnly but readOnly now",
				isReadonly());
		getInputBox().setValue(value.toString());
	}

	protected ElementStub getInputBox() {
		return getBrowser().textbox(getDojoWidgetId()).in(es);
	}

	public void pressEnter() {
		getInputBox().keyUp(KEYCODE_ENTER, KEYCODE_ENTER);
	}

	public void assertHasError() {
		assertEquals("There should be errors.", "true", getInputBox()
				.getAttribute("aria-invalid"));
	}

	public void assertNoError() {
		assertEquals("There should not be errors.", "false", getInputBox()
				.getAttribute("aria-invalid"));
	}

	public void assertAttribute(String expected, String attrName) {
		assertEquals(expected, getInputBox().getAttribute(attrName));
	}
}
