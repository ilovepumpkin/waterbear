package org.waterbear.core.utils;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.waterbear.core.exception.AutomationException;

public class WBStringUtils {

	public static List<String> arrayToList(String[] arr) {
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < arr.length; i++) {
			String str = arr[i];
			l.add(str);
		}
		return l;
	}

	public static String[] listToArray(List<String> l) {
		String[] array = (String[]) l.toArray(new String[l.size()]);
		return array;
	}

	public static String concat(String... strs) {
		/*
		 * StringBuilder is not thread safe but it has much better performance
		 * than StringBuffer in single thread application.
		 */
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strs.length; i++) {
			String str = strs[i];
			sb.append(str);
		}
		return sb.toString();
	}

	public static String stringtify(Object... data) {
		String result = "";
		for (int i = 0, len = data.length; i < len; i++) {
			Object item = data[i];
			if (item != null)
				result += (i == 0 ? "" : ", ") + stringtify(item);
		}
		return result;
	}

	public static String stringtify(Object data) {
		if (data instanceof String[]) {
			return stringtify((String[]) data);
		} else if (data instanceof String[][]) {
			return stringtify((String[][]) data);
		} else {
			return data.toString();
		}
	}

	public static String stringtify(String[] data) {
		String result = "";
		for (int i = 0, len = data.length; i < len; i++) {
			result += (i == 0 ? "" : ", ") + data[i];
		}
		return "[" + result + "]";
	}

	public static String stringtify(List<String> data) {
		String result = "";
		for (int i = 0, len = data.size(); i < len; i++) {
			result += (i == 0 ? "" : ", ") + data.get(i);
		}
		return "[" + result + "]";
	}

	public static String stringtify(String[][] data) {
		String result = "";
		for (int i = 0, len = data.length; i < len; i++) {
			result += (i == 0 ? "" : ", ") + stringtify(data[i]);
		}
		return "[" + result + "]";
	}

	public static String leftPad(String str, int len, String fillChar) {
		int strLen = str.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len - strLen; i++) {
			sb.append(fillChar);
		}
		sb.append(str);
		return sb.toString();
	}

	public static String rightPad(String str, int len, String fillChar) {
		int strLen = str.length();
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < len - strLen; i++) {
			sb.append(fillChar);
		}
		return sb.toString();
	}

	public static int indexOf(String[] strGroup, String str) {
		int groupLen = strGroup.length;
		for (int i = 0; i < groupLen; i++) {
			if (strGroup[i].equals(str))
				return i;

		}

		return -1;
	}

	/**
	 * Convert a String array to a string containing the items in array and
	 * separated with the specified delimiter.
	 * 
	 * @param items
	 * @param delim
	 * @return
	 */
	public static String join(String[] items, final String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			String item = items[i];
			sb.append(delim).append(item);
		}
		return sb.substring(delim.length());
	}

	public static String capitalize(String str) {
		int strLen;

		if ((str == null) || ((strLen = str.length()) == 0)) {
			return str;
		}

		return new StringBuffer(strLen)
				.append(Character.toTitleCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}

	public static String uncapitalize(String str) {
		int strLen;

		if ((str == null) || ((strLen = str.length()) == 0)) {
			return str;
		}

		return new StringBuffer(strLen)
				.append(Character.toLowerCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}

	/**
	 * Convert a normal string to a regular expression by escaping some special
	 * characters.
	 * 
	 * @param str
	 * @return if it looks like "/xxxxx/", returns it without doing nothing.
	 *         Otherwise escape some special characters and return it as
	 *         "\/.*xxxxx.*\/".
	 */
	public static String toRegExp(String str) {
		if (str.startsWith("/") && str.endsWith("/")) {
			return str;
		} else {
			String[] chars = new String[] { "\\", ".", "*", "+", "?", "^", "$",
					"/", "(", ")", "[", "]" };
			for (String c : chars) {
				str = str.replaceAll("\\" + c,
						(c.equals("$") || c.equals("\\") ? "\\\\\\" : "\\\\")
								+ c);
			}
			return "/.*" + str + ".*/";
		}
	}

	/**
	 * 
	 * Test a string.
	 * 
	 * @param opt
	 *            The option defined what kind of test to be done for the texts
	 * @param otherTexts
	 *            text1,text2, text3 .... The text1 is text to be tested, all
	 *            other texts are the necessary parameters for the test.
	 * @return
	 */
	public static boolean testText(Option opt, String toBeTestedText,
			String... otherTexts) {
		if (otherTexts.length == 0) {
			throw new AutomationException(
					"At least one other text should be provided.");
		}
		boolean result = false;
		if (opt == Option.Equals) {
			result = toBeTestedText.equals(otherTexts[0]);
		} else if (opt == Option.EqualsIgnoreCase) {
			result = toBeTestedText.equalsIgnoreCase(otherTexts[0]);
		} else if (opt == Option.Contains) {
			result = toBeTestedText.contains(otherTexts[0]);
		} else if (opt == Option.ContainsIgnoreCase) {
			result = toBeTestedText.toLowerCase().contains(
					otherTexts[0].toLowerCase());
		} else if (opt == Option.In) {
			result = Arrays.asList(otherTexts).contains(toBeTestedText);
		} else {
			throw new AutomationException("Unknown comparision option [" + opt
					+ "].");
		}
		return result;
	}

	/**
	 * Assert a string based on the specified option. For the arguments
	 * description, look at the method testText.
	 * 
	 * @param opt
	 * @param toBeTestedText
	 * @param otherTexts
	 */
	public static void assertText(Option opt, String toBeTestedText,
			String... otherTexts) {
		assertTrue("Assertion failed. Option [" + opt + "], ToBeTestedText ["
				+ toBeTestedText + "] ,Other texts [" + stringtify(otherTexts)
				+ "]", testText(opt, toBeTestedText, otherTexts));
	}

	public enum Option {
		Equals(), EqualsIgnoreCase(), Contains(), ContainsIgnoreCase(), In();
	}
}
