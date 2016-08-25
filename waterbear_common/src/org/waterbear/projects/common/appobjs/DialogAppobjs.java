package org.waterbear.projects.common.appobjs;

import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ExecutionException;

import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.dijit.Button;
import org.waterbear.core.widgets.evo.EvoDialog;
import org.waterbear.core.widgets.evo.EvoProgressDialog;

public abstract class DialogAppobjs extends BaseAppobjs {

	public DialogAppobjs() {
		super();
	}

	public void next() {
		byLabel("/Next.*/", Button.class, self()).click();
	}

	public void previous() {
		byLabel("/.*Previous/", Button.class, self()).click();
	}

	public Button getFinishBtn() {
		return byLabel("Finish", Button.class, self());
	}

	public Button ok() {
		return byLabel("OK", Button.class, self());
	}

	public void clickOK() {
		((EvoDialog) self()).clickOK();
	}

	public EvoProgressDialog clickOKWithProgressDialog(
			String progressDialogTitle, int timeout) {
		return ((EvoDialog) self()).clickOKWithProgressDialog(
				progressDialogTitle, timeout);
	}

	@Override
	public EvoProgressDialog clickButtonWithProgressDialog(Button btn,
			String progressDialogTitle, int timeout) {
		return ((EvoDialog) self()).clickButtonWithProgressDialog(btn,
				progressDialogTitle, timeout);
	}

	public EvoProgressDialog clickButtonWithProgressDialog(String btnLabel,
			String progressDialogTitle, int timeout) {
		return ((EvoDialog) self()).clickButtonWithProgressDialog(
				btnByLabel(btnLabel), progressDialogTitle, timeout);
	}

	protected Button btnByLabel(String btnLabel) {
		Button btn = byLabel(btnLabel, Button.class,
				((EvoDialog) self()).getButtonBar());
		return btn;
	}

	public void clickYES() {
		((EvoDialog) self()).clickYES();
	}

	public void clickAdd() {
		((EvoDialog) self()).clickAdd();
	}

	/**
	 * Close the dialog via the cancel icon at the top-right corner of the
	 * dialogue.
	 */
	public void kill() {
		((EvoDialog) self()).kill();
	}

	public EvoProgressDialog clickYESWithProgressDialog(
			String progressDialogTitle, int waitTime) {
		return ((EvoDialog) self()).clickYESWithProgressDialog(
				progressDialogTitle, waitTime);
	}

	public void clickNO() {
		((EvoDialog) self()).clickNO();
	}

	public void clickClose() {
		((EvoDialog) self()).clickClose();
	}

	public void clickCancel() {
		((EvoDialog) self()).clickCancel();
	}

	public boolean exists() {
		return ((EvoDialog) self()).exists();
	}

	public void assertTitle(String expected) {
		((EvoDialog) self()).assertTitle(expected);
	}

	public String title() {
		return ((EvoDialog) self()).title();
	}

	public void waitForExists(int timeout) {
		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() throws ExecutionException {
				return ((EvoDialog) self()).exists();
			}
		};
		getBrowser().waitFor(cond, timeout);
	}

	public static EvoDialog dlg(final String title, final Object... args) {
		return WidgetFinder.dlg(title, args);
	}
}
