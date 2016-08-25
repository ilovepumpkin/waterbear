package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertTrue;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

/**
 * <img src="button.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class Button extends DojoWidget {

	public Button(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String label() {
		return labelES().getText();
	}

	private ElementStub labelES() {
		ElementStub innerElement = getBrowser().span(
				"dijitReset dijitInline dijitButtonText").in(es);
		return innerElement;
	}

	public void click() {
		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() throws ExecutionException {
				return isVisible() && isEnabled();
			}
		};
		getBrowser().waitFor(cond, 10000);

		assertVisible(true);
		assertEnabled(true);

		ElementStub innerElement = labelES();
		log("_click", innerElement);
		innerElement.click();
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub

	}

}
