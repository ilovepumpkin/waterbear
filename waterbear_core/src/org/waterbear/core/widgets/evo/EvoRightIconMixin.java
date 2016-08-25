package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoRightIconMixin extends DojoWidget {

	public EvoRightIconMixin(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String getSelected() {
		return getBrowser()
				.span("dijitReset dijitInline dijitSelectLabel dijitValidationTextBoxLabel ")
				.in(es).getText();
	}

	public void assertSelected(String expected) {
		assertEquals(expected, getSelected());
	}
}
