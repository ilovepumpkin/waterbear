package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;
import org.waterbear.core.widgets.WebElement;

/**
 * <img src="checkbox.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "easyCheck")
public class Checkbox extends DojoWidget {

	public Checkbox(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	protected ElementStub getInternalCheckbox() {
		return getBrowser().checkbox(0).in(es);
	}

	public void easyCheck(boolean flag) {
		if (flag) {
			if (!checked()) {
				check();
			}
		} else {
			if (checked()) {
				uncheck();
			}
		}
	}

	public void check() {
		log("_check");
		getInternalCheckbox().check();
	}

	public boolean checked() {
		log("_checked");
		return getInternalCheckbox().checked();
	}

	public void uncheck() {
		log("_uncheck");
		getInternalCheckbox().uncheck();
	}

	public boolean isEnabled() {
		log("isEnabled", es.toString());
		String widgetClasses = new WebElement(getInternalCheckbox()
				.parentNode()).getWidgetClasses();
		return widgetClasses.contains("dijitDisabled") ? false : true;
	}

	public void assertChecked(boolean isChecked) {
		assertEquals(isChecked, checked());
	}

	@Override
	public void verify(Object... stateValues) {
		boolean expected = Boolean.valueOf(String.valueOf(stateValues[0]));
		boolean actual = checked();
		assertWidgetData(expected, actual);
	}
}
