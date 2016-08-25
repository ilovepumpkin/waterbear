package org.waterbear.core.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoPresetChooser2 extends DojoWidget {

	public EvoPresetChooser2(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void choose(String presetName) {
		ElementStub presetES = getBrowser().link(presetName);
		if (presetES.exists()) {
			presetES.click();
		} else {
			throw new ObjectNotFoundException("The preset [" + presetName
					+ "] was not found.");
		}
	}
}
