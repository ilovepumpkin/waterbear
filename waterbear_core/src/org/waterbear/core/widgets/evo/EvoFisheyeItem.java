package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoFisheyeItem extends DojoWidget {

	public EvoFisheyeItem(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void clickItem(String itemText) {
		ElementStub fishEyeIcon = getBrowser().image(0).in(es);
		try{
			fishEyeIcon.click();
			ElementStub itemLink = getBrowser().link(itemText).in(es);
			BrowserUtil.waitForCond(() -> itemLink.exists(), 10000);
			try{
				itemLink.click();
			}catch(RuntimeException e){
				BrowserUtil.resetToBaseUrl();
				throw new ObjectNotFoundException("Item Link [" + itemLink
						+ "] does not exist. Resetting to base url.",e);
			}
		}catch(RuntimeException e){
			BrowserUtil.resetToBaseUrl();
			throw new ObjectNotFoundException("Fisheye Icon [" + fishEyeIcon
					+ "] does not exist. Resetting to base url.",e);
		}
	}

}
