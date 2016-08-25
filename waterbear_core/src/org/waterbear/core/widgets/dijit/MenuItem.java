package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.ElementStub;

/**
 * <img src="menuitem.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class MenuItem extends DojoWidget {

	public MenuItem(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public boolean checked() {
		return getWidgetClasses().endsWith("dijitChecked");
	}

	public void assertChecked(boolean expected) {
		assertEquals(expected, checked());
	}

	public void easyCheck(boolean flag) {
		if (flag) {
			if (!checked()) {
				clickMenuItem();
			}
		} else {
			if (checked()) {
				clickMenuItem();
			}
		}
	}

	public String getMenuItemLabel() {
		return getBrowser().byId(getDojoWidgetId() + "_text").getText();
	}

	public void clickMenuItem() {
		es.click();
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub
		super.verify(stateValues);

		boolean expectedEnabled = (Boolean) stateValues[0];
		assertEquals(expectedEnabled, isEnabled());
	}

}
