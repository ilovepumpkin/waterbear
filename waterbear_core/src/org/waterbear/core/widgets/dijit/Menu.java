package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertTrue;
import static org.waterbear.core.widgets.WidgetFinder.byAttribute;
import static org.waterbear.core.widgets.WidgetFinder.widget;
import net.sf.sahi.client.ElementStub;

public class Menu extends DojoWidget {

	public Menu(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void assertMenuItemsEnableStates(boolean expected, String... labels) {
		for (String label : labels) {
			getMenuItemByLabel(label).assertEnabled(expected);
		}
	}

	public MenuItem clickAction(String text) {
		MenuItem mi = getMenuItemByLabel(text);
		assertTrue("The menu item [" + text
				+ "] is expected to be enabled but disabled now.",
				mi.isEnabled());
		mi.clickMenuItem();
		return mi;
	}

	public MenuItem eachCheck(String text, boolean flag) {
		MenuItem mi = getMenuItemByLabel(text);
		mi.easyCheck(flag);
		return mi;
	}

	public MenuItem getMenuItemByLabel(String label) {
		return (MenuItem) byAttribute(label, MenuItem.class, es);
	}

	public String[] getContextualList() {
		return new String[] {};
	}

	public boolean isVisible() {
		return es.parentNode().isVisible();
	}

	public Menu popupMenu(String actionText) {
		MenuItem mi = clickAction(actionText);
		final String newMenuDivId = mi.getDojoWidgetId() + "_dropdown";
		final ElementStub newMenuES = getBrowser().table(0).in(
				getBrowser().div(newMenuDivId));
		return (Menu) widget(newMenuES, Menu.class);
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub

	}
}
