package org.waterbear.core.widgets.dijit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;


/**
 * <img src="searchablefilter.jpeg">
 * 
 * @author sxyu@cn.ibm.com
 * 
 */

public class SearchableFilter extends DojoWidget {
	
	public SearchableFilter(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void enableSearch() {
		log("_enableSearch");
		getBrowser().link("Filter").in(es).click();
	}

	public ElementStub getInputBox() {
		ElementStub inputbox = getBrowser().textbox("dijitReset dijitInputInner").in(es);
		return inputbox;
	}

	public String getItemList() {
		ArrayList itemList = new ArrayList();
		List<ElementStub> results = getBrowser()
				.div("/" + "aspen_users_UserGroupRepeaterItem_" + "\\d+/").collectSimilar();
		for (Iterator it = results.iterator(); it.hasNext();) {
			ElementStub result = (ElementStub) it.next();			
			if (result.isVisible()){
				itemList.add(result.getText());
			}
		}
		return itemList.toString();
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public boolean selectByText(String text) {
		ElementStub group = getBrowser().div(text).in(es);
		group.click();
		getBrowser().waitFor(3000);
		return true;
	}
	
	public String cancelSearch() {
		ElementStub cancelsearch = getBrowser().link("Filter").in(es);	
		cancelsearch.click();
		
		ArrayList itemList = new ArrayList();
		List<ElementStub> results = getBrowser()
				.div("/" + "aspen_users_UserGroupRepeaterItem_" + "\\d+/").collectSimilar();
		for (Iterator it = results.iterator(); it.hasNext();) {
			ElementStub result = (ElementStub) it.next();			
			itemList.add(result.getText());
		}
		return itemList.toString();
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub
		
	}

}
