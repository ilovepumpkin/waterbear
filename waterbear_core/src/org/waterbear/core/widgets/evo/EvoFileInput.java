package org.waterbear.core.widgets.evo;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

import org.waterbear.core.widgets.dijit.DojoWidget;

public class EvoFileInput extends DojoWidget {

	public EvoFileInput(ElementStub es, Object[] stateValues) {
		super(es, stateValues);
		// TODO Auto-generated constructor stub
	}

	public void upload(String filePath, int waitTime, boolean isAutoUpload) {
		ElementStub fileEs = getBrowser().file(0).in(es);

		final String setFileSahiScript = "_sahi._setFile(" + fileEs + ", '"
				+ filePath + "', 'FileUploadHandler')";
		log.info(setFileSahiScript);
		getBrowser().execute(setFileSahiScript);

		StringBuffer mainJS = new StringBuffer();
		mainJS.append("var $fileinput=dijit.getEnclosingWidget(" + fileEs
				+ ");");

		String changeTypeScript = "$fileinput.fileInput.type='text';";
		/*
		 * This code is for IE8, but actually IE8 is already not supported by
		 * our products. Not sure if IE9 or IE10 need this code. 
		 * 
		 * if
		 * (getBrowser().isIE()) { changeTypeScript = fileEs + ".outerHTML=" +
		 * fileEs +
		 * ".outerHTML.replace(/type=file/,'type=text');$fileinput.fileInput=" +
		 * getBrowser().textbox(0).in(es) + ";"; }
		 */
		mainJS.append(changeTypeScript);
		mainJS.append("$fileinput.fileInput.value=\"" + filePath + "\";");
		if (!isAutoUpload) {
			mainJS.append("_sahi._blur($fileinput.fileInput);");
		} else {
			mainJS.append("$fileinput.uploadFile();");
		}

		log.info(mainJS);
		getBrowser().execute(mainJS.toString());

		//The following code takes long time - uncomment them unless they are really needed.
//		BrowserCondition cond = new BrowserCondition(getBrowser()) {
//			public boolean test() throws ExecutionException {
//				return !getBrowser().file(0).in(es).exists();
//			}
//		};
//		getBrowser().waitFor(cond, waitTime);
//
//		assertFalse(getBrowser().file(0).in(es).exists());
//		assertFalse(getBrowser().span("/progressIcon.*/").in(es).exists());
	}
}
