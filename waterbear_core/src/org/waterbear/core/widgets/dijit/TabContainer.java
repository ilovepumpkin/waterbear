package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.utils.WaitForCondition;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;

import net.sf.sahi.client.ElementStub;

public class TabContainer extends DojoWidget {

	public TabContainer(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public WebElement getVisibleTabPane() {
		return new WebElement(getBrowser().div(
				"dijitTabContainerTopChildWrapper dijitVisible").in(es));
	}

	public WebElement getTabPane(String tabText) {
		WebElement we = tabElement(tabText);
		String id = we.getAttribute("id");
		return WidgetFinder.webElem("{'aria-labelledby':'" + id + "'}", ET.DIV,
				es);
	}

	public void select(String tabText) {
		WebElement we = tabElement(tabText);
		we.click();
		String tabPaneClass = getBrowser()
				.div(we.getAttribute("id").split("tablist_")[1]).parentNode()
				.getAttribute("className");
		assertTrue("The selected tabPane is invisible.",
				tabPaneClass.endsWith("dijitVisible"));
	}

	private WebElement tabElement(String tabText) {
		WebElement we = WidgetFinder.webElem(tabText, ET.SPAN, getBrowser()
				.div(getDojoWidgetId() + "_tablist").in(es));
		return we;
	}

	protected List<ElementStub> getTabList() {
		ElementStub tabListES = getBrowser()
				.div(getDojoWidgetId() + "_tablist").in(es);
		List<ElementStub> tabs = getBrowser().span("tabLabel").in(tabListES)
				.collectSimilar();
		return tabs;
	}
}
