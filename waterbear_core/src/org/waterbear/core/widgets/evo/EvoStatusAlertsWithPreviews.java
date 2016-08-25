package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class EvoStatusAlertsWithPreviews extends DojoWidget {

	public EvoStatusAlertsWithPreviews(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public EvoStatusMsg findMsg(String text) {
		return (EvoStatusMsg) byLabel(text, EvoStatusMsg.class, es);
	}

	public EvoStatusMsg findMsg(int index) {
		return byIndex(index, EvoStatusMsg.class, es);
	}

}
