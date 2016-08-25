package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.utils.asserter.TestStrategies;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;

import static org.waterbear.core.widgets.WidgetFinder.*;

public class EvoSettingsContainerToolbar extends DojoWidget {

	public EvoSettingsContainerToolbar(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public WebElement save() {
		WebElement we = webElem("Save", ET.SUBMIT, es);
		we.assertEnabled(true);
		return we;
	}

	public WebElement reset() {
		WebElement we = webElem("Reset", ET.SUBMIT, es);
		we.assertEnabled(true);
		return we;
	}

	public WebElement statusNode() {
		return webElem("statusNode", ET.DIV, es);
	}

	public void assertSuccess() {
		WebElement statusNode = statusNode();
		statusNode.assertVisible(true);
		statusNode.assertText(TestStrategies.IN, "Saved", "Saving...");
	}
}
