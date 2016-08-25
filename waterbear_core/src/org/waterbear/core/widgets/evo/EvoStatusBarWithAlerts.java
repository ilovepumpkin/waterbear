package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;
import static org.waterbear.core.widgets.WidgetFinder.byIndex;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoStatusBarWithAlerts extends DojoWidget {

	public EvoStatusBarWithAlerts(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	public EvoStatusAlertsWithPreviews showStatusAlerts() {
		getSbAlertIcon().hover();
		return (EvoStatusAlertsWithPreviews) byIndex(0,
				EvoStatusAlertsWithPreviews.class, es);
	}

	/**
	 * Green: return true; Red: return false
	 */
	public boolean curState() {
		if (getSbAlertIcon().exists()) {
			return false;
		} else {
			return true;
		}
	}

	public ElementStub getSbAlertIcon() {
		return getBrowser().div("sbAlertIcon sprite error").in(es);
	}

	public void assertCurState(boolean expected) {
		assertEquals(curState(), expected);
	}
}
