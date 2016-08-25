package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;

/**
 * <img src="password.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "setValue")
public class Password extends Textbox {

	public Password(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	protected ElementStub getInputBox() {
		return getBrowser().password("dijitReset dijitInputInner").in(es);
	}

}
