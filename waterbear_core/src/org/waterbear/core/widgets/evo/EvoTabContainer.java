package org.waterbear.core.widgets.evo;

import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.TabContainer;

public class EvoTabContainer extends TabContainer {

	public EvoTabContainer(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<ElementStub> getTabList() {
		ElementStub tabListES = getBrowser().div(
				"nowrapTabStrip dijitTabContainerTop-tabs").in(es);
		List<ElementStub> tabs = getBrowser().span("tabLabel").in(tabListES)
				.collectSimilar();
		return tabs;
	}

}
