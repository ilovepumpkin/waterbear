package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;
import static org.waterbear.core.widgets.WidgetFinder.byAttribute;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.Textbox;

public class EvoGridFilterTextbox extends Textbox {

	public EvoGridFilterTextbox(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void resetFilter(){
		getBrowser().link("leftIcon icon").in(es).click();
		assertEquals("",getValue());
	}
	
	public void setBasicFilterText(String text) {
		setValue(text);
		pressEnter();
	}
}
