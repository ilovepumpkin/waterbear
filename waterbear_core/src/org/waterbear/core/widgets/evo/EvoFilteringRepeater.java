package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoFilteringRepeater extends DojoWidget {

	public EvoFilteringRepeater(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public WebElement getItem(String itemText) {
		return WidgetFinder.webElem("{sahiText:'" + itemText
				+ "',className:'h3 lightBlack'}", ET.DIV, es);
	}
}
