package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class EvoSettingsContainer extends DojoWidget {

	public EvoSettingsContainer(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public WebElement controller() {
		return webElem(getDojoWidgetId() + "_controller", ET.DIV, es);
	}

	public WebElement entry(String label) {
		return webElem(label, ET.DIV, controller());
	}
}
