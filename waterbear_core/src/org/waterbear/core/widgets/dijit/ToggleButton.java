package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

/**
 * <img src="togglebutton.jpeg">
 * 
 * @author jtingsh@cn.ibm.com
 * 
 */

public class ToggleButton extends DojoWidget {

	public ToggleButton(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
	}

	private ElementStub getInternalCheckbox() {
		return getBrowser().span(
				"dijitReset dijitInline dijitIcon dijitCheckBoxIcon").in(es);
	}

	public void easyCheck(boolean flag) {
		if (flag) {
			check();
		} else {
			uncheck();
		}
	}

	public void check() {
		if (!isChecked()) {
			getInternalCheckbox().click();
		}
	}

	public void uncheck() {
		if (isChecked()) {
			getInternalCheckbox().click();
		}
	}

	public boolean isChecked() {
		log("isChecked", es.toString());
		String widgetClasses = getWidgetClasses();
		return widgetClasses.contains("dijitChecked") ? true : false;
	}

	@Override
	public void verify(Object... stateValues) {
		assertWidgetData(stateValues[0], isChecked());
	}

}
