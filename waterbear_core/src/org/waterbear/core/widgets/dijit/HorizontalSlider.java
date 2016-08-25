package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.ElementStub;

/**
 * <img src="button.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class HorizontalSlider extends DojoWidget {

	public HorizontalSlider(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public int getIntValue() {
		return Integer.parseInt(getBrowser().hidden(0).in(es).getValue());
	}

	@Override
	public String getValue() {
		return String.valueOf(getIntValue());
	}

	protected ElementStub getSliderHandle() {
		return getBrowser().div("dijitSliderMoveable dijitSliderMoveableH").in(
				es);
	}

	public void setValue(int value) {
		getBrowser().execute(
				"dijit.byId('" + getDojoWidgetId() + "').setValue(" + value
						+ ");");
	}

	public void assertSelected(int expected) {
		assertEquals(getIntValue(), expected);
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub
	}

}
