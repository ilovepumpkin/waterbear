package org.waterbear.core.widgets.evo;

import static org.waterbear.core.widgets.WidgetFinder.byIndex;
import static org.waterbear.core.widgets.WidgetFinder.webElem;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoEventBadge extends DojoWidget {

	public EvoEventBadge(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}
	
	public EvoStatusAlertsWithPreviews showStatusAlerts() {
		getBrowser().div("sbAlertIcon").in(es)
				.hover();
		return (EvoStatusAlertsWithPreviews) byIndex(0,
				EvoStatusAlertsWithPreviews.class, es);
	}

	public WebElement badgeValue() {
		return webElem("badgeValue", ET.DIV, es);
	}
}
