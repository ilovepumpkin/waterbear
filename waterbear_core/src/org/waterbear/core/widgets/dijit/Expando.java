package org.waterbear.core.widgets.dijit;

import org.waterbear.core.widgets.IntfExpando;

import net.sf.sahi.client.ElementStub;

public class Expando extends DojoWidget implements IntfExpando {

	public Expando(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	public boolean isOpen() {
		String classValue = getWidgetClasses();
		if (classValue.equals("dojoxGridExpando dojoxGridExpandoOpened")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isClose() {
		return !isOpen();
	}

	private ElementStub innerElem() {
		return getBrowser().div("dojoxGridExpandoNodeInner").in(es);
	}

	public void open() {
		if (isClose()) {
			innerElem().click();
		}
	}

	public void close() {
		if (isOpen()) {
			innerElem().click();
		}
	}

	@Override
	public boolean isNoChildren() {
		return es.parentNode().getAttribute("aria-expanded").equals("");
	}
}
