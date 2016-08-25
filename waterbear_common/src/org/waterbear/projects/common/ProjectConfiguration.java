package org.waterbear.projects.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.waterbear.core.exception.AutomationException;

public class ProjectConfiguration {
	private Properties props;

	/**
	 * By this SingletonHolder, this singleton class becomes Lazy Load and
	 * Thread Safe.
	 * 
	 * @author shenrui@cn.ibm.com
	 *
	 */
	private static class SingletonHolder {
		/**
		 * The construction of instance filed is Thread Safe because it is a
		 * static variable. And it won't be created till SingtonHolder is
		 * invoked - Lazy Load.
		 */
		private static ProjectConfiguration instance = new ProjectConfiguration();
	}

	public static ProjectConfiguration getInstance() {
		return SingletonHolder.instance;
	}

	private ProjectConfiguration() {
		props = new Properties();
		try {
			InputStream is = ProjectConfiguration.class.getClassLoader()
					.getResourceAsStream("config.properties");
			props.load(is);
		} catch (Exception e) {
			new AutomationException("Failed to load config.properties", e);
		}

		initializeLogger();

	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}

	private void initializeLogger() {
		Properties logProperties = new Properties();
		try {
			// load our log4j properties / configuration file
			logProperties.load(ProjectConfiguration.class.getClassLoader()
					.getResourceAsStream("log4j.properties"));
			PropertyConfigurator.configure(logProperties);
		} catch (IOException e) {
			throw new RuntimeException("Unable to load logging property ");
		}
	}

	public String getProjectProfile() {
		String profile = getProperty("project.profile");
		if (profile == null) {
			throw new AutomationException(
					"project.profile in config.properties is required.");
		}
		return profile.trim();
	}

	public String getCliHost() {
		String cliHost = getProperty("cli.host");
		if (cliHost == null) {
			cliHost = getProperty("app.url").replace("http://", "")
					.replace("https://", "").split(":")[0];
		}
		return cliHost;
	}
	
	public String getAppUsername(){
		return getProperty("app.username");
	}
	
	public String getAppPassword(){
		return getProperty("app.password");
	}

	public boolean isDebugMode() {
		String modeDebug = getProperty("mode.debug");
		if (modeDebug != null && modeDebug.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
}
