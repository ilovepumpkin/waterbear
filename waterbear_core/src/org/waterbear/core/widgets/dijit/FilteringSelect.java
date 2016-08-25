package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.utils.BrowserUtil;

/**
 * <img src="filteringselect.jpeg">
 * 
 * @author zhuzhenq@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "inputText")
public class FilteringSelect extends DojoWidget {
	public FilteringSelect(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void assertInOptions(String expected) {
		assertTrue(getAllMenus().contains(expected));
	}

	public void assertNotInOptions(String expected) {
		assertFalse(getAllMenus().contains(expected));
	}

	private ElementStub getComboBoxMenu() {
		return getBrowser().div(getDojoWidgetId() + "_popup");
	}

	protected ElementStub getInputBox() {
		return getBrowser().textbox("dijitReset dijitInputInner").in(es);
	}

	public String getSelected() {
		return getInputBox().getValue();
	}

	public void inputText(String text) {
		getInputBox().setValue(text);
		BrowserUtil.waitFor(1000);
		ElementStub m = getBrowser().div(text).in(getComboBoxMenu());
		m.click();
	}

	public void setValue(String value) {
		inputText(value);
	}


	public void selectByText(String timeText) {
		getArrowButton().click();
		getBrowser().div(timeText).in(getComboBoxMenu()).hover();
		getArrowButton().click();
		getBrowser().div(timeText).in(getComboBoxMenu()).click();
	}

	/**
	 * 
	 * 
	 * @param text
	 * @param index
	 *            Starting from zero
	 */
	public void filterText(String text, int index) {
		getInputBox().setValue(text);
		List<ElementStub> options = getBrowser().div(
				"/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		if (index > options.size() - 1) {
			throw new AutomationException("Index [" + index
					+ "] is out of range. The list size is " + options.size());
		} else {
			options.get(index).click();
		}
	}

	public void selectByIndex(int index) {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());
		getArrowButton().click();

		List<ElementStub> options = getBrowser().div(
				"/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		if (index > options.size() - 1) {
			throw new AutomationException("Index [" + index
					+ "] is out of range. The list size is " + options.size());
		} else {
			options.get(index).hover();
			getArrowButton().click();
			options.get(index).click();
		}
	}

	public List<String> getAllMenus() {
		getArrowButton().click();
		BrowserUtil.waitFor(1000);

		List<String> allMenus = new ArrayList<String>();
		List<ElementStub> esMenus = getBrowser().div(
				"/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		for (ElementStub es : esMenus) {
			allMenus.add(es.getText());
		}
		return allMenus;
	}

	protected ElementStub getArrowButton() {
		return getBrowser().textbox("/.*dijitArrowButtonInner/").in(es);
	}

	@Override
	public void verify(Object... stateValues) {
		assertWidgetData(stateValues[0], getSelected());
	}

	public void assertSelected(String expected) {
		assertWidgetData(expected, getSelected());
	}

}