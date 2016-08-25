package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertTrue;
import java.util.Iterator;
import java.util.List;

import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.utils.WaitForCondition;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;

import net.sf.sahi.client.ElementStub;
import static org.waterbear.core.widgets.WidgetFinder.*;

public class AccordionContainer extends DojoWidget {

	public AccordionContainer(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void selectSection(final String sectionText) {
		if (!isSectionFocused(sectionText)) {
			findSectionTitleBar(sectionText).click();
		}
		BrowserUtil.waitForCond(new WaitForCondition() {
			@Override
			public boolean test() {
				// TODO Auto-generated method stub
				return isSectionFocused(sectionText);
			}
		}, 10000);
	}

	private WebElement findSectionTitleBar(String sectionText) {
		List<ElementStub> sections = null;
		sections = getBrowser().span("dijitAccordionText").collectSimilar();

		for (Iterator<ElementStub> it = sections.iterator(); it.hasNext();) {
			ElementStub sectionES = (ElementStub) it.next();
			if (sectionES.getText().equals(sectionText)) {
				return new WebElement(sectionES);
			}
		}
		throw new ObjectNotFoundException("The section [" + sectionText
				+ "] was not found.");
	}

	public WebElement section(String sectionText) {
		String sectionDivId = findSectionTitleBar(sectionText).getAttribute(
				"id").replace("button_title", "wrapper");
		return webElem(sectionDivId, ET.DIV, es);
	}

	public boolean isSectionFocused(String sectionText) {
		ElementStub sectionES = findSectionTitleBar(sectionText).parentNode()
				.parentNode();
		String className = sectionES.fetch("className");
		return className.endsWith("dijitFocused")
				|| className.endsWith("dijitSelected");
	}
}
