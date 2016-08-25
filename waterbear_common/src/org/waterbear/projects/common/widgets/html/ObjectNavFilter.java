package org.waterbear.projects.common.widgets.html;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.Textbox;
import org.waterbear.core.widgets.evo.EvoFilteringRepeater;
import org.waterbear.projects.common.widgets.evo.RepeaterItem;

import static org.waterbear.core.widgets.WidgetFinder.*;

public class ObjectNavFilter extends WebElement {
	private Class repeaterItemClazz;

	public ObjectNavFilter(ElementStub es, Class repeaterItemClazz) {
		super(es);
		this.repeaterItemClazz = repeaterItemClazz;
	}

	public int visibleItemCount() {
		return getObjLabels(true).size();
	}

	public void assertVisibleItemCount(int expected) {
		assertEquals(expected, visibleItemCount());
	}

	public void reset() {
		getBrowser().link("/bareA cancelIcon sprite.*/").in(es).click();
	}

	public void assertNotEmpty() {
		assertTrue(getObjLabels(true).size() > 0);
	}

	public void assertItems(List<String> expected) {
		List<String> actItems = getObjLabels(true);
		assertTrue(actItems.containsAll(expected)
				&& expected.containsAll(actItems));
	}

	public void assertAllItemsContains(String subStr) {
		List<String> l = getObjLabels(true);
		for (Iterator<String> it = l.iterator(); it.hasNext();) {
			String str = (String) it.next();
			assertTrue("[" + str + "] does not contain [" + subStr + "]", str
					.toLowerCase().contains(subStr.toLowerCase()));
		}
	}

	public List<String> search(String keyword) {
		getBrowser().link("Filter").in(es).click();
		Textbox searchBox = (Textbox) byIndex(0, Textbox.class, es);
		searchBox.setValue(keyword);
		searchBox.pressEnter();
		return getObjLabels(true);
	}

	public List<String> getAllObjLabels() {
		return getObjLabels(false);
	}

	private List<String> getObjLabels(boolean isVisibleOnly) {
		List<String> labelList = new ArrayList<String>();

		List<ElementStub> esList = getBrowser()
				.div("/aspenFilteringRepeaterItem.*/").in(es).collectSimilar();
		for (Iterator<ElementStub> it = esList.iterator(); it.hasNext();) {
			ElementStub item = (ElementStub) it.next();
			RepeaterItem ri = (RepeaterItem) widget(item,
					this.repeaterItemClazz);
			if (isVisibleOnly) {
				if (ri.isVisible()) {
					labelList.add(ri.getLabel());
				}
			} else {
				labelList.add(ri.getLabel());
			}
		}
		return labelList;
	}

	public RepeaterItem findByLabel(String name) {
		RepeaterItem ri = (RepeaterItem) byLabel(name, this.repeaterItemClazz,
				es);
		return ri;
	}

	public RepeaterItem waitFor(final String label) {
		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() {
				return isLabelExists(label);
			}
		};
		getBrowser().waitFor(cond, 30000);
		return findByLabel(label);
	}

	public boolean isLabelExists(String name) {
		try {
			findByLabel(name);
		} catch (ObjectNotFoundException e) {
			return false;
		}
		return true;
	}

	public void assertItemExists(String expected) {
		assertTrue(isLabelExists(expected));
	}

	public void assertItemNotExists(String expected) {
		assertFalse(isLabelExists(expected));
	}

	public RepeaterItem findByIndex(int index) {
		int count = getBrowser().div("/aspenFilteringRepeaterItem.*/")
				.countSimilar();
		if (count - 1 < index) {
			throw new AutomationException("Index [" + index
					+ "] is out of range [" + count + "].");
		} else {
			return (RepeaterItem) byIndex(index, this.repeaterItemClazz, es);
		}
	}

	public void scrollToEnd() {
		ElementStub contentPane = byIndex(0, EvoFilteringRepeater.class, es)
				.getElementStub().parentNode();
		String script = contentPane + ".scrollTop=" + contentPane
				+ ".scrollHeight";
		getBrowser().execute(script);
	}
}
