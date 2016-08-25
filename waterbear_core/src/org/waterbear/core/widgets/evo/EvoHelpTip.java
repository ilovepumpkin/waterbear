package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertTrue;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoHelpTip extends DojoWidget {

	public EvoHelpTip(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String getHelpTipText() {
		getBrowser().span(0).in(es).mouseOver();
		assertTrue("Help tip is expected to be visiable but it was not.",
				es.isVisible());
		ElementStub c = getBrowser().span("helpTipContainer").in(es);
		return c.getText();
	}
}
