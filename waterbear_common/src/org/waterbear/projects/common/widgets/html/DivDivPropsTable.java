package org.waterbear.projects.common.widgets.html;

import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.widgets.WebElement;

import net.sf.sahi.client.ElementStub;

public class DivDivPropsTable extends BasePropsTable {

	public DivDivPropsTable(WebElement we) {
		super(we);
		// TODO Auto-generated constructor stub
	}

	public DivDivPropsTable(ElementStub es) {
		super(es);
		// TODO Auto-generated constructor stub
	}

	@Override
	String readProperyValue(String label) {
		WebElement cell = new WebElement(getBrowser().label(1).near(
				getBrowser().label("/" + label + ".*/").in(es)));
		if (!cell.exists()) {
			throw new ObjectNotFoundException(
					"Failed to locate the property label with [" + label + "]");
		}
		String value = cell.getText();
		return value.split("\\?")[0].trim();// remove the tool tip text
											// following
											// the real property value
	}

}
