package org.waterbear.core.widgets.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.waterbear.core.exception.AutomationException;

public class WidgetRegistry {
	private HashMap<String, RegEntry> regMap = null;
	private static WidgetRegistry instance;

	private WidgetRegistry() {
		regMap = new HashMap<String, RegEntry>();
	}

	/**
	 * Register a new widget entry
	 * 
	 * @param classQName
	 * @param regEntry
	 */
	public void register(String classQName, RegEntry regEntry) {
		regMap.put(classQName, regEntry);
	}

	/**
	 * Extend the identifiers of an existing widget entry
	 * 
	 * @param classQName
	 * @param extendedIdentifiers
	 */
	public void extend(String classQName, String[] extendedIdentifiers) {
		if (!regMap.containsKey(classQName)) {
			throw new AutomationException("There is no [" + classQName
					+ "] in Widget Registory.");
		}
		RegEntry regEntry = regMap.get(classQName);
		regEntry.addIdentifiers(extendedIdentifiers);
		register(classQName, regEntry);
	}

	/**
	 * Check if a dojo widget class is already registered.
	 * 
	 * @param classQName
	 * @return
	 */
	public boolean exists(String classQName) {
		return regMap.containsKey(classQName);
	}

	public RegEntry getRegEntry(String classQName) {
		RegEntry entry = (RegEntry) regMap.get(classQName);

		if (entry == null) {
			throw new AutomationException(classQName
					+ " needs to be added into WidgetRegistry.");
		}

		return entry;
	}

	public RegEntry getRegEntryByWidgetIdPrefix(String widgetIdPrefix) {
		Set<String> iKeys = regMap.keySet();
		for (Iterator<String> it = iKeys.iterator(); it.hasNext();) {
			Object key = (Object) it.next();
			RegEntry r = regMap.get(key);
			List<String> ids = r.getIdentifiers();
			if (ids.contains(widgetIdPrefix)) {
				return r;
			}
		}
		throw new AutomationException("The widget id prefix [" + widgetIdPrefix
				+ "] is not found in the widget registry.");
	}

	public static WidgetRegistry getRegistry() {
		if (instance == null) {
			instance = new WidgetRegistry();
		}
		return instance;
	}
}
