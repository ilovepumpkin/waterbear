package org.waterbear.core.widgets.evo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoActiveUserCount extends DojoWidget {
	
	private static Pattern p = Pattern.compile("(\\d+)");

	public EvoActiveUserCount(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public int getUserCount() {
		String text = es.getText();
		Matcher m = p.matcher(text);
		m.find();
		return Integer.parseInt(m.group());
	}
}
