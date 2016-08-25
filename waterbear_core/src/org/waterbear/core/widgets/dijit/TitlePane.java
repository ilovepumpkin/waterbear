package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertTrue;
import net.sf.sahi.client.ElementStub;

public class TitlePane extends DojoWidget {

	public TitlePane(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public boolean isClosed() {
		return titleBarNode().getAttribute("className").endsWith(
				"dijitTitlePaneTitleClosed dijitClosed");
	}

	public boolean isOpen() {
		return titleBarNode().getAttribute("className").endsWith(
				"dijitTitlePaneTitleOpen dijitOpen");
	}

	private ElementStub titleBarNode() {
		return getBrowser().div("titleBarNode").in(es);
	}

	public void click() {
		titleBarNode().click();
	}

	public void open() {
		if (isClosed()) {
			click();
		}
	}

	public void close() {
		if (isOpen()) {
			click();
		}
	}

	public void assertOpen() {
		assertTrue(isOpen());
	}

	public void assertClosed() {
		assertTrue(isClosed());
	}
}
