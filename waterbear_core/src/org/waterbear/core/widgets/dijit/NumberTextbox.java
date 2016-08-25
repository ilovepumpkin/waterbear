package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

public class NumberTextbox extends Textbox {

	public NumberTextbox(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	@Override
	public String getValue() {
		String value = super.getValue();
		return value.replaceAll(",", "");
	}
}
