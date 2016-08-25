package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;

/**
 * <img src="numberspinner.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "setValue")
public class NumberSpinner extends Textbox {

	public NumberSpinner(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getValue() {
		String value = super.getValue();
		return value.replaceAll(",", "");
	}

}
