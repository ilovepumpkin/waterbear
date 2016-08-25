package org.waterbear.core.widgets.dijit;

import net.sf.sahi.client.ElementStub;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class InlineEditor extends DojoWidget {

	public InlineEditor(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	@Override
	public void setValue(Object value) {
		Textbox tb = (Textbox) byIndex(0, Textbox.class, es);
		tb.setValue(value);
		es.blur();
	}
}
