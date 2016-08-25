package org.waterbear.core.widgets.dijit;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.registry.RegEntry;
import org.waterbear.core.widgets.registry.WidgetRegistry;

public abstract class DojoWidget extends WebElement {

	private String widgetName = null;
	private String widgetId = null;
	private RegEntry entry = null;

	private ElementStub fatEs = null;
	private boolean isfatEsMode = false;

	public DojoWidget(ElementStub es, Object... stateValues) {
		this.es = findEnclosingDojoWidget(es);
		this.fatEs = this.es;
		simplifyEs();
	}

	public void toFatEsMode() {
		this.isfatEsMode = true;
		this.es = this.fatEs;
	}

	public boolean isReadonly() {
		String widgetClasses = getWidgetClasses();
		return widgetClasses.contains("dijitReadOnly") ? true : false;
	}

	public void assertReadonly(boolean expected) {
		boolean actual = isReadonly();
		log.info("Verify the widget readonly status - expected [" + expected
				+ "], actual [" + actual + "].");
		assertEquals("Verify the readonly status of this widget.", expected,
				actual);
	}

	public void assertEnabled(boolean expected) {
		boolean actual = isEnabled();
		assertEquals("Verify the enablement status of the widget <"
				+ widgetName() + ">.", expected, actual);
	}

	public void assertWidgetData(Object expected, Object actual) {
		String strExpected = WBStringUtils.stringtify(expected);
		String strActual = WBStringUtils.stringtify(actual);
		log.info("Verifying the " + getClass().getSimpleName()
				+ " value - expected [" + expected + "], actual [" + actual
				+ "]");
		assertEquals(strExpected, strActual);
	}

	public boolean isEnabled() {
		String widgetClasses = getWidgetClasses();
		return widgetClasses.contains("dijitDisabled") ? false : true;
	}

	protected ElementStub findEnclosingDojoWidget(ElementStub es) {
		if (testWidgetId(es)) {
			return es;
		} else {
			final String elemType = getRegEntry().getWidgetElemType();
			ElementStub widgetES = es.parentNode();
			while (!testWidgetId(widgetES)) {
				widgetES = widgetES.parentNode(elemType);
				if (widgetES == null) {
					throw new AutomationException(
							"Cannot find the enclosing widget [widget id:"
									+ getRegEntry().getIdentifiers() + "] of "
									+ es);
				}
			}
			return widgetES;
		}
	}

	private void simplifyEs() {
		final String widgetId = getDojoWidgetId();
		this.es = getBrowser().accessor(
				"_sahi._" + getRegEntry().getWidgetElemType() + "({widgetid:'"
						+ widgetId + "'})");
	}

	protected boolean testWidgetId(ElementStub es) {
		final String widgetId = es.fetch("getAttribute(\"widgetid\")");

		if (widgetId == null || widgetId.isEmpty()
				|| widgetId.trim().equals("null")) {
			return false;
		} else if (isWidgetIdValid(widgetId)) {
			this.widgetId = widgetId;
			return true;
		} else {
			throw new AutomationException(
					"Cannot find the enclosing widget with the widget id <"
							+ widgetId + "> in "
							+ getRegEntry().getIdentifiers());
		}
	}

	protected boolean isWidgetIdValid(String widgetId) {
		if (widgetId.matches(".*_\\d+$")) {
			widgetId = widgetId.replaceFirst("_\\d+$", "");
		}
		return getRegEntry().getIdentifiers().contains(widgetId);
	}

	private RegEntry getRegEntry() {
		if (entry == null) {
			entry = WidgetRegistry.getRegistry().getRegEntry(
					getClass().getCanonicalName());
			if (entry == null) {
				throw new AutomationException(
						"Oops! Cannot find the entry for [" + getClass()
								+ "] in the widget registry.");
			}
		}

		return entry;
	}

	private String fetchWidgetId() {
		String widgetId = es.fetch("getAttribute(\"widgetid\")");
		if (widgetId == null) {
			throw new AutomationException("Widget Id is null.");
		}
		return widgetId;
	}

	public String getDojoWidgetId() {
		if (isfatEsMode) {
			return fetchWidgetId();
		} else {
			if (widgetId == null) {
				widgetId = fetchWidgetId();
			}
			return widgetId;
		}
	}

	public boolean isValid() {
		final String widgetId = getDojoWidgetId();
		String isValid = getBrowser().fetch(
				"var w=dijit.byId('" + widgetId
						+ "');w.isValid?w.isValid():true;");
		return isValid.equals("true") ? true : false;
	}

	public String message() {
		final String widgetId = getDojoWidgetId();
		String message = getBrowser().fetch(
				"var w=dijit.byId('" + widgetId + "');w.message?w.message:'';");
		return message;
	}

	@Override
	protected String widgetName() {
		String widgetName = null;
		if (isfatEsMode) {
			widgetName = genWidgetName();
		} else {
			if (this.widgetName == null) {
				this.widgetName = getBrowser().fetch(
						"dijit.byId('" + getDojoWidgetId()
								+ "').dojoAttachPoint");
				if ("".equals(this.widgetName)) {
					this.widgetName = getDojoWidgetId();
				}
			}
			widgetName = this.widgetName;
		}
		return getClass().getSimpleName() + "<" + widgetName + ">";
	}

	private String genWidgetName() {
		String widgetName = getBrowser().fetch(
				"dijit.byId('" + getDojoWidgetId() + "').dojoAttachPoint");
		if ("".equals(widgetName)) {
			widgetName = getDojoWidgetId();
		}
		return widgetName;
	}

	@Override
	public String toString() {
		return widgetName();
	}

	public <T extends DojoWidget> T byLabel(String labelText,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byLabel(labelText, dojoClazz, args);
	}

	public <T extends DojoWidget> T byAttribute(String identifer,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byAttribute(identifer, dojoClazz, args);
	}

	public <T extends DojoWidget> T byIndex(int index,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byIndex(index, dojoClazz, args);
	}

	public WebElement webElem(Object identifier, String elemType,
			Object... args) {
		return WidgetFinder.webElem(identifier, elemType, args);
	}

	public <T extends DojoWidget> T byLabel(WebElement labelElem,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byLabel(labelElem, dojoClazz, args);
	}
}
