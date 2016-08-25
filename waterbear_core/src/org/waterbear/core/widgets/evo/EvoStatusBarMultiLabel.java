package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoStatusBarMultiLabel extends DojoWidget {

	public EvoStatusBarMultiLabel(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}
	
	public ElementStub getSBArrow() {
		return getBrowser().div("sbArrow").in(es);
	}
}
