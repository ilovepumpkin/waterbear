package org.waterbear.projects.common.widgets.html;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.widgets.WebElement;

public class HtmlTable extends WebElement {

	private int headingRowCount;

	public HtmlTable(ElementStub es) {
		this.es = es;
	}

	public HtmlTable(WebElement we) {
		this.es = we.getElementStub();
	}

	public void setHeadingRowCount(int headingRowCount) {
		this.headingRowCount = headingRowCount;
	}

	/**
	 * Locate cell with row/column index.
	 * 
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 */
	public WebElement cell(int rowIndex, int colIndex) {
		ElementStub cell = getBrowser().cell(es, rowIndex + headingRowCount,
				colIndex).in(es);
		if (cell.exists()) {
			return new WebElement(cell);
		} else {
			throw new ObjectNotFoundException("Failed to locate the cell at ["
					+ rowIndex + "," + colIndex + "] in the table [" + es + "]");
		}
	}

	/**
	 * Locate row with row index - starting from zero.
	 */
	public WebElement row(int rowIndex) {
		ElementStub rowES = getBrowser().row(rowIndex + headingRowCount).in(es);
		if (rowES.exists()) {
			return new WebElement(rowES);
		} else {
			throw new ObjectNotFoundException(
					"Can not find the row with index " + rowIndex
							+ " in the table [" + es + "]");
		}
	}

	/**
	 * Locate row with text
	 */
	public WebElement row(String text) {
		ElementStub rowES = getBrowser().row("/.*" + text + ".*/").in(es);
		if (rowES.exists()) {
			return new WebElement(rowES);
		} else {
			throw new ObjectNotFoundException(
					"Can not find the row with the text '" + text
							+ " in the table [" + es + "].");
		}
	}

	private List<WebElement> dataRows() {
		List<WebElement> rows = new ArrayList<WebElement>();
		int rowCount = dataRowCount();
		for (int i = 0; i < rowCount; i++) {
			rows.add(new WebElement(getBrowser().row(i + headingRowCount)
					.in(es)));
		}
		return rows;
	}

	public void assertCellText(int rowIndex, int colIndex, String expected) {
		assertEquals(expected, cell(rowIndex, colIndex).getText());
	}

	public WebElement findCellByText(String text) {
		return new WebElement(getBrowser().cell(text).in(es));
	}

	public int dataRowCount() {
		String rowCountStr = es
				.fetch("getElementsByTagName(\"TBODY\")[0].children.length");
		int rowCount = Integer.parseInt(rowCountStr);
		return rowCount;
	}

	public void assertRowVisible(String text) {
		WebElement we = row(text);
		assertFalse(we.getWidgetClasses().contains("hidden"));
	}

	public void assertRowInvisible(String text) {
		WebElement we = row(text);
		assertTrue(we.getWidgetClasses().contains("hidden"));
	}

	public void assertTableData(String[][] tableData) {
		for (int i = 0; i < tableData.length; i++) {
			String[] rowData = tableData[i];
			for (int j = 0; j < rowData.length; j++) {
				String cellData = rowData[j];
				assertCellText(i, j, cellData);
			}
		}
	}
}
