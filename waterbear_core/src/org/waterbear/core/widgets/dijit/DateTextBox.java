package org.waterbear.core.widgets.dijit;

import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.annotation.Genable;

/**
 * <img src="datetextbox.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
@Genable(setDataMethodName = "select")
public class DateTextBox extends DojoWidget {

	public DateTextBox(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param select
	 *            date by year, month, day
	 */
	public void select(String year, String Month, String Day) {
		getArrowButton().click();
		ElementStub calPopup = getBrowser().table(getDojoWidgetId() + "_popup");
		getBrowser().span(year).in(calPopup).click();
		getMonthArrowButton().click();
		ElementStub monthPopup = getBrowser().div(getDojoWidgetId() + "_popup_mddb_mdd");
		getBrowser().div(Month).in(monthPopup).click();

		List<ElementStub> dayESs = getBrowser()
				.cell("dijitCalendarEnabledDate dijitCalendarCurrentMonth dijitCalendarDateTemplate")
				.in(calPopup).collectSimilar();
		for (Iterator it = dayESs.iterator(); it.hasNext();) {
			ElementStub es = (ElementStub) it.next();
			ElementStub dayES = getBrowser().span(Day).in(es);
			if (dayES.exists(true)) {
				dayES.click();
				break;
			}
		}
	}

	/**
	 * 
	 * @param input
	 *            date(year, month, day).
	 */
	public void inputDate(String text) {
		getArrowButton().click();
		getBrowser().focus(getBrowser().textbox("dijitReset dijitInputInner").in(es));
		getBrowser().textbox("dijitReset dijitInputInner").in(es).setValue(text);
	}

	/**
	 * 
	 * @param get
	 *            the select date
	 */
	public String getValue() {
		return getBrowser().textbox("dijitReset dijitInputInner").in(es).getValue();
	}

	protected ElementStub getArrowButton() {
		return getBrowser().textbox("dijitReset dijitInputField dijitArrowButtonInner")
				.in(es);
	}

	protected ElementStub getMonthArrowButton() {
		return getBrowser().span("dijitReset dijitInline dijitArrowButtonInner").in(
				getBrowser().table(getDojoWidgetId() + "_popup"));
	}

	@Override
	public void verify(Object... stateValues) {
		assertWidgetData(stateValues[0], getValue());
	}

}
