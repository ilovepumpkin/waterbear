package org.waterbear.projects.common.widgets.html;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;

import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.widgets.WebElement;

import net.sf.sahi.client.ElementStub;

/**
 * 
 * You can see such table in Properties dialogue, like Drive Properties
 * dialogue.
 * 
 * Its structure looks like below:
 * 
 * <tr >
 * <td>label</td>
 * <td>value</td>
 * </tr>
 * <tr>
 * ...
 * </tr>
 * 
 * 
 */
public class TdTdPropsTable extends BasePropsTable {

	public TdTdPropsTable(ElementStub es) {
		super(es);
	}

	public TdTdPropsTable(WebElement we) {
		super(we.getElementStub());
	}

	/**
	 * 
	 * label - regular expression to locate the label
	 * 
	 */
	public String readProperyValue(String label) {
		WebElement cell = new WebElement(getBrowser().cell(1).near(
				getBrowser().cell("/" + label + ".*/").in(es)));
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
