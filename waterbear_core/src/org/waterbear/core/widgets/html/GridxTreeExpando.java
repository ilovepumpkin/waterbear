package org.waterbear.core.widgets.html;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.IntfExpando;
import org.waterbear.core.widgets.WebElement;

public class GridxTreeExpando extends WebElement implements IntfExpando {

	public GridxTreeExpando(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public boolean isOpen() {
		String classValue = getWidgetClasses();
		if (classValue.equals("gridxTreeExpandoCell gridxTreeExpandoCellOpen")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isClose() {
		return !isOpen();
	}

	private ElementStub innerElem() {
		return getBrowser().div("gridxTreeExpandoInner").in(es);
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

	public boolean hasNoChildren() {
		if (getBrowser().div(0).in(es).getAttribute("className")
				.contains("gridxTreeExpandoIconNoChildren")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isNoChildren() {
		return getBrowser().div(0).in(es).getAttribute("className")
				.contains("gridxTreeExpandoIconNoChildren");
	}

}
