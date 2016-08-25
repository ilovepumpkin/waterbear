package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.core.utils.asserter.TestStrategies;
import org.waterbear.core.utils.asserter.WBAsserter;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;

import static org.waterbear.core.widgets.WidgetFinder.*;

public class CssStateMixin extends DojoWidget {

	public CssStateMixin(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public WebElement touchButton() {
		return webElem("touchbutton", ET.DIV);
	}

	public WebElement hcmTextNode() {
		return webElem("hcmTextNode", ET.DIV);
	}

	public void assertLabel(String expected) {
		WBAsserter.assertText(TestStrategies.EQUALS, hcmTextNode().getText(),
				expected);
	}
	public void touch(boolean flag){
		if(hcmTextNode().getText().equals("Off")){
			if(flag){
				touchButton().click();
			}
		}
		else{
			if(!flag){
				touchButton().click();
			}
		}
	}
	
}
