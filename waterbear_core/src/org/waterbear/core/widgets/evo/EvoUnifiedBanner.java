package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;

import static org.waterbear.core.widgets.WidgetFinder.*;

public class EvoUnifiedBanner extends DojoWidget {

	public EvoUnifiedBanner(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String currPageName() {
		return getBrowser().span("ubNavLabel")
				.in(getBrowser().link("ubBCCurrent ").in(es)).getText();
	}

	public void assertCurrPageName(String expected) {
		assertEquals(expected, currPageName());
	}

	public WebElement breadcrumbs() {
		return webElem("breadcrumbs", ET.DIV, es);
	}

	public WebElement username() {
		return webElem("username hasEllipsisTooltip", ET.LINK, es);
	}

	public WebElement help() {
		return webElem("ubIconHelp sprite", ET.LINK, es);
	}

	public WebElement extraInfoSpan() {
		return webElem("extraInfoSpan", ET.SPAN, es);
	}

	public EvoBannerMenu helpMenu() {
		return (EvoBannerMenu) byIndex(0, EvoBannerMenu.class);
	}

	public WebElement productName() {
		return webElem("ubProductName", ET.DIV, es);
	}

	public WebElement currentPageLabel() {
		return webElem("ubNavLabel", ET.SPAN,
				webElem("ubBCCurrent ", ET.LINK, es));
	}
}
