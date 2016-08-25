package org.waterbear.core.widgets.evo;

import static org.waterbear.core.widgets.WidgetFinder.byIndex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.IntfExpando;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.dijit.Expando;
import org.waterbear.core.widgets.dijit.TreeGrid;
import org.waterbear.core.widgets.dijit.DataGrid.Row;
import org.waterbear.core.widgets.html.GridxTreeExpando;

public class EvoGrid extends EvoGPTreeGrid {

	public EvoGrid(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasNoChildren(Row r) {
		GridxTreeExpando gte = (GridxTreeExpando) getExpandoInRow(r
				.getElementStub());
		return gte.hasNoChildren();
	}

	@Override
	public boolean isParentRow(Row r) {
		String parentId = r.parentNode().getAttribute("parentid");
		return parentId.equals("");
	}

	protected String getGridDataText() {
		return div_DojoxGridMasterView().getText();
	}

	public int rowCount() {
		String rowCount = getBrowser().fetch(
				dijitRegistryByIdExpr() + ".view.visualCount");
		return Integer.parseInt(rowCount);
	}

	/**
	 * There is no the corresponding property in this grid, so return 40
	 * directly.
	 */
	public int rowsPerPage() {
		return 40;
	}

	public int pageCount() {
		return (int) Math.ceil(rowCount() / rowsPerPage()) + 1;
	}

	protected ElementStub div_DojoxGridMasterHeader() {
		return getBrowser().div("headerNode").in(getElementStub());
	}

	protected ElementStub div_DojoxGridMasterView() {
		return getBrowser().div("mainNode").in(getElementStub());
	}

	protected ElementStub headTable() {
		return getBrowser().table(0).in(div_DojoxGridMasterHeader());
	}

	/**
	 * @return table column headers
	 * @param displayedOnly
	 *            Set it true to get the displayed-only header cells
	 */
	@Override
	public List<WebElement> getColHeaders(boolean visibleOnly) {
		List<WebElement> colHeaders = new ArrayList<WebElement>();
		List<ElementStub> esHeaders = getBrowser()
				.cell("/" + getDojoWidgetId() + "-\\d+/").in(headTable())
				.collectSimilar();
		for (ElementStub es : esHeaders) {
			if (!visibleOnly || es.isVisible()) {
				colHeaders.add(new GridxHeaderCell(es));
			}
		}
		return colHeaders;
	}

	protected ElementStub rowTable() {
		ElementStub masterView = div_DojoxGridMasterView();
		ElementStub rowTable = getBrowser().table("/gridxRowTable.*/").in(
				masterView);
		return rowTable;
	}

	public void scrollToRow(int rowIndex) {
		getBrowser().execute(
				dijitRegistryByIdExpr() + ".vScroller.scrollToRow(" + rowIndex
						+ ");");
	}

	public class GridxHeaderCell extends DojoxHeaderCell {
		public GridxHeaderCell(ElementStub es) {
			super(es);
		}

		@Override
		public String getName() {
			return getBrowser().div("gridxSortNode").in(es).getText();
		}
	}

	@Override
	public Row findRowByIndex(final int rowIndex) {
		if (rowIndex > rowCount() - 1) {
			throw new AutomationException("The row index [" + rowIndex
					+ "] is out of range [max:" + (rowCount() - 1) + "].");
		}
		List<ElementStub> esRows = rowTable().collectSimilar();
		final int size = esRows.size();
		if (rowIndex < size) {
			Row row = new Row(esRows.get(rowIndex));
			row.setRowIndex(rowIndex);
			return row;
		} else {
			throw new AutomationException(
					"Paging is not supported. Row index [" + rowIndex
							+ "], size [" + size + "], rowCount [" + rowCount()
							+ "]");
		}
	}

	private Row findRowByRowIndex(final int rowIndex) {
		if (rowIndex > rowCount() - 1) {
			throw new AutomationException("The row index [" + rowIndex
					+ "] is out of range [max:" + (rowCount() - 1) + "].");
		}
		if (rowIndex >= pageCount()) {
			scrollToRow(rowIndex);
		}
		ElementStub rES = getBrowser().table(0).in(
				WidgetFinder.webElem("{rowindex:" + rowIndex + "}", ET.DIV,
						div_DojoxGridMasterView()).getElementStub());

		Row row = new Row(rES);
		row.setRowIndex(rowIndex);
		return row;
	}

	public IntfExpando getExpandoInRow(ElementStub rowES) {
		WebElement we = WidgetFinder.webElem("/gridxTreeExpandoCell.*/",
				ET.DIV, rowES);
		return new GridxTreeExpando(we.getElementStub());
	}
}
