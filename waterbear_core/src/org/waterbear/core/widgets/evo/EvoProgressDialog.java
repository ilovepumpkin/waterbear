package org.waterbear.core.widgets.evo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.TaskFailException;
import org.waterbear.core.utils.BrowserUtil;
import org.waterbear.core.widgets.dijit.Button;

import static org.waterbear.core.widgets.WidgetFinder.*;

/**
 * 
 * @author shenrui@cn.ibm.com
 * 
 */
public class EvoProgressDialog extends EvoDialog {
	private String formattedMessage;

	public EvoProgressDialog(ElementStub es, Object... stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return getClass().getSimpleName() + "<" + title() + ">";
	}

	public String getFormattedMessage() {
		return formattedMessage;
	}

	/**
	 * if waitTime==0, using the default wait time 60 seconds.
	 * 
	 * @param timeout
	 */
	public EvoProgressDialog waitforTaskdone(int timeout) {
		return waitforTaskdone(timeout, "Close");
	}

	/**
	 * using default timeout and closing button.
	 * 
	 * @return
	 */
	public EvoProgressDialog waitforTaskdone() {
		return waitforTaskdone(0, "Close");
	}

	public EvoProgressDialog waitforTaskdone(int timeout, String btnLabel) {
		Button btn = null;
		if (btnLabel != null) {
			btn = byLabel(btnLabel, Button.class, getButtonBar());
		}
		if (timeout == 0) {
			timeout = 60000;
		}
		log.info("Wait time of " + this + ":" + timeout + " ms");

		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() throws ExecutionException {
				ElementStub percentageES = null;
				if (WaterBearConfiguration.instance.getDojoVersion().equals(
						"1.8.2")) {
					percentageES = getBrowser().div("100%").in(es);
				} else {
					percentageES = getBrowser().span("100%").in(es);
				}

				return getBrowser().isVisible(percentageES);
			}
		};
		getBrowser().waitFor(cond, timeout);

		formattedMessage = formatMessage(false);

		if (getBrowser().span("/checkMark.*|successIcon/").in(es)
				.exists()) {
			log.info(this + formattedMessage);
			if (btn != null) {
				btn.click();
			}
		} else {
			throw new TaskFailException(
					"The task was not finished successfully.",
					formatMessage(true));
		}
		return this;
	}

	/**
	 * Retrieve the detailed message in the Progress Dialog and format it so
	 * it is displayed in multiple lines in the log files.
	 * 
	 * @return The formated detailed message.
	 */
	private String formatMessage(boolean toHTML) {
		final String re = "((?:\\d|1[0-1]):[0-5]\\d [A|P]M)";
		String newLine = "\n";
		String space = "\t";

		String beginToken = "";
		String endToken = "";
		if (toHTML) {
			// beginToken = "&lt;pre&gt;";
			// endToken = "&lt;/pre&gt;";
		}

		StringBuilder sb = new StringBuilder(beginToken + newLine
				+ "Details:" + newLine);
		String text = getBrowser().div("taskProgressLog monospaced")
				.in(es).getText();

		String[] temp = text.split(re);
		int j = 1;
		Matcher m = Pattern.compile(re).matcher(text);
		while (m.find()) {
			sb.append(m.group()).append(space).append(temp[j++])
					.append(newLine);
		}
		sb.append(endToken);
		return sb.toString();
	}

	/**
	 * Get the progress task dialogue title.
	 * 
	 * @return The progress task dialogue title
	 */
	@Override
	public String title() {
		ElementStub titleES = null;
		if (WaterBearConfiguration.instance.getDojoVersion().equals(
				"1.8.2")) {
			titleES = getBrowser().div("h1").in(es);
		} else {
			titleES = getBrowser().span("dijitDialogTitle").in(es);
		}
		return titleES.getText();
	}
}
