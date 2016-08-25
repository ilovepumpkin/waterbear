package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.Button;
import org.waterbear.core.widgets.dijit.DojoWidget;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class ApplyChangesInlinePanel extends DojoWidget {

	public ApplyChangesInlinePanel(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return getBrowser().div("pad10b").in(es).getText();
	}

	public Button getApplyChangesButton() {
		return (Button) byLabel("Apply Changes", Button.class, es);
	}
}
