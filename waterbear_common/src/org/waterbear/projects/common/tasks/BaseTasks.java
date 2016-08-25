package org.waterbear.projects.common.tasks;

import static org.waterbear.core.widgets.WidgetFinder.webElem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ExecutionException;

import org.apache.log4j.Logger;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WebElement;
import org.waterbear.projects.common.testcases.BaseTest;

public class BaseTasks {

	protected Logger log = Logger.getLogger(this.getClass());

	public boolean isPageLoading() {
		WebElement divLoadingStatus = webElem("loadingStatus", ET.DIV);
		return divLoadingStatus.isVisible();
	}

	public boolean isPageOffine() {
		WebElement divOfflineStatus = webElem("offlineStatus", ET.DIV);
		return divOfflineStatus.isVisible();
	}

	public void waitForLoadingDone() {
		Browser b = getBrowser();
		BrowserCondition cond = new BrowserCondition(b) {
			public boolean test() throws ExecutionException {
				return !isPageLoading();
			}
		};

		b.waitFor(cond, 30000);
	}

	protected Browser getBrowser() {
		return WaterBearConfiguration.instance.getBrowser();
	}

}
