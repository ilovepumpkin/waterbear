package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.waterbear.core.widgets.WidgetFinder.byIndex;
import static org.waterbear.core.widgets.WidgetFinder.byLabel;
import static org.waterbear.core.widgets.WidgetFinder.findActiveEvoMenu;
import static org.waterbear.core.widgets.WidgetFinder.findActiveMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

import org.waterbear.core.DojoVersion;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.core.utils.WaitForCondition;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.Where;
import org.waterbear.core.widgets.dijit.validators.IValidator;
import org.waterbear.core.widgets.evo.EvoGridFilterTextbox;
import org.waterbear.core.widgets.evo.EvoGridToolbar;
import org.waterbear.core.widgets.evo.EvoMenu;

import static org.waterbear.core.widgets.WidgetFinder.*;

/**
 * <img src="datagrid.jpeg">
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class DataGrid extends DojoWidget {

	public final static String ORDER_ASC = "ascending";
	public final static String ORDER_DESC = "descending";
	private final static String EXPECTED_LAST_DOWNLOAD_FILE_NAME = "export.csv";

	private String iframeId;
	private int checkboxColCount = 0;

	public DataGrid(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void setIFrameId(String theIFrameId) {
		this.iframeId = theIFrameId;
	}

	public void setCheckboxColCount(int checkboxColCount) {
		this.checkboxColCount = checkboxColCount;
	}

	/**
	 * Locate a visible cell by its row index and column index
	 * 
	 * @param rowIndex
	 *            Starting from zero
	 * @param colIndex
	 *            Starting from zero
	 * @return
	 */
	public WebElement visibleCellAt(int rowIndex, int colIndex) {
		Row row = findRowByIndex(rowIndex);
		return row.visibleCell(colIndex);
	}

	/**
	 * @return table column headers
	 * @param displayedOnly
	 *            Set it true to get the displayed-only header cells
	 */
	public List<WebElement> getColHeaders(boolean visibleOnly) {
		List<WebElement> colHeaders = new ArrayList<WebElement>();
		List<ElementStub> esHeaders = getBrowser()
				.tableHeader("/" + getDojoWidgetId() + "Hdr.*/")
				.in(headTable()).collectSimilar();
		for (ElementStub es : esHeaders) {
			if (!visibleOnly || es.isVisible()) {
				colHeaders.add(new DojoxHeaderCell(es));
			}
		}
		return colHeaders;
	}

	/**
	 * 
	 * 
	 */
	public int getColCount(boolean visibleOnly) {
		int count = 0;
		List<ElementStub> esHeaders = getBrowser()
				.tableHeader("/" + getDojoWidgetId() + "Hdr.*/")
				.in(headTable()).collectSimilar();
		for (ElementStub es : esHeaders) {
			if (!visibleOnly || es.isVisible()) {
				count += 1;
			}
		}
		return count;
	}

	public void assertColumnNames(List<String> expected) {
		List<String> actColNames = getColNames(true);
		assertTrue(
				"The actual column names are not identical to the expected names. actual:"
						+ actColNames + ", expected: " + expected,
				actColNames.containsAll(expected)
						&& expected.containsAll(actColNames));
	}

	/**
	 * 
	 * @param visibleOnly
	 * @return
	 */
	public List<String> getColNames(boolean visibleOnly) {
		List<String> colNames = new ArrayList<String>();
		List<WebElement> cols = getColHeaders(visibleOnly);
		for (Iterator<WebElement> it = cols.iterator(); it.hasNext();) {
			WebElement we = (WebElement) it.next();
			colNames.add(getBrowser().div(0).in(we.getElementStub())
					.getAttribute("name"));
		}
		return colNames;
	}

	/**
	 * Get column index by header name
	 * 
	 */
	public int getColIdxByColName(String colName, boolean visibleOnly) {
		int colIdx = -1;
		List<WebElement> visibleHeaders = getColHeaders(visibleOnly);
		assertTrue("Visible headers count is 0.", visibleHeaders.size() > 0);
		for (int i = 0; i < visibleHeaders.size(); i++) {
			if (((DojoxHeaderCell) visibleHeaders.get(i)).getName().equals(
					colName)) {
				colIdx = i;
				break;
			}
		}
		if (colIdx == -1) {
			throw new AutomationException("Failed to find index of [" + colName
					+ "]. The current columns are " + getColNames(visibleOnly)
					+ ".");
		}
		return colIdx + this.checkboxColCount;
	}

	public boolean isActiveIconFoundInCell(WebElement cell) {
		ElementStub activeES = getBrowser().span("activeIcon sprite").in(
				cell.getElementStub());
		if (activeES.exists()) {
			return true;
		} else {
			ElementStub inactiveES = getBrowser().span("inactiveIcon sprite")
					.in(cell.getElementStub());
			if (inactiveES.exists()) {
				return false;
			} else {
				throw new AutomationException(
						"There is no active/inactive icon in this cell " + cell);
			}
		}
	}

	/**
	 * Get the leading checkbox by the specified cell with the cell text and the
	 * column index
	 * 
	 * @param cellText
	 * @param colIdx
	 * @return
	 */
	public Checkbox findLeadingCheckboxByCellText(final String cellText,
			final int colIdx) {

		Row row = findRowBy(new RowTestable() {
			@Override
			public boolean test(ElementStub rowES, int rowIdx) {
				return new Row(rowES).visibleCell(colIdx).getText()
						.equals(cellText);
			}
		});

		if (row != null && row.exists()) {
			Checkbox cb = (Checkbox) byIndex(0, Checkbox.class, row);
			if (cb.exists()) {
				return cb;
			} else {
				throw new ObjectNotFoundException(
						"Faile to find a checkbox in the row with the cell text ["
								+ cellText + "].");
			}
		} else {
			throw new ObjectNotFoundException(
					"Failed to find a row with the cell text [" + cellText
							+ "]");
		}

	}

	public WebElement findCell(String keyword, String columnName) {
		return findRowByText(keyword).visibleCell(columnName);
	}

	/**
	 * keyword - a regular expression
	 */
	public WebElement findCellByText(String keyword) {
		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				if (!(keyword.startsWith("/") && keyword.endsWith("/"))) {
					keyword = WBStringUtils.toRegExp(keyword);
				}
				WebElement we = row.visibleCellByText(keyword);
				if (we.exists()) {
					return we;
				}
			}
			scrollToRowNum += rowsPerPage;
		}
		throw new ObjectNotFoundException(
				"Can not find a cell with the keyword [" + keyword + "]");
	}

	public Row findRowByTextInColumn(String cellText, String columnName) {
		int colIndex = getColIdxByColName(columnName, true);
		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				WebElement cellWE = row.visibleCell(colIndex);
				if (cellText.equals(cellWE.getText())) {
					return row;
				}
			}
			scrollToRowNum += rowsPerPage;
		}

		throw new ObjectNotFoundException("Can not find a row with ["
				+ cellText + "] in column [" + columnName + "]");
	}

	public WebElement findCellByTextInColumn(String cellText, String columnName) {
		Row r = findRowByTextInColumn(cellText, columnName);
		return r.visibleCell(columnName);
	}

	public void assertCellContainsHTML(int rowIndex, String columnName,
			String expected) {
		assertTrue(findCell(rowIndex, columnName).getElementStub()
				.containsHTML(expected));
	}

	public void assertCellContainsHTML(String keyword, String columnName,
			String expected) {
		assertTrue("The column [" + columnName + "] for the row with keyword ["
				+ keyword + "] does not contain the expected HTML [" + expected
				+ "]", findCell(keyword, columnName).getElementStub()
				.containsHTML(expected));
	}

	public void assertCell(String keyword, String columnName, String expected) {
		assertEquals(expected, findCell(keyword, columnName).getText());
	}

	public void assertCellContains(String keyword, String columnName,
			String... expected) {
		final String cellText = findCell(keyword, columnName).getText();
		for (String str : expected) {
			assertTrue(cellText.contains(str));
		}
	}

	public void assertCells(String keyword, String[] columnNames,
			String[] expectedTexts) {
		Row r = findRowByText(keyword);
		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			r.assertCellText(columnName, expectedTexts[i]);
		}
	}

	public void assertCell(int rowIndex, String columnName, String expected) {
		assertEquals(expected, findCell(rowIndex, columnName).getText());
	}

	public void assertCellIgnoreCase(int rowIndex, String columnName,
			String expected) {
		assertEquals(expected.toLowerCase(), findCell(rowIndex, columnName)
				.getText().toLowerCase());
	}

	public void assertColData(String colName, List<String> expected) {
		List<String> colData = getColData(colName);
		assertEquals(expected.toString(), colData.toString());
	}

	public void assertColData(String colName, String[] expected) {
		assertColData(colName, Arrays.asList(expected));
	}

	public void assertColDataContains(String colName, List<String> expected) {
		List<String> colData = getColData(colName);
		Function<List<String>, List<String>> f = (o) -> {
			List<String> newList = new ArrayList<String>();
			o.stream().map(String::trim).sorted()
					.forEach((s) -> newList.add(s));
			return newList;
		};
		colData = f.apply(colData);
		expected = f.apply(expected);
		assertTrue("Column data [" + colData + "], Expected data [" + expected
				+ "]", colData.containsAll(expected));
	}

	public void assertColDataAllIs(String colName, String expected) {
		List<String> colData = getColData(colName);
		boolean matched = colData.stream().allMatch((s) -> s.equals(expected));
		assertTrue(matched);
	}

	public WebElement findCell(int rowIndex, String columnName) {
		int colIndex = getColIdxByColName(columnName, true);
		return visibleCellAt(rowIndex, colIndex);
	}

	public EvoGridFilterTextbox showFilter() {
		((Button) byLabel("Filter", Button.class, getToolbar())).click();
		return (EvoGridFilterTextbox) byIndex(0, EvoGridFilterTextbox.class,
				getToolbar());
	}

	public void resetFilter() {
		EvoGridFilterTextbox f = (EvoGridFilterTextbox) byIndex(0,
				EvoGridFilterTextbox.class, getToolbar());
		f.resetFilter();
	}

	public Row findRowByIndex(final int rowIndex) {
		if (rowIndex > rowCount() - 1) {
			throw new AutomationException("The row index [" + rowIndex
					+ "] is out of range [max:" + (rowCount() - 1) + "].");
		}
		List<ElementStub> esRows = rowTable().collectSimilar();
		if (rowIndex < esRows.size()) {
			Row row = new Row(esRows.get(rowIndex));
			row.setRowIndex(rowIndex);
			return row;
		} else {
			scrollToRow(rowIndex);
			List<ElementStub> esRows2 = rowTable().collectSimilar();
			int rowsPerPage = rowsPerPage();
			Row row = new Row(esRows2.get(rowsPerPage
					+ (rowIndex % rowsPerPage)));
			row.setRowIndex(rowIndex);
			return row;
		}
	}

	public Row findRowByText(final String text) {
		Row row = null;

		row = findRowBy(new RowTestable() {
			@Override
			public boolean test(ElementStub es, int rowIdx) {
				if (text.startsWith("/") && text.endsWith("/")) {
					String regex = text.substring(1, text.length() - 1);
					return es.getText().matches(regex);
				} else {
					return es.getText().contains(text);
				}
			}
		});

		if (row == null) {
			throw new ObjectNotFoundException(
					"Cannot find the row by the text [" + text
							+ "] in the table. Table data [" + getText() + "] ");
		}

		return row;
	}

	public List<Row> findMultiRowsByText(final String text, final int colIndex) {
		List<Row> multiRows = new ArrayList<Row>();
		List<Row> allRows = getAllRows();
		for (Row r : allRows) {
			if (r.visibleCell(colIndex).getText().equals(text)) {
				multiRows.add(r);
			}
		}

		return multiRows;
	}

	public List<Row> findMultiRowsByPattern(final String patternText) {
		List<Row> multiRows = new ArrayList<Row>();
		final String newText = patternText.replace("/", "");
		multiRows = findMultiRowsBy(new RowTestable() {
			@Override
			public boolean test(ElementStub es, int rowIdx) {
				return es.getText().matches(newText);
			}
		});

		if (multiRows == null) {
			throw new ObjectNotFoundException(
					"Cannot find the row by the pattern text [" + patternText
							+ "] in the table. Table data [" + getText() + "] ");
		}

		return multiRows;
	}

	protected ElementStub div_DojoxGridMasterHeader() {
		return getBrowser().div("dojoxGridMasterHeader").in(getElementStub());
	}

	protected ElementStub div_DojoxGridMasterView() {
		return getBrowser().div("dojoxGridMasterView").in(getElementStub());
	}

	protected ElementStub headTable() {
		return getBrowser().table("dojoxGridRowTable").in(
				div_DojoxGridMasterHeader());
	}

	protected ElementStub rowTable() {
		ElementStub masterView = div_DojoxGridMasterView();
		ElementStub rowTable = getBrowser().table("/dojoxGridRowTable.*/").in(
				masterView);
		return rowTable;
	}

	/**
	 * This method gets the total count from a text above the grid.
	 * 
	 * @return
	 */
	public int totalCountFromStatusMsg() {
		String countStr = null;
		if (WaterBearConfiguration.instance.getDojoVersion().equals("1.8.2")) {
			countStr = getBrowser().bold(0)
					.in(getBrowser().div("statusMsgContainer")).getText();
		} else {
			countStr = getBrowser().strong(0)
					.in(getBrowser().div("statusMsgContainer")).getText();
		}
		return Integer.parseInt(countStr);
	}

	/**
	 * This method returns the total row count by counting the real row elements
	 * in the grid and performing paging actions if necessary. So this method
	 * could be slow.
	 * 
	 * If your grid cannot use rowCount(),i.e there is "total count" above your
	 * grid, you have to use this method.
	 * 
	 * @return
	 */
	public int rowCount2() {
		return getAllRows().size();
	}

	public void assertEveryRowContains(final String text) {
		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		int counter = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				row.setRowIndex(counter++);
				assertTrue("The row [" + rowES.parentNode().getText()
						+ "] does not contain [" + text + "].", rowES
						.parentNode().containsText(text));
			}
			scrollToRowNum += rowsPerPage;
		}
	}

	public boolean containsText(String text) {
		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			if (getGridDataText().contains(text)) {
				return true;
			}
			scrollToRowNum += rowsPerPage;
		}
		return false;
	}

	/**
	 * 
	 * @param colName
	 * @return
	 */
	public List<String> getColData(String colName) {
		List<String> colValueList = new ArrayList<String>();
		int colIdx = getColIdxByColName(colName, true);

		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		int counter = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				row.setRowIndex(counter++);
				colValueList.add(row.visibleCell(colIdx).getText().trim());
			}
			scrollToRowNum += rowsPerPage;
		}

		return colValueList;
	}

	protected Row findRowBy(RowTestable rt) {
		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		int counter = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				if (rt.test(rowES, counter)) {
					row.setRowIndex(counter++);
					return row;
				}
				counter++;
			}
			scrollToRowNum += rowsPerPage;
		}

		return null;
	}

	protected List<Row> findMultiRowsBy(RowTestable rt) {
		List<Row> multiRows = new ArrayList<Row>();
		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		int counter = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				if (rt.test(rowES, counter)) {
					row.setRowIndex(counter++);
					multiRows.add(row);
				}
				counter++;
			}
			scrollToRowNum += rowsPerPage;
		}

		return multiRows;
	}

	protected String getGridDataText() {
		ElementStub scrollBoxES = getBrowser().div("dojoxGridScrollbox").in(es);
		return scrollBoxES.getText();
	}

	public List<Row> getAllRows() {
		List<Row> allRows = new ArrayList<Row>();

		int totalCount = rowCount();
		int rowsPerPage = rowsPerPage();
		int scrollToRowNum = 0;
		int counter = 0;
		while (scrollToRowNum < totalCount) {
			scrollToRow(scrollToRowNum);
			List<ElementStub> esRows = rowTable().collectSimilar();
			for (int i = (esRows.size() > rowsPerPage ? rowsPerPage : 0); i < esRows
					.size(); i++) {
				ElementStub rowES = esRows.get(i);
				Row row = new Row(rowES);
				row.setRowIndex(counter++);
				allRows.add(row);
			}
			scrollToRowNum += rowsPerPage;
		}

		return allRows;
	}

	public String getText() {
		return es.getText();
	}

	public int rowNum(final WebElement elem) {
		Row row = findRowBy(new RowTestable() {
			@Override
			public boolean test(ElementStub es, int rowIdx) {
				return es.getText().equals(elem.getText());
			}
		});

		return row.getRowIndex();
	}

	public int getRowNumByCellText(String cellText) {
		WebElement we = findRowByText(cellText);
		return rowNum(we);
	}

	/**
	 * 
	 * @param elem
	 *            Should be a cell element. Row element does not work.
	 * @return
	 */
	public EvoMenu popupEvoMenuOn(WebElement elem) {
		int count = 3;
		EvoMenu cm = null;
		while (cm == null && count > 0) {
			elem.rightClick();
			getBrowser().waitFor(500);
			cm = findActiveEvoMenu();
			count = count - 1;
		}
		if (cm == null) {
			throw new AutomationException(
					"Failed to popup the contextual menu for " + elem);
		}
		return cm;
	}

	protected Menu popupMenuOn(WebElement elem) {
		int count = 3;
		Menu cm = null;
		while (cm == null && count > 0) {
			elem.rightClick();
			getBrowser().waitFor(500);
			cm = findActiveMenu();
			count = count - 1;
		}
		if (cm == null) {
			throw new AutomationException(
					"Failed to popup the contextual menu for " + elem);
		}
		return cm;
	}

	public EvoMenu popupEvoMenuOn(int rowIndex) {
		Row r = findRowByIndex(rowIndex);
		return r.popupEvoMenu();
	}

	public Menu popupMenuOn(int rowIndex) {
		Row r = findRowByIndex(rowIndex);
		return r.popupMenu();
	}

	protected void multiSelect(String[] cellTexts) {
		for (int i = 0; i < cellTexts.length; i++) {
			String cellText = cellTexts[i];
			selectRow(cellText, COMBO_KEY_CTRL);
		}
	}

	protected void multiSelect(int... rowIndexes) {
		for (int i = 0; i < rowIndexes.length; i++) {
			int cellText = rowIndexes[i];
			selectRow(cellText, COMBO_KEY_CTRL);
		}
	}

	public void selectRow(String keyword, String comboKey) {
		String regPattern = "^/.*/$";
		List<Row> rows = new ArrayList<Row>();
		if (keyword.matches(regPattern)) {
			rows = findMultiRowsByPattern(keyword);
		} else {
			rows.add(findRowByText(keyword));
		}
		for (int i = 0; i < rows.size(); i++) {
			WebElement row = rows.get(i);
			boolean alreadySelected = row.getElementStub().parentNode()
					.fetch("className").contains("dojoxGridRowSelected");
			if (!alreadySelected) {
				if (comboKey == null) {
					row.click();

				} else {
					row.clickWithCombo(comboKey);
				}
			}
		}
	}

	public void selectRow(int rowIndex, String comboKey) {
		WebElement row = findRowByIndex(rowIndex);
		boolean alreadySelected = row.getElementStub().parentNode()
				.fetch("className").contains("dojoxGridRowSelected");
		if (!alreadySelected) {
			if (comboKey == null) {
				row.click();
			} else {
				row.clickWithCombo(comboKey);
			}
		}
	}

	/**
	 * Verify the data for the specified row with specified column names and
	 * values.
	 * 
	 * @param rowIndex
	 *            The row index
	 * @param args
	 *            [col1Name,col1Value,col2Name,col2Value, ...]
	 */
	public void assertRow(int rowIndex, String... args) {
		Row r = findRowByIndex(rowIndex);
		for (int i = 0; i < (args.length / 2); i += 2) {
			String columnName = args[i];
			String expectedColValue = args[i + 1];
			r.assertCellText(columnName, expectedColValue);
		}
	}

	/**
	 * Verify the data for the specified row with specified column names and
	 * values.
	 * 
	 * @param keyword
	 *            The keyword to find a row
	 * @param args
	 *            [col1Name,col1Value,col2Name,col2Value, ...]
	 */
	public void assertRow(String keyword, String... args) {
		Row r = findRowByText(keyword);
		for (int i = 0; i < (args.length / 2); i += 2) {
			String columnName = args[i];
			String expectedColValue = args[i + 1];
			r.assertCellText(columnName, expectedColValue);
		}
	}

	/**
	 * 
	 * @param keyword
	 *            A string which helps to identify the row. Normally it is a
	 *            value of one cell in the row.
	 * @param expectedData
	 *            This parameter could be NULL,String,Regular Expression String
	 *            or A class which implements IValidator. <br>
	 *            Examples: - String: "mydata" - Regular Expression:
	 *            "/.*mydata/" Note: NULL means that the data in the cell is not
	 *            intended to be verified
	 */
	public void assertRowData(String keyword, Object... expectedData) {
		Row row = findRowByText(keyword);
		row.assertRow(expectedData);
	}

	public ElementStub getToolbar() {
		return byIndex(0, EvoGridToolbar.class, es, Where.NEAR)
				.getElementStub();
	}

	/**
	 * 
	 * @param colName
	 * @param ascOrDesc
	 *            - ORDER_ASC or ORDER_DESC
	 */
	public void setColOrderAs(String colName, String ascOrDesc) {
		List<WebElement> cols = getColHeaders(true);
		boolean found = false;
		for (Iterator<WebElement> it = cols.iterator(); it.hasNext();) {
			DojoxHeaderCell c = (DojoxHeaderCell) it.next();

			if (c.getName().equals(colName)) {
				found = true;
				int maxCounter = 10;
				while (!c.isOrderAs(ascOrDesc) && maxCounter > 0) {
					/*
					 * The normal code is: we.click();
					 * 
					 * Sahi has a bug that <th> click event can not be triggered
					 * in IE8, so here is workaround.
					 */
					getBrowser().execute(c.getElementStub() + ".click();");
					maxCounter--;
				}
				if (maxCounter == 0) {
					throw new AutomationException(
							"Failed to find the ordering icon [" + ascOrDesc
									+ "] on the column [" + colName + "]");
				}
				break;
			}
		}
		if (!found) {
			throw new AutomationException("The column [" + colName
					+ "] was not found.");
		}
	}

	/**
	 * 
	 * @param exportMode
	 * @return
	 */
	public File exportToCSV() {
		getBrowser().span("dijitReset dijitInline dijitIcon exportIcon sprite")
				.in(getToolbar()).click();

		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() throws ExecutionException {
				return EXPECTED_LAST_DOWNLOAD_FILE_NAME.equals(getBrowser()
						.lastDownloadedFileName());
			}
		};
		getBrowser().waitFor(cond, 60000);
		assertEquals(EXPECTED_LAST_DOWNLOAD_FILE_NAME, getBrowser()
				.lastDownloadedFileName());

		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("guiauto_csv_export", ".tmp");
			getBrowser().saveDownloadedAs(tmpFile.getAbsolutePath());
		} catch (IOException e) {
			throw new AutomationException(e);
		}
		getBrowser().clearLastDownloadedFileName();
		assertNull(getBrowser().lastDownloadedFileName());
		return tmpFile;
	}

	/**
	 * This method only verify the total line count and column header line
	 * 
	 * @param f
	 * @return
	 */
	public String verifyExportedCSV(File f) {
		String colNameStr = "";
		Object[] colNames = getColNames(true).toArray();
		for (int i = 0; i < colNames.length; i++) {
			Object obj = colNames[i];
			colNameStr = colNameStr + obj.toString();
			if (i != colNames.length - 1) {
				colNameStr = colNameStr + ",";
			}
		}

		String fContent = readFile(f);
		String[] lines = fContent.split("\r\n");
		assertEquals(rowCount(), lines.length - 1);
		assertEquals(lines[0], colNameStr);
		return fContent;
	}

	private String readFile(File f) {
		Long filelength = f.length();
		byte[] fContent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(f);
			in.read(fContent);
			in.close();
		} catch (IOException e) {
			throw new AutomationException(e);
		}

		return new String(fContent);
	}

	/**
	 * 
	 * @param colName
	 * @param orderType
	 */
	public void verifyColDataOrder(String colName, String orderType) {
		final List<String> actualColData = getColData(colName);
		List<String> expectedColData = new ArrayList<String>(actualColData);

		final Comparator<String> comparator = new Comparator<String>() {
			public int compare(String o1, String o2) {
				int cmp = o1.toLowerCase().compareTo(o2.toLowerCase());
				return cmp;
			}
		};

		if (orderType.equals(DataGrid.ORDER_ASC)) {
			Collections.sort(expectedColData, comparator);
		} else if (orderType.equals(DataGrid.ORDER_DESC)) {
			Collections.sort(expectedColData,
					Collections.reverseOrder(comparator));
		}

		assertEquals(expectedColData.toString(), actualColData.toString());
	}

	public void setAllColumnsVisible() {
		getBrowser().div("customColsIcon").in(es).click();
		ElementStub popUpES = getColCustomizationPopup();

		String uncheckedColsRE = "dijitReset dijitMenuItem dijitCheckedMenuItem";
		List<ElementStub> uncheckedCols = getBrowser().row(uncheckedColsRE)
				.in(popUpES).collectSimilar();

		List<String> trIdsToBeChecked = new ArrayList<String>();
		for (Iterator<ElementStub> it = uncheckedCols.iterator(); it.hasNext();) {
			ElementStub colNameES = (ElementStub) it.next();
			String trId = colNameES.getAttribute("id");
			trIdsToBeChecked.add(trId);
		}

		for (Iterator<String> it = trIdsToBeChecked.iterator(); it.hasNext();) {
			String id = (String) it.next();
			getBrowser().byId(id).click();
		}
	}

	public void setVisiableCols(List<String> colNames) {
		getBrowser().div("customColsIcon").in(es).click();
		ElementStub popUpES = getColCustomizationPopup();

		// check the specified columns
		for (int i = 0; i < colNames.size(); i++) {
			String colName = colNames.get(i);
			ElementStub cellES = getBrowser().cell(colName).in(popUpES);
			if (!cellES.exists()) {
				throw new AutomationException(
						"The column name ["
								+ colName
								+ "] is not in the Column Customizaton Popup for the data grid ["
								+ es.getAttribute("id") + "]");
			} else {
				if (!cellES.parentNode().getAttribute("className")
						.contains("dijitCheckedMenuItemChecked")) {
					cellES.click();
				}
			}
		}
		// uncheck all other columns
		String checkedColsRE = "/dijitReset .* dijitCheckedMenuItemChecked dijitChecked/";
		if (WaterBearConfiguration.instance.getDojoVersion().equals("1.8.2")) {
			checkedColsRE = "/dijitReset .* dijitChecked/";
		}
		List<ElementStub> checkedCols = getBrowser().row(checkedColsRE)
				.in(popUpES).collectSimilar();
		List<String> trIdsToBeUnchecked = new ArrayList<String>();
		for (Iterator<ElementStub> it = checkedCols.iterator(); it.hasNext();) {
			ElementStub colNameES = (ElementStub) it.next();
			String trId = colNameES.getAttribute("id");
			String colName = getBrowser().cell(trId + "_text").in(colNameES)
					.getText();
			if (!colNames.contains(colName)) {
				trIdsToBeUnchecked.add(trId);
			}
		}

		for (Iterator<String> it = trIdsToBeUnchecked.iterator(); it.hasNext();) {
			String id = (String) it.next();
			getBrowser().byId(id).click();
		}
	}

	public void restoreDefaultView() {
		getBrowser().div("customColsIcon").in(es).click();
		ElementStub popUpES = getColCustomizationPopup();
		getBrowser().cell("         Restore Default View     ").in(popUpES)
				.click();
	}

	private ElementStub getColCustomizationPopup() {
		WebElement popup = webElem(
				"{dijitpopupparent:'" + es.getAttribute("id") + "'}", ET.DIV);
		if (popup.exists()) {
			return popup.getElementStub();
		} else {
			throw new ObjectNotFoundException(
					"Cannot locate the Column Customizaton Popup for the data grid ["
							+ es.getAttribute("id") + "]");
		}
	}

	public EvoMenu popupEvoMenuOn(String cellText) {
		return popupEvoMenuOn(new String[] { cellText });
	}

	public EvoMenu popupEvoMenuOn(String[] cellTexts) {
		int len = cellTexts.length;
		EvoMenu p = null;
		WebElement cell = null;
		if (len <= 0) {
			throw new AutomationException("cellTexts length is zero!");
		} else if (len == 1) {
			cell = findCellByText(cellTexts[0]);
		} else {
			multiSelect(cellTexts);
			cell = findCellByText(cellTexts[cellTexts.length - 1]);
			assertTrue(cell + " does not exist!", cell.exists());
		}
		p = popupEvoMenuOn(cell);
		return p;
	}

	public EvoMenu popupEvoMenuOn(int... rowIndexes) {
		int len = rowIndexes.length;
		EvoMenu p = null;
		WebElement cell = null;
		if (len <= 0) {
			throw new AutomationException("cellTexts length is zero!");
		} else if (len == 1) {
			cell = findRowByIndex(rowIndexes[0]).visibleCell(0);
		} else {
			multiSelect(rowIndexes);
			cell = findRowByIndex(rowIndexes[rowIndexes.length - 1])
					.visibleCell(0);
			assertTrue(cell + " does not exist!", cell.exists());
		}
		p = popupEvoMenuOn(cell);
		return p;
	}

	public Menu popupMenuOn(String cellText) {
		return popupMenuOn(new String[] { cellText });
	}

	public Menu popupMenuOn(String[] cellTexts) {
		int len = cellTexts.length;
		Menu p = null;
		WebElement cell = null;
		if (len <= 0) {
			throw new AutomationException("cellTexts length is zero!");
		} else if (len == 1) {
			cell = findCellByText(cellTexts[0]);
		} else {
			multiSelect(cellTexts);
			cell = findCellByText(cellTexts[cellTexts.length - 1]);
		}
		p = popupMenuOn(cell);
		return p;
	}

	/**
	 * By default, assume DataGrid is not in an iframe. But if iframeId is set,
	 * it means the DataGrid is in an iframe, then the iframe object needs to be
	 * the top object in the returned expression.
	 * 
	 */
	protected String dijitRegistryByIdExpr() {
		String expr = "dijit.registry.byId('" + getDojoWidgetId() + "')";
		if (iframeId != null) {
			expr = "document.getElementById('" + iframeId + "').contentWindow."
					+ expr;
		}
		return expr;
	}

	public int rowsPerPage() {
		String rowsPerPage = getBrowser().fetch(
				dijitRegistryByIdExpr() + ".scroller.rowsPerPage");
		return Integer.parseInt(rowsPerPage);
	}

	public int pageCount() {
		String rowsPerPage = getBrowser().fetch(
				dijitRegistryByIdExpr() + ".scroller.pageCount");
		return Integer.parseInt(rowsPerPage);
	}

	public int rowCount() {
		String rowCount = getBrowser().fetch(
				dijitRegistryByIdExpr() + ".scroller.rowCount");
		return Integer.parseInt(rowCount);
	}

	public void scrollToRow(int rowIndex) {
		getBrowser().execute(
				dijitRegistryByIdExpr() + ".scrollToRow(" + rowIndex + ");");
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub

	}

	public void assertRowCount_EQ(int expected) {
		assertEquals(expected, rowCount());
	}

	public void assertNotEmpty() {
		assertRowCount_GT(0);
	}

	public void assertRowCount_GT(int expected) {
		final int rowCount = rowCount();
		assertTrue("More than " + expected + " rows was expected, but it is "
				+ rowCount + ".", rowCount > expected);
	}

	public void assertRowCount_EQ_GT(int expected) {
		final int rowCount = rowCount();
		assertTrue("Equal or more than " + expected
				+ " rows was expected, but it is " + rowCount + ".",
				rowCount >= expected);
	}

	public void assertTextNotExists(String... expected) {
		for (int i = 0; i < expected.length; ++i) {
			assertFalse("[" + expected[i] + "] was found in grid.",
					containsText(expected[i]));
		}
	}

	public void assertTextExists(String... expected) {
		for (int i = 0; i < expected.length; ++i) {
			assertTrue("[" + expected[i] + "] was not found in grid.",
					containsText(expected[i]));
		}
	}

	public void assertTextExists(List<String> expected) {
		for (Iterator<String> it = expected.iterator(); it.hasNext();) {
			String text = (String) it.next();
			assertTrue("[" + text + "] was not found in grid.",
					containsText(text));
		}
	}

	public interface RowTestable {
		public boolean test(ElementStub es, int rowIdx);
	}

	/**
	 * 
	 * Representing a TR element
	 * 
	 * @author shenrui@cn.ibm.com
	 * 
	 */
	public class Subrow extends WebElement {
		private ElementStub es;

		public Subrow(ElementStub es) {
			this.es = es;
		}

		public WebElement cell(int colIdx) {
			return new WebElement(getBrowser().cell(colIdx).in(es));
		}
	}

	/**
	 * 
	 * Row is a table with className "dojoxGridRowTable"
	 * 
	 */
	public class Row extends WebElement {
		private int rowIndex;

		public Row(ElementStub es) {
			this.es = es;
		}

		public EvoMenu popupEvoMenu() {
			return popupEvoMenuOn(visibleCell(0));
		}

		public Menu popupMenu() {
			return popupMenuOn(visibleCell(0));
		}

		public int getRowIndex() {
			return rowIndex;
		}

		public void setRowIndex(int rowIndex) {
			this.rowIndex = rowIndex;
		}

		public Subrow findSubrowByText(String keyword) {
			ElementStub trES = getBrowser().row("/.*" + keyword + ".*/").in(es);
			if (trES.exists()) {
				return new Subrow(trES);
			} else {
				throw new ObjectNotFoundException(
						"No Subrow found for the keyword [" + keyword + "].");
			}
		}

		public String getRowStr(boolean visibleOnly) {
			String rowStr = "";
			int count = Integer.parseInt(es
					.fetch("getElementsByTagName('td').length"));

			for (int i = 0; i < count; i++) {
				ElementStub cell = getBrowser().cell(es, 0, i);
				if (visibleOnly) {
					if (cell.isVisible()) {
						rowStr = rowStr + cell.getText() + ",";
					}
				} else {
					rowStr = rowStr + cell.getText() + ",";
				}
			}
			return removeTrailingCommas(rowStr);
		}

		private String removeTrailingCommas(String str) {
			while (str.endsWith(",")) {
				str = str.substring(0, str.length() - 1);
			}
			return str;
		}

		public int cellsCount() {
			final int cellsCount = getBrowser()
					.cell("/dojoxGridCell.*|gridxCell.*/").in(es)
					.countSimilar();
			return cellsCount;
		}

		public WebElement visibleCellByText(String text) {
			return new WebElement(getBrowser().cell(text).in(es));
		}

		public WebElement visibleCell(int idx) {
			int count = cellsCount();

			int counter = -1;
			for (int i = 0; i < count; i++) {
				ElementStub cell = getBrowser().cell(es, 0, i);
				if (!cell.style("display").equals("none")) {
					counter = counter + 1;
				} else {
					continue;
				}
				if (idx == counter) {
					return new WebElement(cell);
				}
			}

			throw new ObjectNotFoundException("Failed to locate the cell at ["
					+ idx + "]");
		}

		public boolean isSelected() {
			return es.parentNode().fetch("className")
					.contains("dojoxGridRowSelected");
		}

		public boolean isActionHighlight() {
			return es.fetch("className").contains("actionHighlight");
		}

		public void assertActionHighlight(boolean expected) {
			assertEquals(expected, isActionHighlight());
		}

		public void assertSelected(boolean expected) {
			assertEquals(expected, isSelected());
		}

		public void assertCellContainsHTML(String columnName,
				String... expectedTexts) {
			ElementStub cellES = visibleCell(columnName).getElementStub();
			for (String expectedText : expectedTexts) {
				assertTrue("The cell in the column [" + columnName
						+ "] does not contain HTML [" + expectedText + "]",
						cellES.containsHTML(expectedText));
			}
		}

		public void assertCellText(int idx, String expected) {
			assertEquals(expected, visibleCell(idx).getText());
		}

		public void assertCellText(String columnName, String expected) {
			assertEquals(expected, visibleCell(columnName).getText());
		}

		public void assertCellTextContains(String columnName,
				String... expected) {
			final String text = visibleCell(columnName).getText();
			for (String str : expected) {
				assertTrue("The cell text [" + text
						+ "] does not contain the expected string [" + str
						+ "].", text.contains(str));
			}
		}

		public WebElement visibleCell(String columnName) {
			return visibleCell(getColIdxByColName(columnName, true));
		}

		public void assertRow(Object... expectedData) {
			for (int i = 0; i < expectedData.length; i++) {
				Object expectedValue = expectedData[i];
				if (expectedValue == null) {
					log.warn("The expected data is NULL, but this normally indicates that NO NEED TO VERIFY THIS.");
				} else if (expectedValue instanceof IValidator) {
					((IValidator) expectedValue).validate(visibleCell(i));
				} else {
					String expectedStrValue = expectedValue.toString();
					String actualCellValue = visibleCell(i).getText().trim();
					if (expectedStrValue.startsWith("/")
							&& expectedStrValue.endsWith("/")) {
						String re = expectedStrValue.substring(1,
								expectedStrValue.length() - 1);
						boolean matched = actualCellValue.matches(re);
						if (!matched) {
							throw new AutomationException(
									"The expected cell text is ["
											+ expectedStrValue
											+ "] but it was ["
											+ actualCellValue + "]");
						}
					} else {
						assertEquals("expected:<" + expectedStrValue
								+ "> but was:<" + actualCellValue + ">.",
								expectedStrValue, actualCellValue);
					}
				}
			}
		}
	}

	public Button getToolbarBtn(String btnLabel) {
		return byLabel(btnLabel, Button.class, getToolbar());
	}

	/**
	 * Wait till the expected text appears in the data grid or timed out.
	 * 
	 * @param expected
	 * @param timeout
	 *            unit:millisecond.
	 */
	public void waitForTextAppear(final String expected, int timeout) {
		BrowserUtil.waitForCond(new WaitForCondition() {
			@Override
			public boolean test() {
				return containsText(expected);
			}
		}, timeout);
	}

	public void waitForTextsAppear(int timeout, final String... expected) {
		BrowserUtil.waitForCond(new WaitForCondition() {
			@Override
			public boolean test() {
				for (int i = 0; i < expected.length; i++) {
					String expectedText = expected[i];
					if (!containsText(expectedText)) {
						return false;
					}
				}
				return true;
			}
		}, timeout);
	}

	public void waitForCellTextAppear(final WebElement cell,
			final String expected) {
		BrowserUtil.waitForCond(new WaitForCondition() {
			@Override
			public boolean test() {
				return cell.getText().equals(expected);
			}
		}, 30000);
	}

	/**
	 * With default timeout 60000 seconds
	 * 
	 * @param expected
	 */
	public void waitForTextAppear(final String... expected) {
		waitForTextsAppear(60000, expected);
	}

	/**
	 * With default timeout 60000 seconds
	 * 
	 * @param expected
	 */
	public void waitForTextDisappear(final String expected) {
		waitForTextDisappear(expected, 60000);
	}

	public void waitForTextDisappear(final String expected, int timeout) {
		BrowserUtil.waitForCond(new WaitForCondition() {
			@Override
			public boolean test() {
				return !containsText(expected);
			}
		}, timeout);
	}

	public class DojoxHeaderCell extends WebElement {
		public DojoxHeaderCell(ElementStub es) {
			super(es);
		}

		public String getName() {
			return getBrowser().div(0).in(es).getAttribute("name");
		}

		public boolean isOrderAs(String ascOrDesc) {
			return es.getAttribute("aria-sort").equals(ascOrDesc);
		}
	}
}
