package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertTrue;
import static org.waterbear.core.widgets.WidgetFinder.byLabel;
import static org.waterbear.core.widgets.WidgetFinder.progressDlg;
import static org.waterbear.core.widgets.WidgetFinder.webElem;

import java.util.regex.Pattern;

import net.sf.sahi.client.ElementStub;

import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.dijit.Button;
import org.waterbear.core.widgets.dijit.DojoWidget;

/**
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class EvoDialog extends DojoWidget {

	private String dlgId = null;

	public EvoDialog(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		dlgId = es.fetch("id");
	}

	public String title() {
		return getBrowser().span("titleNode").in(es).getText();
	}

	public void assertTitle(String expected) {
		boolean matched = false;
		if (expected.startsWith("/") && expected.endsWith("/")) {
			expected = expected.substring(1, expected.length() - 1);
			Pattern p = Pattern.compile(expected);
			matched = p.matcher(title()).find();
		} else {
			matched = title().equals(expected);
		}
		assertTrue("The dialogue title is not expected. Actual: [" + title()
				+ "], Expected: [" + expected + "].", matched);
	}

	protected ElementStub byTitle(String title) {
		return getBrowser().div(title).parentNode("div");
	}

	public Button getButton(String btnLabel) {
		return (Button) byLabel(btnLabel, Button.class, getButtonBar());
	}

	protected Button getClose() {
		return (Button) byLabel("Close", Button.class, getButtonBar());
	}

	public void clickClose() {
		getClose().click();
	}

	public void clickCancel() {
		byLabel("Cancel", Button.class, getButtonBar()).click();
	}

	public void clickButton(String btnLabel) {
		byLabel(btnLabel, Button.class, getButtonBar()).click();
	}

	public void clickContinue() {
		byLabel("Continue", Button.class, getButtonBar()).click();
	}

	public void clickNO() {
		byLabel("No", Button.class, getButtonBar()).click();
	}

	public void clickOK() {
		byLabel("OK", Button.class, getButtonBar()).click();
	}

	public void clickYES() {
		byLabel("Yes", Button.class, getButtonBar()).click();
	}
	public void clickAdd(){
		byLabel("Add",Button.class,getButtonBar()).click();
	}

	public EvoProgressDialog clickOKWithProgressDialog(
			String progressDialogTitle, int waitTime) {
		clickOK();
		return progressDlg(progressDialogTitle).waitforTaskdone(waitTime);
	}

	public EvoProgressDialog clickYESWithProgressDialog(
			String progressDialogTitle, int timeout) {
		clickYES();
		return progressDlg(progressDialogTitle).waitforTaskdone(timeout);
	}

	public EvoProgressDialog clickButtonWithProgressDialog(String btnText,
			String progressDialogTitle, int timeout) {
		byLabel(btnText, Button.class, getButtonBar()).click();
		return progressDlg(progressDialogTitle).waitforTaskdone(timeout);
	}

	public EvoProgressDialog clickButtonWithProgressDialog(Button btn,
			String progressDialogTitle, int timeout) {
		btn.click();
		return progressDlg(progressDialogTitle).waitforTaskdone(timeout);
	}

	public void kill() {
		webElem("dijitDialogCloseIcon", ET.SPAN, es).click();
	}

	@Override
	public void verify(Object... stateValues) {
		// TODO Auto-generated method stub

	}

	public boolean exists() {
		return getBrowser().byId(dlgId).exists();
	}

	public WebElement getButtonBar() {
		return webElem("/dijitDialogPaneActionBar.*/", ET.DIV, es);
	}

}
