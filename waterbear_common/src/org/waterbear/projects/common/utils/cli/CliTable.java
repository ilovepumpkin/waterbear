package org.waterbear.projects.common.utils.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.ObjectNotFoundException;

/**
 * This class is used to retrieve data from the CLI command output, especially
 * 'ls' commands.
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class CliTable {
	private String[][] data;
	private String[] headers;

	public CliTable(String[] headers, String[][] data) {
		this.headers = headers;
		this.data = data;
	}

	CliTable(String[] headers, Collection<String[]> dataC) {
		this.headers = headers;

		data = new String[dataC.size()][];
		int i = 0;
		Iterator<String[]> iDataC = dataC.iterator();
		while (iDataC.hasNext()) {
			String[] rowData = (String[]) iDataC.next();
			data[i] = rowData;
			i = i + 1;
		}
	}

	public CliTable(String cliOutput, String delim) {
		String[] temp = cliOutput.trim().split("\n");
		/*
		 * Form headers array
		 */
		String headersStr = temp[0];
		headers = headersStr.split(delim);

		/*
		 * Form data array
		 */
		data = new String[temp.length - 1][];

		for (int i = 1; i < temp.length; i++) {
			String line = temp[i];
			if (line.trim().equals("")) {
				continue;
			}
			String[] rowData = line.split(delim, headers.length);
			data[i - 1] = rowData;
		}

	}

	public List<CliRow> data() {
		List<CliRow> l = new ArrayList<CliRow>();
		for (String[] r : data) {
			l.add(new CliRow(r));
		}
		return l;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		CliTable other = (CliTable) obj;
		/*
		 * for (int i = 0; i < other.data.length; i++) { for (int j = 0; j <
		 * other.headers.length; j++){ System.out.println(other.data[i][j]); } }
		 */
		// header comparison
		/*
		 * for (int i = 0; i < other.headers.length; i++) { if
		 * (!this.headers[i].equals(other.headers[i])) { return false; } }
		 */

		// data comparison
		for (int i = 0; i < other.data.length; i++) {
			for (int j = 0; j < other.headers.length; j++)
				if (!this.data[i][j].equals(other.data[i][j])) {
					System.out.println("-->" + this.data[i][j] + "<-->"
							+ other.data[i][j] + "<--");
					return false;
				}
		}
		return true;
	}

	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.headers.length; i++) {
			sb.append(headers[i]);
		}
		return sb.toString().hashCode();
	}

	private void checkRowCount() {
		if (rowCount() <= 0) {
			throw new AutomationException("There is no data!");
		}
	}

	/**
	 * Return the data in a column matching with the specified column name.
	 * 
	 * @param colName
	 * @return
	 */
	public String[] getColData(String colName) {
		if (rowCount() == 0) {
			return new String[] {};
		}
		int colIndex = getColIndex(colName);
		if (colIndex == -1) {
			throw new AutomationException("The column [" + colName
					+ "] was not found.");
		}

		String[] colData = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			String colValue = data[i][colIndex];
			colData[i] = colValue;
		}
		return colData;
	}

	/**
	 * Return the data of specified column names.
	 * 
	 * @param colNames
	 * @return
	 */
	public String[][] getMultiColData(String... colNames) {
		String[][] colData = new String[data.length][colNames.length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < colNames.length; j++) {
				String colName = colNames[j];
				int colIndex = getColIndex(colName);
				if (colIndex == -1) {
					throw new AutomationException("The column [" + colName
							+ "] was not found.");
				}
				String colValue = data[i][colIndex];
				colData[i][j] = colValue;
			}
		}
		return colData;
	}

	/**
	 * Find the corresponding column index for the given column name
	 * 
	 * @param colName
	 * @return
	 */
	private int getColIndex(String colName) {
		for (int i = 0; i < headers.length; i++) {
			String headerName = headers[i];
			if (colName.equals(headerName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Return the value of a cell by the first column value and the specific
	 * column name
	 * 
	 * @param colName
	 * @param firstColValue
	 * @return
	 */
	public String cell(String colName, String firstColValue) {
		Iterator<CliRow> iter = this.data().iterator();
		while (iter.hasNext()) {
			CliRow r = iter.next();
			if (r.cell(0).equals(firstColValue)) {
				return r.cell(colName);
			}
		}
		throw new ObjectNotFoundException(
				"Can not find the row whose first column value is ["
						+ firstColValue + "]");
	}

	/**
	 * Return the value of a cell by the column value and colIndex and the
	 * specific column name
	 * 
	 * @param colName
	 * @param firstColValue
	 * @return
	 */
	public String cell(String colName, String ColValue, int ColIndex) {
		String[] ColData = getColData(headers[ColIndex]);
		int rowIdx = 0;
		for (int i = 0; i < ColData.length; i++) {
			String value = ColData[i];
			if (value.equals(ColValue)) {
				rowIdx = i;
				break;
			}
		}

		return getColData(colName)[rowIdx];
	}

	public CliRow row(int rowIdx) {
		validateRowIdx(rowIdx);
		return new CliRow(data[rowIdx]);
	}

	/**
	 * 
	 * @param rowIdx
	 *            Starting from zero and excluding the table header line, which
	 *            means the row index for the first line of data is 0.
	 * @param colIdx
	 *            Starting from zero
	 * @return
	 */
	public String cell(int rowIdx, int colIdx) {
		validateRowIdx(rowIdx);
		validateColIdx(colIdx);
		return data[rowIdx][colIdx].trim();
	}

	private void validateRowIdx(int rowIdx) {
		if (rowIdx > data.length - 1 || rowIdx < 0) {
			throw new AutomationException(
					"The row index passed in is invalid - the valid range is [0,"
							+ (data.length - 1) + "]");
		}
	}

	private void validateColIdx(int colIdx) {
		if (colIdx > headers.length - 1 || colIdx < 0) {
			throw new AutomationException(
					"The column index passed in is invalid [" + colIdx
							+ "] - the valid range is [0,"
							+ (headers.length - 1) + "]");
		}
	}

	/**
	 * Return the rowIndex matched with the defined columns.
	 * 
	 * @param Multidata
	 *            An array to get the unique row in CliTable example:
	 *            {{col1Name,col1Value},{col2Name,col2Value}}
	 * 
	 * @return
	 */
	public int getUniqueRowIndex(String[][] Multidata) {
		Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
		for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
			dataMap.put(rowIndex, data[rowIndex]);
		}
		for (String[] colData : Multidata) {
			String colName = colData[0];
			String colValue = colData[1];
			String[] filterColData = getColData(colName);
			for (int i = 0; i < filterColData.length; i++) {
				if (!filterColData[i].equals(colValue)) {
					dataMap.remove(i);
				}
			}
		}
		if (dataMap.size() != 1) {
			throw new AutomationException("The expect result is not unique!!");
		}
		return dataMap.keySet().iterator().next();
	}

	/**
	 * 
	 * @param rowIdx
	 *            Starting from zero and excluding the table header line, which
	 *            means the row index for the first line of data is 0.
	 * @param colName
	 *            A column name.
	 * @return
	 */
	public String cell(int rowIdx, String colName) {
		validateRowIdx(rowIdx);
		return row(rowIdx).cell(colName);
	}

	/**
	 * Return the data matched with the defined columns
	 * 
	 * @param args
	 *            an varient arguments to define the filter criteria. an
	 *            example:
	 *            filterByCols(<col1Name>,<col1Value>,<col2Name>,<col2Value>)
	 * @return
	 */
	public CliTable filterByCols(String... args) {
		int argLen = args.length;
		if (argLen % 2 != 0) {
			throw new AutomationException(
					"The number of the paramters should be a even number. The format is: <col1Name>,<col1Value>,<col2Name>,<col2Value>,...");
		}

		String[] colNames = new String[argLen / 2];
		String[] colValues = new String[argLen / 2];

		for (int i = 0; i < argLen; i = i + 2) {
			colNames[i / 2] = args[i];
			colValues[i / 2] = args[i + 1];
		}

		List<String[]> filteredData = new ArrayList<String[]>();

		String[][] colData = getMultiColData(colNames);
		for (int i = 0; i < colData.length; i++) {
			String[] tempData = colData[i];
			if (Arrays.asList(tempData).toString()
					.equals(Arrays.asList(colValues).toString())) {
				filteredData.add(data[i]);
			}
		}

		return new CliTable(headers, filteredData);
	}

	public int rowCount() {
		if (data != null) {
			return data.length;
		} else {
			return 0;
		}
	}

	public class CliRow {
		private String[] rData;

		public CliRow(String[] data) {
			this.rData = data;
		}

		public String cell(String colName) {
			int colIdx = getColIndex(colName);
			validateColIdx(colIdx);
			return rData[colIdx].trim();
		}

		public String cell(int colIdx) {
			return rData[colIdx].trim();
		}
	}

}
