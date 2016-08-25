package org.waterbear.projects.common.utils.cli;


public interface CliVerticalTable {
	public abstract CliVerticalTable subTab(int index);

	public abstract String property(String propertyName);

	public abstract void assertPropertyValue(String propertyName,
			String expected);

	/**
	 * Return the data of several row names.
	 * 
	 * @param propertyNames
	 * @return
	 */
	public abstract String[] properties(String[] propertyNames);

}