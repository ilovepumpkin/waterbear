package org.waterbear.projects.common.widgets;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.evo.EvoLayoutFilterHeading;
import org.waterbear.projects.common.widgets.html.ObjectNavFilter;

public class CommonWidgetFinder extends WidgetFinder {

	protected static ElementStub findMainContentPane() {
		ElementStub es = getBrowser().div("/dijit_layout_StackContainer_.*/");
		if (!es.exists()) {
			es = getBrowser().div("/dijit_layout_BorderContainer_.*/");
		}
		if (es.exists()) {
			String id = es.fetch("id");
			es = getBrowser().div(id);
		} else {
			throw new AutomationException("Cannot find the page container!!");
		}
		return es;
	}

	public static WebElement findBorderContainerBottomUp(WebElement widget) {
		ElementStub es = getBrowser()
				.parentNode(widget.getElementStub(), "div");
		while (!es.fetch("id").startsWith("dijit_layout_BorderContainer")) {
			es = es.parentNode("div");
		}
		return new WebElement(es);
	}
	
	public static ObjectNavFilter findObjNavFilter(String heading,
			Class repeaterItemClazz, Object... args) {
		EvoLayoutFilterHeading filterHeading = (EvoLayoutFilterHeading) byLabel(
				heading, EvoLayoutFilterHeading.class, args);
		return new ObjectNavFilter(filterHeading.getElementStub().parentNode()
				.parentNode(), repeaterItemClazz);
	}
}
