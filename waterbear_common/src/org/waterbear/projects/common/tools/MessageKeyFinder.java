package org.waterbear.projects.common.tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

public abstract class MessageKeyFinder {

	protected abstract Class[] getBundleClasses();

	public void search(String[] texts) {
		Class[] bundleClazzes = getBundleClasses();

		for (int i = 0; i < texts.length; i++) {
			String text = texts[i].trim();
			if (text == "") {
				continue;
			}
			System.out.println("[ " + text + " ]");
			for (int k = 0; k < bundleClazzes.length; k++) {
				Class clazz = bundleClazzes[k];
				String clazzName = clazz.getSimpleName();
				try {
					Object obj = clazz.getConstructor().newInstance();
					Field[] bundles = clazz.getDeclaredFields();
					for (int j = 0; j < bundles.length; j++) {
						Field b = bundles[j];
						String resName = b.get(obj).toString();
						List keys = search(resName, text);
						for (Iterator it = keys.iterator(); it.hasNext();) {
							String key = (String) it.next();
							String varName = key.substring(
									key.lastIndexOf(".") + 1).toUpperCase();
							System.out
									.println("public static final String "
											+ varName + " = getString("
											+ clazzName + "." + b.getName()
											+ ", \"" + key + "\");");
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			System.out.println("");
		}

	}

	private List search(String resName, String text) {
		List results = new ArrayList();
		Properties props = new Properties();
		resName = resName.replaceAll("\\.", "/") + ".properties";
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				resName);

		try {
			props.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<Entry<Object, Object>> values = props.entrySet();

		for (Iterator<Entry<Object, Object>> it = values.iterator(); it
				.hasNext();) {
			Entry obj = (Entry) it.next();
			if (obj.getValue().equals(text)) {
				results.add(obj.getKey().toString());
			}
		}
		return results;
	}

	protected static String[] readMessages() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input your messages (seperated with comma):");
		String str = scanner.nextLine();
		return str.split(",");
	}
}
