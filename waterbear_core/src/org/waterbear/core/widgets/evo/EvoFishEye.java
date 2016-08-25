package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoFishEye extends DojoWidget {

	public EvoFishEye(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void clickMenuItem(String level1Text, String level2Text) {
		EvoFisheyeItem item = WidgetFinder.byAttribute(
				"{className:'title',sahiText:'" + level1Text + "'}",
				EvoFisheyeItem.class, es);
		item.toFatEsMode();
		item.clickItem(level2Text);
	}
}
