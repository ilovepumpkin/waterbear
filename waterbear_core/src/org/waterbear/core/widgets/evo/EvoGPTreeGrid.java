package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.waterbear.core.widgets.WidgetFinder.byIndex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.dijit.Expando;
import org.waterbear.core.widgets.dijit.TreeGrid;
import org.waterbear.core.widgets.dijit.DataGrid.Row;
import org.waterbear.core.widgets.html.GridxTreeExpando;

public class EvoGPTreeGrid extends TreeGrid {

	@Override
	public List<String> getColData(String colName) {
		List<String> l = super.getColData(colName);
		List<String> newList = new ArrayList<String>();
		for (Iterator<String> it = l.iterator(); it.hasNext();) {
			String str = (String) it.next();
			if (str.startsWith("+") || str.startsWith("-")) {
				str = str.substring(1, str.length());
			}
			newList.add(str);
		}
		return newList;
	}

	public void expandAll() {
		List<Expando> l = WidgetFinder.collect(Expando.class, es);
		for (Iterator<Expando> it = l.iterator(); it.hasNext();) {
			Expando e = (Expando) it.next();
			e.open();
		}
	}

	public Row expandPath(String... rowKeywords) {
		Row firstRow = findRowByText(rowKeywords[0]);
		int firstRowIndex = firstRow.getRowIndex();
		if (rowKeywords.length == 1) {
			return firstRow;
		}
		getExpandoInRow(firstRow.getElementStub()).open();
		Row r = null;
		int keywordIndex = 1;
		for (int j = firstRowIndex + 1; j < rowCount(); j++) {
			if (keywordIndex < rowKeywords.length) {
				String rowKeyword = rowKeywords[keywordIndex];
				r = findRowByIndex(j);
				if (r.getText().contains(rowKeyword)) {
					if (keywordIndex != rowKeywords.length - 1) {
						getExpandoInRow(r.getElementStub()).open();
						keywordIndex += 1;
					} else {
						return r;
					}
				}
			}
		}
		throw new AutomationException("Failed to expand the path ["
				+ WBStringUtils.stringtify(rowKeywords) + "].");
	}

	public void assertTotalChildRowCount_EQ(int expected) {
		assertEquals(expected, totalChildRowCount());
	}

	public int totalChildRowCount() {
		return getBrowser().div("/dojoxGridRow child.*/").countSimilar();
	}

	public WebElement visibleCellAt(int rowIndex, int colIndex) {
		Row row = findRowByIndex(rowIndex);
		return row.visibleCell(colIndex);
	}

	public Row findRowByIndex(final int rowIndex) {
		int rowCount = rowCount2();
		if (rowIndex > rowCount - 1) {
			throw new AutomationException("The row index " + rowIndex
					+ " is out of range (max rows count:" + rowCount + ")");
		}

		Row row = findRowBy(new RowTestable() {
			@Override
			public boolean test(ElementStub es, int rowIdx) {
				return rowIdx == rowIndex;
			}
		});

		if (row == null) {
			throw new ObjectNotFoundException(
					"Cannot find the row by the index [" + rowIndex
							+ "] in the table. Table data [" + getText() + "] ");
		}

		return row;
	}

	public Row findChildRow(String parentRowKeyword, String childRowKeyword) {
		Row row = expandRow(parentRowKeyword);
		for (int i = row.getRowIndex() + 1; i < rowCount(); i++) {
			Row r = findRowByIndex(i);
			if (hasNoChildren(r)) {
				if (r.getText().contains(childRowKeyword)) {
					return r;
				}
			} else {
				break;
			}
		}
		throw new ObjectNotFoundException("Failed to find the child row with ["
				+ childRowKeyword + "] under parent row with ["
				+ parentRowKeyword + "]");
	}

	/**
	 * 
	 * @param parentRowKeyword
	 * @return
	 */
	public List<Row> findChildRows(String parentRowKeyword) {
		List<Row> list = new ArrayList<Row>();
		Row row = findRowByText(parentRowKeyword);
		for (int i = row.getRowIndex() + 1; i < rowCount(); i++) {
			Row r = findRowByIndex(i);
			if (isParentRow(r)) {
				break;
			} else {
				list.add(r);
			}
		}
		return list;
	}

	public boolean isParentRow(Row r) {
		if (WaterBearConfiguration.instance.getDojoVersion().equals("1.9.0")) {
			return r.parentNode().getAttribute("className").contains("parent");
		} else {
			// the version bigger than 1.9.0, like 1.9.3, 1.9.7
			return !r.parentNode().getAttribute("className").contains("parent");
		}
	}

	public void assertChildRowsContains(String parentRowText,
			String... expectedTextInChildRows) {
		expandRow(parentRowText);
		List<Row> children = findChildRows(parentRowText);
		if (children.isEmpty()) {
			throw new AutomationException("Not child rows found for the row ["
					+ parentRowText + "]");
		} else {
			for (String expectedText : expectedTextInChildRows) {
				boolean found = false;
				for (Iterator<Row> it = children.iterator(); it.hasNext();) {
					Row row = (Row) it.next();
					if (row.getText().indexOf(expectedText) != -1) {
						found = true;
					}
				}
				if (!found) {
					throw new AutomationException("Child Rows do not contain ["
							+ expectedText + "]");
				}
			}
		}
	}

	public void assertChildRowsNotContains(String parentRowText,
			String expectedTextInChildRows) {
		expandRow(parentRowText);
		List<Row> children = findChildRows(parentRowText);
		boolean found = false;
		for (Iterator it = children.iterator(); it.hasNext();) {
			Row row = (Row) it.next();
			if (row.getText().indexOf(expectedTextInChildRows) != -1) {
				found = true;
			}
		}
		if (found) {
			throw new AutomationException("Child Rows contain ["
					+ expectedTextInChildRows + "]");
		}
	}

	public EvoGPTreeGrid(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

}