package org.waterbear.core.widgets.evo;

import static org.waterbear.core.widgets.WidgetFinder.byIndex;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.Where;
import org.waterbear.core.widgets.dijit.DojoWidget;
import org.waterbear.core.widgets.dijit.InlineEditor;

public class EvoInlineValidatingEditBox extends DojoWidget {

	public EvoInlineValidatingEditBox(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Object value) {
		es.click();
		InlineEditor editor = (InlineEditor) byIndex(0, InlineEditor.class, es,
				Where.NEAR);
		editor.setValue(value);
	}

	@Override
	public String getValue() {
		return es.getText();
	}
}
