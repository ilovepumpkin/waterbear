package org.waterbear.core.utils;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ExecutionException;

import org.apache.log4j.Logger;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.ConditionNotMetException;

public class BrowserUtil {
	protected Logger log = Logger.getLogger(getClass().getSimpleName());
	private static String userAgent = null;
	public static final String BROWSER_UNKNOWN = "Unknown";
	public static final String BROWSER_NA = "NotAvailable";

	public static final String IE8 = "MSIE/8.0";

	private static String browserName = null;

	private static Browser getBrowser() {
		Browser b = WaterBearConfiguration.instance.getBrowser();
		return b;
	}

	public static void setSahiNotStarted() {
		browserName = BROWSER_NA;
	}

	public static String getBrowserName() {
		if (browserName == null) {
			String userAgent = getUserAgent();
			String re = "";
			if (userAgent.contains("Chrome")) {
				re = "Chrome\\/[\\d|.]*";
			} else if (userAgent.contains("MSIE")) {
				re = "MSIE\\s[\\d|.]*";
			} else if (userAgent.contains("Firefox")) {
				re = "Firefox\\/[\\d|.]*";
			} else if (userAgent.contains("rv:")) {
				re = "rv:[\\d|.]*";
			} else {
				throw new AutomationException("Unknown User Agent string ["
						+ userAgent + "]");
			}

			Pattern pattern = Pattern.compile(re);
			Matcher matcher = pattern.matcher(userAgent);
			if (matcher.find()) {
				browserName = matcher.group();
				if (!browserName.trim().equals("")) {
					browserName = browserName.replace("rv:", "IE ").replace(
							" ", "/");
				}
			} else {
				browserName = BROWSER_UNKNOWN;
			}
		}
		return browserName;
	}

	public static String getUserAgent() {
		if (userAgent == null) {
			userAgent = getBrowser().fetch("navigator.userAgent");
		}
		return userAgent;
	}

	public static String getFirefoxVersion() {
		String[] parts = getUserAgent().split("/");
		return parts[parts.length - 1];
	}

	public static boolean isFF3() {
		return getFirefoxVersion().startsWith("3.");
	}

	public static boolean isFF7() {
		return getFirefoxVersion().startsWith("7.");
	}

	public static void waitFor(long waitTime) {
		getBrowser().waitFor(waitTime);
	}

	public static boolean isIE() {
		return getBrowser().isIE();
	}

	public static boolean isIE8() {
		return getBrowserName().equals(IE8);
	}

	public static void navigateTo(String url, boolean forceReload) {
		getBrowser().navigateTo(url, forceReload);
	}

	public static void refreshPage() {
		getBrowser().execute("top.location.reload();");
	}

	public static void resetToBaseUrl() {
		String url = getBrowser().fetch("top.location.href");
		if (url.trim().length() == 0) {
			throw new AutomationException(
					"Something wrong with the retrieved URL [" + url + "]!");
		}
		String[] parts = url.split("/");
		String baseUrl = parts[0] + "//" + parts[2];
		navigateTo(baseUrl, true);
	}

	public static void flushGUICache() {
		getBrowser()
				.execute(
						"rpc.config.flushCache();dojo.publish(\"RESOURCE/InvalidateAll\");");
	}

	public static void flushGUICacheOnly() {
		getBrowser().execute("rpc.config.flushCache();");
	}

	public static void flushGUICacheOnly_Refresh() {
		flushGUICacheOnly();
		refreshPage();
	}

	public static void waitForCond(final WaitForCondition wfCond, int timeout) {
		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() throws ExecutionException {
				return wfCond.test();
			}
		};
		getBrowser().waitFor(cond, timeout);
		if (!cond.test()) {
			throw new ConditionNotMetException(
					"The condition was not met after waiting for the time ["
							+ timeout + "].");
		}
	}

	public static String downloadFile(final String expectedDownloadFileName) {
		BrowserCondition cond = new BrowserCondition(getBrowser()) {
			public boolean test() throws ExecutionException {
				return expectedDownloadFileName.equals(getBrowser()
						.lastDownloadedFileName());
			}
		};
		getBrowser().waitFor(cond, 60000);
		assertEquals(expectedDownloadFileName, getBrowser()
				.lastDownloadedFileName());

		File f = null;
		try {
			f = File.createTempFile("downloadfile", ".tmp");
			getBrowser().saveDownloadedAs(f.getAbsolutePath());
		} catch (IOException e) {
			throw new AutomationException(e);
		}
		getBrowser().clearLastDownloadedFileName();
		assertNull(getBrowser().lastDownloadedFileName());

		Long filelength = f.length();
		byte[] fContent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(f);
			in.read(fContent);
			in.close();
		} catch (IOException e) {
			throw new AutomationException(e);
		}

		String fileContent = new String(fContent);
		return fileContent;
	}
}
