package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;
import org.waterbear.core.widgets.evo.EvoStatusAlertsWithPreviews;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class LRTPod extends DojoWidget {

	public LRTPod(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	public EvoStatusAlertsWithPreviews showStatusAlerts() {
		getBrowser().div("sbAlertIcon statusIcon sprite syncIcon").in(es)
				.hover();
		return (EvoStatusAlertsWithPreviews) byIndex(0,
				EvoStatusAlertsWithPreviews.class, es);
	}
}
