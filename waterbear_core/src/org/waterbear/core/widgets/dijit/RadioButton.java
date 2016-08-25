package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import org.waterbear.core.exception.MethodNotSupportedException;

import net.sf.sahi.client.ElementStub;

/**
 * <img src="radiobutton.jpeg">
 * 
 * @author junjiyu@cn.ibm.com
 * 
 */
public class RadioButton extends DojoWidget {
	public RadioButton(ElementStub es, Object[] initStateValues) {
		super(es, initStateValues);
		// TODO Auto-generated constructor stub
	}

	private ElementStub getCheckBoxInput() {
		return getBrowser().radio("dijitReset dijitCheckBoxInput").in(es);
	}

	public void select() {
		getCheckBoxInput().check();
	}

	public void click() {
		throw new MethodNotSupportedException(
				"To select the radio button, use the method 'select' instead.");
	}

	public String getValue() {
		return getCheckBoxInput().getValue();
	}

	public boolean checked() {
		return getCheckBoxInput().checked();
	}

	public void assertChecked(boolean checked) {
		assertEquals(checked, checked());
	}

	@Override
	public void verify(Object... stateValues) {
		boolean isSelected = (Boolean) stateValues[0];
		assertWidgetData(isSelected, checked());
	}
}
