package org.waterbear.projects.common.widgets.html;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import org.waterbear.core.widgets.WebElement;

import net.sf.sahi.client.ElementStub;

/**
 * 
 * Such table is not in Details dialogue, like Pool Details dialogue.
 * 
 * Its structure looks like below:
 * 
 * <tr>
 * <th>label</th>
 * <td>value</td>
 * </tr>
 * <tr>
 * ...
 * </tr>
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class ThTdPropsTable extends BasePropsTable {

	public ThTdPropsTable(ElementStub es) {
		super(es);
	}

	public ThTdPropsTable(WebElement we) {
		super(we.getElementStub());
	}

	public String readProperyValue(String label) {
		return getBrowser().cell(0)
				.near(getBrowser().tableHeader("/" + label + ".*/").in(es))
				.getText();
	}
}
