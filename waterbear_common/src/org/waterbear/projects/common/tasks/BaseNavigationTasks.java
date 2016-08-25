package org.waterbear.projects.common.tasks;

import static org.waterbear.core.widgets.WidgetFinder.byIndex;

import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.WidgetFinder;
import org.waterbear.core.widgets.evo.EvoFishEye;

public class BaseNavigationTasks extends BaseTasks {

	protected EvoFishEye fe;

	public BaseNavigationTasks() {
		super();
		fe = byIndex(0, EvoFishEye.class);
		fe.toFatEsMode();
	}

	protected void gotoPage(String level1Text, String level2Text) {
		fe.clickMenuItem(level1Text, level2Text);
		BrowserUtil.refreshPage();
	}

	protected void gotoSubPage(String level2Text, String level3Text) {
		WidgetFinder.webElem(
				"{sahiText:'" + level3Text + "',className:'h3 lightBlack'}",
				ET.DIV, getBrowser().heading3(level2Text).parentNode()).click();
	}

}
