package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

public class MasterTooltip extends DojoWidget {

	public MasterTooltip(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String getContent() {
		return getBrowser().div("dijitTooltipContainer dijitTooltipContents")
				.in(es).getText();
	}
}
