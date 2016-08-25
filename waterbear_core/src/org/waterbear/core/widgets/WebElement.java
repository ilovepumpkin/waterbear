package org.waterbear.core.widgets;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.function.Predicate;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;

import org.apache.log4j.Logger;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.core.utils.WaitForCondition;
import org.waterbear.core.utils.asserter.TestStrategy;
import org.waterbear.core.utils.asserter.WBAsserter;

/**
 * This class is the root super class for the all the element classes.
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class WebElement {

	protected Logger log = Logger.getLogger(getClass().getSimpleName());

	protected ElementStub es;

	public static final String COMBO_KEY_SHIFT = "SHIFT";
	public static final String COMBO_KEY_CTRL = "CTRL";

	public static final int KEYCODE_ENTER = 13;

	private String widgetClasses;

	public WebElement() {

	}

	public WebElement(ElementStub es, Object... stateValues) {
		this.es = es;
		if (stateValues != null && stateValues.length > 0) {
			log.info("Verifying widget state values ["
					+ WBStringUtils.stringtify(stateValues) + "]");
			verify(stateValues);
		}
	}

	protected Browser getBrowser() {
		return WaterBearConfiguration.instance.getBrowser();
	}

	public void verify(Object... stateValues) {
		// do nothing by default;
	}

	public void click() {
		log("_click");
		es.click();
	}

	public void clickWithCombo(String comboKey) {
		log("_click", comboKey);
		getBrowser().execute("_sahi._click(" + es + ",\"" + comboKey + "\")");
		getBrowser().waitFor(500);
	}

	public void doubleClick() {
		log("_doubleClick");
		es.doubleClick();
	}

	public void focus() {
		log("_focus");
		es.focus();
	}

	public void removeFocus() {
		log("_removeFocus");
		es.removeFocus();
	}

	public void blur() {
		log("_blur");
		es.blur();
	}

	public String getText() {
		log("_getText");
		return es.getText();
	}

	public String getValue() {
		log("_getValue");
		return es.getValue();
	}

	public ElementStub parentNode() {
		log("_parentNode");
		return es.parentNode();
	}

	public ElementStub parentNode(String tagName) {
		log("_parentNode", tagName);
		return es.parentNode(tagName);
	}

	public void setValue(Object value) {
		log("_setValue", value.toString());
		es.setValue(value.toString());
	}

	public void rightClick() {
		log("_rightClick");
		es.rightClick();
	}

	public void mouseOver() {
		log("_mouseOver");
		es.mouseOver();
	}

	public boolean isVisible() {
		return es.isVisible();
	}

	public void highlight() {
		log("_highlight");
		getBrowser().execute("_sahi._highlight(" + es + ")");
	}

	public boolean exists() {
		log("_exists");
		return es.exists();
	}

	public String getWidgetClasses() {
		if (widgetClasses == null) {
			widgetClasses = es.fetch("className");
		}
		return widgetClasses;
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + es + "]";
	}

	public ElementStub getElementStub() {
		return es;
	}

	public void choose(String value) {
		log("_setSelected", value);
		es.choose(value);
	}

	public String getSelectedText() {
		log("_getSelectedText");
		return es.getSelectedText();
	}

	public String getAttribute(String attrName) {
		log("_getAttribute", attrName);
		return es.getAttribute(attrName);
	}

	public boolean containsHTML(String expected) {
		log("_containsHTML", expected);
		return es.containsHTML(expected);
	}

	public boolean enabled() {
		return es.fetch("disabled").equals("false");
	}

	public void assertEnabled(boolean expectEnabled) {
		final boolean enabled = enabled();
		assertEquals(es + " was expected to be enabled but not.",
				expectEnabled, enabled);
	}

	protected void log(String action) {
		String message = widgetName() + " " + action + "(" + es + ")";
		log.info(message);
	}

	protected void log(String action, ElementStub someES) {
		String message = widgetName() + " " + action + "(" + someES + ")";
		log.info(message);
	}

	protected void log(String action, String target) {
		String message = widgetName() + " " + action + "(" + target + ")";
		log.info(message);
	}

	protected void log(String action, String target, String actionValue) {
		String message = widgetName() + " " + action + "(" + target + ",\""
				+ actionValue + "\")";
		log.info(message);
	}

	protected String widgetName() {
		return getClass().getSimpleName();
	}

	public WebElement in(WebElement inWidget) {
		return new WebElement(es.in(inWidget.es));
	}

	public WebElement near(WebElement nearWidget) {
		return new WebElement(es.near(nearWidget.es));
	}

	public WebElement under(WebElement underWidget) {
		return new WebElement(es.under(underWidget.es));
	}

	public void assertExists(boolean expected) {
		assertEquals(expected, exists());
	}

	public void assertSelected(String expected) {
		assertEquals(expected, getValue());
	}

	public void assertText(String expected) {
		assertEquals(expected, getText());
	}

	public void assertText(Predicate<String> pred) {
		assertTrue(pred.test(getText()));
	}

	public void assertText(TestStrategy<String> ts, String... args) {
		WBAsserter.assertText(ts, getText(), args);
	}

	public void assertValue(Object expected) {
		assertEquals(expected.toString(), getValue());
	}

	public void assertTextIgnoreCase(String expected) {
		assertEquals(expected.toLowerCase(), getText().toLowerCase());
	}

	public void assertTextExists_IgnoreCase(String... expected) {
		for (int i = 0; i < expected.length; i++) {
			String exp = expected[i];
			assertTrue(
					"The text [" + exp + "] was not found in [" + es.getText()
							+ "]",
					es.getText().toLowerCase().contains(exp.toLowerCase()));
		}
	}

	public void assertAttribute(String expected, String attrName) {
		assertEquals(expected, es.getAttribute(attrName));
	}

	public void assertTextNotEmpty() {
		assertFalse(getText().trim().equals(""));
	}

	public void assertVisible(boolean expected) {
		assertEquals(expected, isVisible());
	}

	public void assertTextExists(String... expected) {
		for (int i = 0; i < expected.length; i++) {
			String exp = expected[i];
			boolean found = false;
			if (exp.startsWith("/") && exp.endsWith("/")) {
				found = es.getText().matches(exp);
			} else {
				found = es.containsText(exp);
			}
			assertTrue(
					"The text [" + exp + "] was not found in [" + es.getText()
							+ "]", found);
		}
	}

	public void assertClassContains(String expected) {
		assertTrue(getWidgetClasses().contains(expected));
	}

	public void assertClassNotContains(String expected) {
		assertFalse(getWidgetClasses().contains(expected));
	}

	public void assertHTMLContains(String... expected) {
		for (int i = 0; i < expected.length; i++) {
			String exp = expected[i];
			assertTrue(es.containsHTML(exp));
		}
	}

	public void assertHTMLNotContains(String... expected) {
		for (int i = 0; i < expected.length; i++) {
			String exp = expected[i];
			assertFalse(es.containsHTML(exp));
		}
	}

	public void assertAttributeContains(String expected, String attrName) {
		assertTrue(es.getAttribute(attrName).contains(expected));
	}

	public void waitForExists() {
		BrowserUtil.waitForCond(new WaitForCondition() {
			@Override
			public boolean test() {
				return exists();
			}
		}, 10000);
	}
}
