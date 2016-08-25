package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DojoWidget;
import org.waterbear.core.widgets.dijit.Select;
import org.waterbear.core.widgets.dijit.Textbox;
import org.waterbear.core.widgets.dijit.Button;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class EvoGridFilterDropDown extends DojoWidget {

	public static final String AND = "AND";
	public static final String OR = "OR";

	public EvoGridFilterDropDown(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void setBasicFilterText(String text) {
		Textbox inputBox = (Textbox) byAttribute("filter", Textbox.class, es);
		inputBox.setValue(text);
		inputBox.pressEnter();
	}

	public void toAdvancedMode() {
		getBrowser().link("expandMenuArrow").parentNode().click();
	}

	public void clickApply() {
		((Button) byLabel("Apply", Button.class, es)).click();
	}

	public void clickReset() {
		((Button) byLabel("Reset", Button.class, es)).click();
	}

	public void clickBasic() {
		((Button) byLabel("Basic", Button.class, es)).click();
	}

	public void toBasicMode() {
		clickBasic();
	}

	public void addCriteriaLine(int lineIdx) {
		getBrowser().link("sprite plusIcon").in(row(lineIdx)).click();
	}

	public void removeCriteriaLine(int lineIdx) {
		getBrowser().link("sprite cancelIcon").in(row(lineIdx)).click();
	}

	public void setCriteria(int lineIdx, String andOr, String colName,
			String operator, String value) {
		ElementStub row = row(lineIdx);

		WebElement andOrCell = webElem("andor", ET.CELL, row);
		String currAndOrValue = andOrCell.getText();
		if (!currAndOrValue.equals(andOr)) {
			webElem(0, ET.SPAN, andOrCell).click();
			assertEquals(andOr, andOrCell.getText());
		}

		((Select) byIndex(0, Select.class, row)).select(colName);
		((Select) byIndex(1, Select.class, row)).select(operator);
		((Textbox) byAttribute("val-q", Textbox.class, row)).setValue(value);
	}

	protected ElementStub row(int lineIdx) {
		return getBrowser().row(lineIdx * 3).in(
				getBrowser().table(0).in(getBrowser().div("gfap").in(es)));
	}
}
