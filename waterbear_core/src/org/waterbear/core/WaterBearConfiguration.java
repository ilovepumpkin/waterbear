package org.waterbear.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import net.sf.sahi.client.Browser;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.widgets.registry.RegEntry;
import org.waterbear.core.widgets.registry.WidgetRegistry;

/**
 * 
 * Single element enum is the best way to implement Singleton class.
 * 
 * @author shenrui@cn.ibm.com
 *
 */
public enum WaterBearConfiguration {
	instance();

	private Properties props;
	private Browser browser;

	private String dojoVersion;

	private final String PROP_DOJO_VERSION = "dojo.version";
	private final String PROP_DOJO_VERSION_CLASSNAME = "dojo.version.classname";

	private final Collection<String> SUPPORTED_DOJO_VERSIONS = Arrays
			.asList(new String[] { "1.8.2", "1.8.5", "1.9.0", "1.9.3", "1.9.7" });

	private final String WATER_BEAR_CONFIGURATION_FILENAME = "waterbear.properties";

	private WaterBearConfiguration() {
		props = new Properties();
		try {
			InputStream is = WaterBearConfiguration.class.getClassLoader()
					.getResourceAsStream(WATER_BEAR_CONFIGURATION_FILENAME);
			props.load(is);
		} catch (Exception e) {
			new AutomationException("Failed to load "
					+ WATER_BEAR_CONFIGURATION_FILENAME, e);
		}

		initSahi();

		loadWidgetDefs();

		readDojoVersion();
	}

	private void readDojoVersion() {
		if (props.getProperty(PROP_DOJO_VERSION_CLASSNAME) != null) {
			dojoVersion = readDojoVersionFromClass();
		} else if (props.getProperty(PROP_DOJO_VERSION) != null) {
			dojoVersion = readDojoVersionFromFile();
		} else {
			throw new AutomationException("Either "
					+ PROP_DOJO_VERSION_CLASSNAME + " or " + PROP_DOJO_VERSION
					+ " should be defined in waterbear.properties.");
		}

		if (!SUPPORTED_DOJO_VERSIONS.contains(dojoVersion)) {
			throw new AutomationException("The dojo version [" + dojoVersion
					+ "] is not supported yet.");
		}
	}

	private String readDojoVersionFromFile() {
		String dojoVer = props.getProperty(PROP_DOJO_VERSION);

		if (!dojoVer.matches("\\d+\\.\\d+\\.\\d+")) {
			throw new AutomationException("The valid format of "
					+ PROP_DOJO_VERSION + " is '<num>.<num>.<num>'.");
		}

		return dojoVer;
	}

	private String readDojoVersionFromClass() {
		String className = props.getProperty(PROP_DOJO_VERSION_CLASSNAME)
				.trim();

		try {
			String ver = ((String) Class.forName(className)
					.getMethod("getVersion")
					.invoke(Class.forName(className).newInstance()));
			return ver;
		} catch (Exception e) {
			throw new AutomationException(
					"Failed to retrieve the dojo version from the defined class ["
							+ className + "]", e);
		}

	}

	public String getProperty(String key) {
		String value = props.getProperty(key);
		if (value == null) {
			throw new AutomationException(key
					+ " is required. Please check if you include "
					+ WATER_BEAR_CONFIGURATION_FILENAME + " in your CLASSPATH.");
		} else {
			return value;
		}
	}

	private void initSahi() {
		final String sahiBase = getProperty("sahi.base.dir");
		String userDataDirectory = props.getProperty("sahi.userdata.dir");
		if (userDataDirectory == null) {
			try {
				userDataDirectory = new File(sahiBase, "userdata")
						.getCanonicalPath();
			} catch (IOException e) {
				throw new AutomationException(e);
			}
		}
		final String browserName = getProperty("browser.name");

		net.sf.sahi.config.Configuration.initJava(sahiBase, userDataDirectory);
		browser = new Browser(browserName);
	}

	private void loadWidgetDefs() {
		final String[] wdefFileNames = new String[] { "core.wdef",
				"project_specific.wdef" };
		WidgetRegistry wr = WidgetRegistry.getRegistry();
		for (int i = 0; i < wdefFileNames.length; i++) {
			String wdefFileName = wdefFileNames[i];
			InputStream is = WaterBearConfiguration.class.getClassLoader()
					.getResourceAsStream(wdefFileName);

			InputStreamReader isr = null;
			String line = null;
			BufferedReader br = null;

			if (is == null) {
				throw new AutomationException(
						wdefFileName
								+ " was not found in CLASSPATH. Please include them in your CLASSPATH.");
			}

			try {
				isr = new InputStreamReader(is);

				br = new BufferedReader(isr);

				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.length() > 0 && !line.startsWith("#")) {
						String[] temp = line.split("=");
						String widgetClassName = temp[0];
						try {
							Class.forName(widgetClassName, false,
									WaterBearConfiguration.class
											.getClassLoader());
						} catch (ClassNotFoundException e) {
							throw new AutomationException(widgetClassName
									+ " is not found.", e);
						}

						String wSpecStr = temp[1];
						if (wSpecStr.indexOf("|") == -1) {
							String[] identifiers = wSpecStr.split(",");
							if (!wr.exists(widgetClassName)) {
								throw new AutomationException(
										"You were trying to extend a widget definition but ["
												+ widgetClassName
												+ "] is not defined yet.");
							} else {
								wr.extend(widgetClassName, identifiers);
							}

						} else {
							String[] wSpecs = wSpecStr.split("\\|");

							if (wSpecs.length != 4) {
								throw new AutomationException(
										"Invalid format - " + line);
							}

							String widgetElemType = wSpecs[0];
							String coreElemType = wSpecs[1];
							String labelPlacement = wSpecs[2];
							String[] identifiers = wSpecs[3].split(",");

							if (!wr.exists(widgetClassName)) {
								RegEntry regEntry = new RegEntry(identifiers,
										widgetElemType, coreElemType,
										labelPlacement, widgetClassName);
								wr.register(widgetClassName, regEntry);
							} else {
								throw new AutomationException(
										"["
												+ widgetClassName
												+ "] is already defined. You cannot define it twice. The line is being processed is ["
												+ line + "]");
							}
						}

					}
				}

			} catch (IOException e) {
				throw new AutomationException(
						"Failed to load widget definitions.", e);
			} finally {
				try {
					isr.close();
					br.close();
				} catch (IOException e) {
					throw new AutomationException(e);
				}

			}
		}

	}

	public Browser getBrowser() {
		return browser;
	}

	public Browser createBrowser() {
		final String browserName = getProperty("browser.name");
		browser = new Browser(browserName);
		return browser;
	}

	public String getDojoVersion() {
		return dojoVersion;
	}

}
