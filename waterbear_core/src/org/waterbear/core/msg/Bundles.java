/** start_Java_Source_User_Copyright_Notice
 IBM Confidential

 OCO Source Materials

 5639-VC6 5639-VM1

 Copyright IBM Corp. 2010, 2011

 The source code for this program is not published or other-
 wise divested of its trade secrets, irrespective of what has
 been deposited with the U.S. Copyright Office.
 end_Java_Source_User_Copyright_Notice */
package org.waterbear.core.msg;

import java.util.Locale;
import java.util.ResourceBundle;

import org.waterbear.core.WaterBearConfiguration;


/**
 * Contains the list of available resource bundles for this application. Has
 * helper functions for getting and formatting resource bundle strings.
 * 
 */
public class Bundles {

	public static class Bundle {

		private String name;

		public Bundle(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	protected static ResourceBundle getBundle(Bundle bundleName, Locale locale) {
		return ResourceBundle.getBundle(bundleName.toString(), locale);
	}

	private static Locale getConfiguredLocale() {
		String localeSetting = WaterBearConfiguration.instance.getProperty(
				"browser.locale");
		Locale locale = null;
		if (localeSetting.indexOf("_") == -1) {
			locale = new Locale(localeSetting);
		} else {
			String language = localeSetting.split("_")[0];
			String country = localeSetting.split("_")[1];
			locale = new Locale(language, country);
		}
		return locale;
	}

	/**
	 * Returns a resource bundle string formatted with any provided arguments
	 * 
	 * @param bundleName
	 * @param locale
	 * @param key
	 * @param arguments
	 * @return
	 */
	public static String getString(Bundle bundle, Locale locale, String key,
			Object... arguments) {
		TextResource tr = new TextResource(bundle, key, arguments);
		return tr.toString(locale);
	}

	/**
	 * Returns a resource bundle string formatted with any provided arguments.
	 * The locale is taken from the effective locale presented by the
	 * UserSession class.
	 * 
	 * @param bundleName
	 * @param key
	 * @param arguments
	 * @return
	 */
	public static String getString(Bundle bundle, String key,
			Object... arguments) {
		return getString(bundle, getConfiguredLocale(), key, arguments);
	}
}
