package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.waterbear.core.widgets.WidgetFinder.byIndex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.widgets.IntfExpando;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.DataGrid.Row;
import org.waterbear.core.widgets.evo.EvoMenu;

public class TreeGrid extends DataGrid {

	public TreeGrid(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
	}

	// public WebElement findCellByText(String text) {
	// return new WebElement(getBrowser().parentCell(
	// getBrowser().span(text).in(es)));
	// }

	public EvoMenu popupItemEvoMenuOn(String cellText, int item) {
		if (item > 1) {
			throw new AutomationException("The item [" + item
					+ "] is out of range [0-1].");
		}
		WebElement elem = findRowByText(cellText);
		ElementStub row = getBrowser().row(item + 1).in(elem.parentNode());
		return popupEvoMenuOn(new WebElement(getBrowser().cell(0).in(row)));
	}

	// @Override
	// public EvoMenu popupEvoMenuOn(String[] cellTexts) {
	// int len = cellTexts.length;
	// EvoMenu p = null;
	// if (len <= 0) {
	// throw new AutomationException("cellTexts length is zero!");
	// } else {
	// WebElement cell = findCellByText(cellTexts[0]);
	// if (len > 1) {
	// multiSelect(cellTexts);
	// cell = findCellByText(cellTexts[cellTexts.length - 1]);
	// }
	// p = popupEvoMenuOn(cell);
	// }
	// return p;
	// }

	protected void multiSelect(String[] cellTexts) {
		for (int i = 0; i < cellTexts.length; i++) {
			String cellText = cellTexts[i];
			selectCell(cellText, COMBO_KEY_CTRL);
		}
	}

	public void selectCell(String keyword, String comboKey) {
		WebElement cell = findCellByText(keyword);
		cell.clickWithCombo(comboKey);
	}

	@Override
	public void assertColData(String colName, List<String> expected) {
		List<String> colData = getColData(colName);
		List<String> trimedColData = new ArrayList<String>();
		colData.stream().map((s) -> {
			return s.replace("+", "").trim();
		}).forEach((s) -> {
			trimedColData.add(s);
		});
		assertEquals(expected.toString(), trimedColData.toString());
	}

	public IntfExpando getExpandoInRow(ElementStub rowES) {
		return (Expando) byIndex(0, Expando.class, rowES);
	}

	/**
	 * 
	 * @param textInRow
	 *            - the text to find the row, which could be part of the text
	 */
	public Row expandRow(String textInRow) {
		Row r = findRowByText(textInRow);
		getExpandoInRow(r.getElementStub()).open();
		return r;
	}

	public void collapseRow(String textInRow) {
		if (verifyIfClicked(textInRow)) {
			ElementStub rowes = findRowByText(textInRow).getElementStub();
			getExpandoInRow(rowes).close();
		}
	}

	public void assertNoChildRows(String textInRow) {
		Row r = findRowByText(textInRow);
		assertTrue(hasNoChildren(r));
	}

	public boolean hasNoChildren(Row r) {
		String rowClassValue = getBrowser().row(0).in(r.getElementStub())
				.getAttribute("className");
		if (rowClassValue.contains("dojoxGridNoChildren")) {
			return true;
		} else {
			return false;
		}
	}

	public void assertHasChildRows(String textInRow) {
		ElementStub rowES = findRowByText(textInRow).getElementStub();
		String classValue = getBrowser().row(0).in(rowES).fetch("className");
		assertFalse("[" + classValue + "] contains 'dojoxGridNoChildren'.",
				classValue.contains("dojoxGridNoChildren"));
	}

	public Boolean verifyIfClicked(String textInRow) {
		ElementStub rowes = findRowByText(textInRow).getElementStub();
		ElementStub expandIcon = getBrowser().div("dojoxGridExpandoNodeInner")
				.in(rowes);
		if (expandIcon.getText().equals("-")) {
			return true;
		} else
			return false;
	}

	public void assertChildRowsContains(String parentRowText,
			String... expectedTextInChildRows) {
		expandRow(parentRowText);
		Row r = findRowByText(parentRowText);
		for (String expectedText : expectedTextInChildRows) {
			ElementStub child = getBrowser().row("/.*" + expectedText + ".*/")
					.in(r.getElementStub());
			assertTrue("The child row containing [" + expectedText
					+ "] was not found under the row [" + parentRowText + "].",
					child.exists());
		}
	}

	public WebElement findCell(String keyword, String columnName) {
		final int colIndex = getColIdxByColName(columnName, false);
		return findRowByText(keyword).findSubrowByText(keyword).cell(colIndex);
	}
}
