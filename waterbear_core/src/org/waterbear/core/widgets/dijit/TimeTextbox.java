package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;

/**
 * <img src="timetextbox.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 *
 */
@Genable(setDataMethodName = "select")
public class TimeTextbox extends DojoWidget {

	public TimeTextbox(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void select(String timeText) {
		ElementStub inputBox = getBrowser().textbox("dijitReset dijitInputInner").in(es);
		inputBox.click();
		String popupDivId = inputBox.fetch("getAttribute('id')") + "_popup";
		getBrowser().div(timeText).in(getBrowser().div(popupDivId)).click();
	}

	/**
	 * 
	 * @param input
	 *            time(hh:mm AM/PM).
	 */
	public void setValue(String text) {
		getArrowButton().click();
		getBrowser().focus(getBrowser().textbox("dijitReset dijitInputInner").in(es));
		getBrowser().textbox("dijitReset dijitInputInner").in(es).setValue(text);
	}

	public String getValue() {
		return getBrowser().hidden(0).in(es).getValue();
	}

	protected ElementStub getArrowButton() {
		return getBrowser().textbox("dijitReset dijitInputField dijitArrowButtonInner")
				.in(es);
	}
	
	@Override
	public void verify(Object... stateValues) {
		assertWidgetData(stateValues[0], getValue());
	}
}
