package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;
import org.waterbear.core.exception.AutomationException;

/**
 * <img src="select.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "select")
public class Select extends DojoWidget {

	public Select(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String getSelected() {
		String selectedText = "";
		ElementStub es = getBrowser().span("/.*dijitSelectLabel.*/").in(
				getElementStub());
		if (es.exists()) {
			selectedText = es.getText();
		}

		log.info("The selected text is:" + selectedText);
		return selectedText;
	}

	public String getText() {
		return getSelected();
	}

	protected ElementStub getSelectMenu() {
		return getBrowser().div(getDojoWidgetId() + "_dropdown");
	}

	public void select(String text) {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());
		if (text.equals(getSelected())) {
			log.info("The option [" + text + "] has already been selected.");
			return;
		}

		getArrowButton().click();
		ElementStub es = getBrowser().cell(text).in(
				getBrowser().table(0).in(getSelectMenu()));
		if (es != null && es.exists()) {
			es.click();
		} else {
			throw new AutomationException("Cannot find the option [" + text
					+ "] in " + es);
		}
	}

	public void selectByIndex(int index) {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());
		getArrowButton().click();

		List<ElementStub> esMenus = getBrowser().row("/dijit_MenuItem_\\d+/")
				.in(getSelectMenu()).collectSimilar();
		esMenus.get(index).click();
	}

	public List<String> getAllMenus() {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());
		getArrowButton().click();

		List<String> allMenus = new ArrayList<String>();
		List<ElementStub> esMenus = getBrowser()
				.cell("dijitReset dijitMenuItemLabel").in(getSelectMenu())
				.collectSimilar();
		for (ElementStub es : esMenus) {
			allMenus.add(es.getText());
		}
		return allMenus;
	}

	public int size() {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());
		getArrowButton().click();

		return getBrowser().cell("dijitReset dijitMenuItemLabel")
				.in(getSelectMenu()).countSimilar();
	}

	public void assertSize_GT(int expected) {
		int size = size();
		assertTrue("The size [" + size
				+ "] is not greater than the expected number [" + expected
				+ "]", size() > expected);
	}

	protected ElementStub getArrowButton() {
		return getBrowser().textbox("/.*dijitArrowButtonInner/").in(es);
	}

	public void assertSelected(String expected) {
		assertEquals(expected, getSelected());
	}

	@Override
	public void verify(Object... stateValues) {
		String expected = String.valueOf(stateValues[0]);
		String actual = getSelected();
		assertWidgetData(expected, actual);
	}

}
