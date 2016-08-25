package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

/**
 * 
 * @author likz@cn.ibm.com
 * 
 */

public class EvoHotspotGraphicOverlay extends DojoWidget {
	public EvoHotspotGraphicOverlay(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
	}

	public void selectByNodeId(String nodeId) {
		List items = getBrowser().link("/hotspot.*/").in(es).collectSimilar();
		int count = items.size();
		for (int i = 0; i < count; i++) {
			ElementStub item = (ElementStub) items.get(i);
			String temp = item.fetch("getAttribute('nodeid')");
			if (temp.equals(nodeId)) {
				item.click();
				item.focus();
				item.hover();
				assertEquals(nodeId, getSelectedNodeId());
				break;
			}
		}
	}

	private String getSelectedNodeId() {
		return getBrowser().link("/hotspot.*highlite.*/").in(es)
				.fetch("getAttribute('nodeid')");
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub
		assertEquals(stateValues[0], getSelectedNodeId());
	}
}
