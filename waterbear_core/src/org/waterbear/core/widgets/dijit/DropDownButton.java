package org.waterbear.core.widgets.dijit;

import static org.waterbear.core.widgets.WidgetFinder.*;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.evo.EvoMenu;

/**
 * <img src="dropdownbutton.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class DropDownButton extends DojoWidget {

	public DropDownButton(ElementStub es, Object... stateValues) {
		super(es, stateValues);
	}

	public EvoMenu popupEvoMenu() {
		click();
		return findActiveEvoMenu();
	}

	public Menu popupMenu() {
		click();
		return findActiveMenu();
	}

	public void click() {
		webElem("/dijit_form_DropDownButton_.*_label/", ET.SPAN, es).click();
	}

}
