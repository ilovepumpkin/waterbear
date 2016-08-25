package org.waterbear.projects.common.widgets.evo;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.dijit.DojoWidget;

public abstract class RepeaterItem extends DojoWidget {

	public RepeaterItem(ElementStub es,Object...args) {
		super(es,args);
	}

	public boolean isSelected() {
		String classes = getWidgetClasses();
		return classes.contains("aspenFilteringRepeaterItemSelected");
	}
	
	public boolean isVisible(){
		String displayValue=es.style("display");
		if(displayValue!=null && displayValue.equals("none")){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * Return the text in bold.
	 * 
	 * @return
	 */
	public abstract String getLabel();
}
