package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertTrue;
import static org.waterbear.core.widgets.WidgetFinder.byAttribute;
import static org.waterbear.core.widgets.WidgetFinder.widget;

import org.waterbear.core.widgets.dijit.DojoWidget;
import org.waterbear.core.widgets.dijit.Menu;
import org.waterbear.core.widgets.dijit.MenuItem;

import net.sf.sahi.client.ElementStub;

/**
 * <img src="menu.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class EvoMenu extends Menu {

	public EvoMenu(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	public EvoMenu popupEvoMenu(String actionText) {
		MenuItem mi = getMenuItemByLabel(actionText);
		assertTrue("The menu item [" + actionText
				+ "] is expected to be enabled but disabled now.",
				mi.isEnabled());
		final String newMenuDivId = mi.getDojoWidgetId() + "_dropdown";
		final ElementStub newMenuES = getBrowser().table(0).in(
				getBrowser().div(newMenuDivId));
		mi.clickMenuItem();
		return (EvoMenu) widget(newMenuES, EvoMenu.class);
	}
	
	
}
