package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;
import org.waterbear.core.exception.AutomationException;

/**
 * <img src="combobox.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "inputText")
public class ComboBox extends DojoWidget {

	public ComboBox(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String getSelected() {
		return getBrowser().textbox("dijitReset dijitInputInner").in(es)
				.getValue();
	}

	/**
	 * 
	 * @param index
	 *            Starting from zero.
	 */
	public void selectByIndex(int index) {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());
		getArrowButton().click();

		List<ElementStub> options = getBrowser().listItem(
				"/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		if (index > options.size() - 1) {
			throw new AutomationException("Index [" + index
					+ "] is out of range. The list size is " + options.size());
		} else {
			options.get(index).click();
		}
	}

	public List<String> getOptions() {
		getArrowButton().click();
		ArrayList<String> optionList = new ArrayList<String>();
		List<ElementStub> options = getBrowser().listItem(
				"/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		for (Iterator<ElementStub> it = options.iterator(); it.hasNext();) {
			ElementStub option = (ElementStub) it.next();
			optionList.add(option.getText());
		}
		return optionList;
	}

	public void selectByText(String text) {
		assertTrue(es + " is supposed to be enabled but now disabled",
				isEnabled());

		getArrowButton().click();
		
		/**
		 * sometime click the arrowbutton,can't immediately get the all selectList;
		 */
		 try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// List<ElementStub> options = getBrowser().listItem(
		// "/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		// The options are used to be LI but it is changed to DIV now -
		// 05/23/2014
		List<ElementStub> options = getBrowser().div(
				"/" + getDojoWidgetId() + "_popup\\d+/").collectSimilar();
		for (Iterator<ElementStub> it = options.iterator(); it.hasNext();) {
			ElementStub option = (ElementStub) it.next();
			if (text.equals(option.getText())) {
				option.click();
				return;
			}
		}
		assertEquals(text, getSelected());
	}

	protected ElementStub getArrowButton() {
		return getBrowser().textbox(
				"dijitReset dijitInputField dijitArrowButtonInner").in(es);
	}

	public void inputText(String text) {
		getBrowser().textbox("dijitReset dijitInputInner").in(es)
				.setValue(text);
		removeFocus();
	}

	@Override
	public void setValue(Object text) {
		inputText(text.toString());
	}

	@Override
	public void verify(Object... stateValues) {
		assertWidgetData(stateValues[0], getSelected());
	}

}
