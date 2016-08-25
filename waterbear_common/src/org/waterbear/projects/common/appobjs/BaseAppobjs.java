package org.waterbear.projects.common.appobjs;

import static org.waterbear.core.widgets.WidgetFinder.byLabel;
import static org.waterbear.core.widgets.WidgetFinder.progressDlg;
import net.sf.sahi.client.Browser;

import org.apache.log4j.Logger;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.msg.Bundles;
import org.waterbear.core.msg.Bundles.Bundle;
import org.waterbear.core.msg.EvoBundle;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.dijit.Button;
import org.waterbear.core.widgets.dijit.DojoWidget;
import org.waterbear.core.widgets.evo.EvoDialog;
import org.waterbear.core.widgets.evo.EvoProgressDialog;

public abstract class BaseAppobjs {
	private WebElement self;

	protected Logger log = Logger.getLogger(getClass().getSimpleName());

	protected Browser getBrowser() {
		return WaterBearConfiguration.instance.getBrowser();
	}

	protected static String getString(Bundle bundle, String key) {
		return Bundles.getString(bundle, key);
	}

	public EvoProgressDialog clickButtonWithProgressDialog(Button btn,
			String progressDialogTitle, int waitTime) {
		btn.click();
		return progressDlg(progressDialogTitle).waitforTaskdone(waitTime);
	}

	public EvoProgressDialog clickOKWithProgressDialog(
			String progressDialogTitle, int waitTime) {
		byLabel("OK", Button.class, self()).click();
		return progressDlg(progressDialogTitle).waitforTaskdone(waitTime);
	}

	public void clickCancel() {
		byLabel("Cancel", Button.class, self()).click();
	}

	public void clickEdit() {
		byLabel(getString(EvoBundle.Evo, "global.labels.edit"), Button.class,
				self()).click();
	}

	public void clickOK() {
		byLabel(getString(EvoBundle.Evo, "global.labels.ok"), Button.class,
				self()).click();
	}

	public void clickRefresh() {
		byLabel(getString(EvoBundle.Evo, "global.labels.refresh"),
				Button.class, self()).click();
	}

	protected abstract WebElement locateSelf();

	protected WebElement self() {
		if (self == null) {
			self = locateSelf();
		}
		return self;
	}

	public static <T extends DojoWidget> T byLabel(String labelText,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byLabel(labelText, dojoClazz, args);
	}

	public static <T extends DojoWidget> T byAttribute(String identifer,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byAttribute(identifer, dojoClazz, args);
	}

	public static <T extends DojoWidget> T byIndex(int index,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		return WidgetFinder.byIndex(index, dojoClazz, args);
	}

	public static WebElement webElem(Object identifier, String elemType,
			Object... args) {
		return WidgetFinder.webElem(identifier, elemType, args);
	}

	public void validateForm(WebElement we) {
		final String id = we.getAttribute("id");
		final String jsStr = "getErrMsgs(dojo.byId(\"" + id + "\"))";
		String errMessage = getBrowser().fetch(jsStr);
		if (!errMessage.equals("undefined")) {
			log.error("[Form Validation]\n" + errMessage);
		} else {
			log.info("[Form Validation] OK!");
		}
	}

	public void validateForm() {
		validateForm(self());
	}

	protected WebElement visibleTabPanel() {
		return webElem("dijitStackContainerChildWrapper dijitVisible", ET.DIV);
	}
}
