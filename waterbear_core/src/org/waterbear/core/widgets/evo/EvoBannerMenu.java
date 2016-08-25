package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoBannerMenu extends DojoWidget {

	public EvoBannerMenu(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void clickMenuItem(String menuItemText) {
		getBrowser().div(menuItemText).in(es).click();
	}
}
