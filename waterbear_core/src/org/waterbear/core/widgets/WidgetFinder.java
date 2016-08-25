package org.waterbear.core.widgets;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.sf.sahi.client.Browser;
import net.sf.sahi.client.BrowserCondition;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.client.ExecutionException;

import org.apache.log4j.Logger;
import org.waterbear.core.WaterBearConfiguration;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.DialogNotFoundException;
import org.waterbear.core.exception.ObjectNotFoundException;
import org.waterbear.core.utils.WBStringUtils;
import org.waterbear.core.widgets.dijit.Button;
import org.waterbear.core.widgets.dijit.DojoWidget;
import org.waterbear.core.widgets.dijit.Menu;
import org.waterbear.core.widgets.dijit.Textbox;
import org.waterbear.core.widgets.evo.EvoDialog;
import org.waterbear.core.widgets.evo.EvoMenu;
import org.waterbear.core.widgets.evo.EvoProgressDialog;
import org.waterbear.core.widgets.registry.RegEntry;
import org.waterbear.core.widgets.registry.WidgetRegistry;

/**
 * Design Pattern: Static Factory
 * 
 */
public class WidgetFinder {

	private static final WidgetRegistry REGISTRY = WidgetRegistry.getRegistry();

	protected static Logger log = Logger.getLogger(WidgetFinder.class
			.getSimpleName());

	private static final int MAX_TRY_COUNT = 3;

	private static final String[] PROGRESS_DLG_CLASS = new String[] {
			"aspenTaskProgressDialog", "evoTaskProgressDialog" };

	protected static Browser getBrowser(Object... args) {
		Browser b = extractBrowser(args);
		return b == null ? WaterBearConfiguration.instance.getBrowser() : b;
	}

	protected static ElementStub bind(ElementStub es, ElementStub relElem,
			Where w) {
		ElementStub rtES = null;
		if (relElem != null) {
			if (w.equals(Where.IN)) {
				rtES = es.in(relElem);
			} else if (w.equals(Where.NEAR)) {
				rtES = es.near(relElem);
			} else if (w.equals(Where.UNDER)) {
				rtES = es.under(relElem);
			}
		}else{
			rtES = es;
		}
		return rtES;
	}

	private static Where extractWhere(Where defaultWhere, Object... args) {
		Where w = extractWhere(args);
		return w == null ? defaultWhere : w;
	}

	protected static Where extractWhere(Object... args) {
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj instanceof Where) {
				return (Where) obj;
			}
		}
		return null;
	}

	protected static ElementStub extractRelElem(Object... args) {
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj instanceof WebElement) {
				return ((WebElement) obj).getElementStub();
			} else if (obj instanceof ElementStub) {
				return (ElementStub) obj;
			}
		}
		return null;
	}

	protected static Browser extractBrowser(Object... args) {
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj instanceof Browser) {
				return (Browser) obj;
			}
		}
		return null;
	}

	protected static Object[] extractStateValues(Object[] args) {
		List<Object> stateValues = new ArrayList<Object>();
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj != null && !(obj instanceof WebElement)
					&& !(obj instanceof Where) && !(obj instanceof ElementStub)
					&& !(obj instanceof Browser)) {
				stateValues.add(obj);
			}
		}

		if (stateValues.size() > 0) {
			return stateValues.toArray();
		} else {
			return new Object[] {};
		}
	}

	/**
	 * Locate a dialogue according to the specified dialogue title.
	 * 
	 * @param title
	 *            If the title string format is '/..../', it is handled as a
	 *            regular expression, otherwise as a plain text.
	 * @param args
	 * @return
	 */
	private static List<EvoDialog> locateDlgs(String title, Object... args) {

		List<EvoDialog> dlgs = new ArrayList<EvoDialog>();
		boolean useRegExp = false;
		Pattern p = null;
		if (title.startsWith("/") && title.endsWith("/")) {
			useRegExp = true;
			title = title.substring(1, title.length() - 1);
			p = Pattern.compile(title);
		}
		/*
		 * At the beginning, I used collectSimilar and countSimilar methods to
		 * get all the candidates but these two methods are not reliable so I
		 * changed to use index directly.
		 */
		ElementStub es = null;
		// Assuming there won't be more than 10 dialogues in the page
		for (int i = 0; i < 10; i++) {
			es = getBrowser(args).div("dijitDialogTitleBar[" + i + "]");
			if (!es.exists()) {
				break;
			}
			if (es.isVisible()) {
				String acDlgTitle = getBrowser(args).span("dijitDialogTitle")
						.in(es).getText();
				if ((useRegExp && p.matcher(acDlgTitle).find())
						|| (!useRegExp && acDlgTitle.equals(title))) {
					es = es.parentNode();
					if (es != null && es.exists() && es.isVisible()) {
						dlgs.add(new EvoDialog(es, extractStateValues(args)));
					}
				}
			} else {
				continue;
			}
		}

		return dlgs;
	}

	/**
	 * Locate a dialogue according to the specified dialogue title. Note that
	 * this method is only used for generic dialogue, but not progress task
	 * dialogue.
	 * 
	 * To locate a progress task dialogue, use the method
	 * <code>progressDlg</code>.
	 * 
	 * @param title
	 *            If the title string format is '/..../', it is handled as a
	 *            regular expression, otherwise as a plain text.
	 * @param args
	 * @return an <code>EvoDialog</code> instance.
	 */
	public static EvoDialog dlg(final String title, final Object... args) {
		
		final String dlgTitle=title.startsWith("/")&&title.endsWith("/")?title:"'"+title+"'";
		
		final int maxDlgCount = 5;
		for (int i = 0; i < maxDlgCount; i++) {
			ElementStub dlgEs = getBrowser()
					.accessor(
							"_sahi._span({sahiText:"
									+ dlgTitle
									+ ",className:'dijitDialogTitle',sahiIndex:"
									+ i + "})").parentNode().parentNode();
			if (dlgEs != null && dlgEs.exists()) {
				if (dlgEs.isVisible() && !isProgressDlg(dlgEs)) {
					return new EvoDialog(dlgEs);
				}
			} else {
				break;
			}
		}

		throw new DialogNotFoundException("The dialog with the title [" + title
				+ "] was not found!");

	}

	/**
	 * 
	 * Locate a progress task dialogue.
	 * 
	 * @param dlgTitle
	 * @param args
	 * @return
	 */
	public static EvoProgressDialog progressDlg(String dlgTitle) {
		int triedCount = 1;

		while (triedCount <= MAX_TRY_COUNT) {
			if (dlgTitle != null) {
				final int maxDlgCount = 5;
				for (int i = 0; i < maxDlgCount; i++) {
					ElementStub dlgEs = getBrowser()
							.accessor(
									"_sahi._span({sahiText:'"
											+ dlgTitle
											+ "',className:'dijitDialogTitle',sahiIndex:"
											+ i + "})").parentNode()
							.parentNode();
					if (dlgEs != null && dlgEs.exists()) {
						if (dlgEs.isVisible() && isProgressDlg(dlgEs)) {
							return new EvoProgressDialog(dlgEs);
						}
					} else {
						break;
					}
				}
			} else {
				EvoProgressDialog dlg = locateProgressDlg();
				if (dlg != null) {
					return dlg;
				}
			}
			getBrowser().waitFor(5000);
			triedCount++;
		}

		throw new DialogNotFoundException(
				"Failed to locate the progress task dialogue with the title ["
						+ dlgTitle + "] after trying " + triedCount + " times.");
	}

	private static boolean isProgressDlg(ElementStub dlgEs) {
		String className = dlgEs.getAttribute("className");
		for (int i = 0; i < PROGRESS_DLG_CLASS.length; i++) {
			String progressDlgClass = PROGRESS_DLG_CLASS[i];
			if (className.indexOf(progressDlgClass) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If the progress dialogue title is not specified, use this method
	 * 
	 * @param args
	 * @return
	 */
	private static EvoProgressDialog locateProgressDlg() {
		List<ElementStub> titleBars = getBrowser().div("dijitDialogTitleBar")
				.collectSimilar();
		for (Iterator<ElementStub> it = titleBars.iterator(); it.hasNext();) {
			ElementStub es = (ElementStub) it.next();
			if (!es.exists()) {
				continue;
			}
			EvoDialog dlg = (EvoDialog) widget(es, EvoDialog.class);
			if (dlg.isVisible() && isProgressDlg(dlg.getElementStub())) {
				return new EvoProgressDialog(dlg.getElementStub());
			}
		}
		return null;
	}

	public static EvoMenu findActiveEvoMenu(Object... args) {
		final ElementStub es = getBrowser(args).table("/.*dijitMenuActive/");
		BrowserCondition cond = new BrowserCondition(getBrowser(args)) {
			public boolean test() throws ExecutionException {
				return es.exists(false);
			}
		};

		getBrowser(args).waitFor(cond, 30000);
		return new EvoMenu(es, extractStateValues(args));
	}

	public static Menu findActiveMenu(Object... args) {
		final ElementStub es = getBrowser(args).div("/.*dijitMenuActive/");
		BrowserCondition cond = new BrowserCondition(getBrowser(args)) {
			public boolean test() throws ExecutionException {
				return es.exists(false);
			}
		};

		getBrowser(args).waitFor(cond, 30000);
		return new Menu(es, extractStateValues(args));
	}

	public static int count(Class<? extends DojoWidget> dojoClazz,
			Object... args) {
		RegEntry regEntry = REGISTRY.getRegEntry(dojoClazz.getCanonicalName());

		String widgetElemType = regEntry.getWidgetElemType();
		String widgetIdRE = formWidgetIdRE(regEntry.getIdentifiers(), dojoClazz);
		String countExpr = "_sahi._count('_" + widgetElemType + "','"
				+ widgetIdRE + "')";
		if (args.length > 0) {
			ElementStub relElem = extractRelElem(args);
			Where rel = extractWhere(Where.IN, args);
			countExpr = "_sahi._count('_" + widgetElemType + "','" + widgetIdRE
					+ "',_sahi." + rel.sahiFuncName() + "(" + relElem + "))";
		}

		int count = Integer.parseInt(getBrowser(args).fetch(countExpr));
		return count;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DojoWidget> List<T> collect(
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		final int count = count(dojoClazz, args);
		List<T> l = new ArrayList<T>();

		for (int i = 0; i < count; i++) {
			T w = (T) byIndex(i, dojoClazz, args);
			l.add(w);
		}

		return l;
	}

	/**
	 * Caution: this method should be only used to static widget id.
	 * 
	 * @param widgetId
	 *            A static widget id value
	 * @return
	 */
	public static <T extends DojoWidget> T byWidgetId(String widgetId) {
		String widgetIdPrefix = widgetId;
		final int lastUnderscorePos = widgetId.lastIndexOf("_");
		if (widgetId.contains("_")
				&& widgetId.substring(lastUnderscorePos + 1).matches("[0-9]+")) {
			widgetIdPrefix = widgetId.substring(0, lastUnderscorePos);
		}
		RegEntry re = REGISTRY.getRegEntryByWidgetIdPrefix(widgetIdPrefix);
		ElementStub es = getBrowser().accessor(
				"_sahi._" + re.getWidgetElemType() + "({widgetid:'" + widgetId
						+ "'})");
		return widget(es, re.getWidgetClazz());
	}

	/**
	 * Find widgets by index.
	 * 
	 * @param index
	 *            Starting from zero.
	 * @param dojoClazz
	 * @param args
	 * @return
	 */
	public static <T extends DojoWidget> T byIndex(int index,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		ElementStub relElem = extractRelElem(args);
		Where rel = extractWhere(Where.IN, args);

		RegEntry regEntry = REGISTRY.getRegEntry(dojoClazz.getCanonicalName());
		String identifier = formWidgetIndentifier(regEntry.getIdentifiers(),
				dojoClazz, index);

		String widgetElemType = regEntry.getWidgetElemType();
		ElementStub es = createES(widgetElemType, identifier);

		return widget(bind(es, relElem, rel), dojoClazz);
	}

	/**
	 * 
	 * @param htmlWidget
	 * @param dojoClazz
	 * @param stateValues
	 * @return
	 */
	public static <T extends DojoWidget> T widget(WebElement htmlWidget,
			Class<? extends DojoWidget> dojoClazz, Object... stateValues) {
		return widget(htmlWidget.getElementStub(), dojoClazz);
	}

	/**
	 * This is a generic method to find a dojo widget. Before invoking this
	 * method, you need to construct ElementStub object yourself.
	 * 
	 * @param es
	 * @param dojoClazz
	 * @param initStateValues
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DojoWidget> T widget(ElementStub es,
			Class<? extends DojoWidget> dojoClazz) {
		es = getBrowser().accessor(
				es.toString().replace("\"{", "{").replace("}\"", "}"));
		if (es == null || !es.exists()) {
			throw new ObjectNotFoundException("The ElementStub object [" + es
					+ "] does not exist!!");
		}
		try {
			return (T) dojoClazz.getConstructor(ElementStub.class,
					Object[].class).newInstance(es, null);
		} catch (Exception e) {
			throw new AutomationException(
					"Failed to create the dojo widget class ["
							+ dojoClazz.getCanonicalName() + "]", e);
		}
	}

	/**
	 * Find widget by its core element's attribute(s).
	 * 
	 * @param identifer
	 *            This could be a single attribute value or an array of
	 *            attributes. <br>
	 *            Example: <br>
	 *            1) Single attribute: "messageBox" <br>
	 *            2) An array of attributes:
	 *            "{id:'messageBox',className:'messagesBox'}"
	 * @param dojoClazz
	 * @param args
	 * @return
	 */
	public static <T extends DojoWidget> T byAttribute(String identifer,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		ElementStub relElem = extractRelElem(args);
		Where rel = extractWhere(Where.IN, args);

		RegEntry regEntry = REGISTRY.getRegEntry(dojoClazz.getCanonicalName());
		String elemType = regEntry.getCoreElemType();

		ElementStub es = createES(elemType, identifer);

		return widget(bind(es, relElem, rel), dojoClazz);
	}

	protected static String formWidgetIdRE(List<String> prefixes,
			Class<? extends DojoWidget> dojoClazz) {
		StringBuffer sb = new StringBuffer("/");
		int len = prefixes.size();
		for (int i = 0; i < len; i++) {
			String prefix = prefixes.get(i);
			if (dojoClazz.isInstance(Textbox.class)) {
				sb.append("widget_");
			}
			sb.append(prefix.replace("/",".*")).append(".*");
			if (i != len - 1) {
				sb.append("|");
			}
		}
		sb.append("/");
		String re = sb.toString();
		return re;
	}

	private static String formWidgetIndentifier(List<String> prefixes,
			Class<? extends DojoWidget> dojoClazz, int sahiIndex) {
		final String re = formWidgetIdRE(prefixes, dojoClazz);
		return "{widgetid:'" + re + "',sahiIndex:" + sahiIndex + "}";
	}

	public static <T extends DojoWidget> T byLabel(WebElement labelElem,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		ElementStub label = labelElem.getElementStub();
		RegEntry regEntry = REGISTRY.getRegEntry(dojoClazz.getCanonicalName());
		String identifier = formWidgetIndentifier(regEntry.getIdentifiers(),
				dojoClazz, 0);

		final String widgetElemType = regEntry.getWidgetElemType();
		final String labelPlacement = regEntry.getLabelPlacement();
		
		ElementStub es = createES(widgetElemType, identifier);

		/*
		 * If the user specified relation type is passed in, use it;otherwise
		 * use the system defined relation.
		 */
		Where userDefinedRel = extractWhere(args);
		if (userDefinedRel != null) {
			es = bind(es, label, userDefinedRel);
		} else {
			switch (labelPlacement) {
			case RegEntry.LABEL_INSIDE:
				es = label;
				break;
			case RegEntry.LABEL_NEAR:
				es = es.near(label);
				break;
			case RegEntry.LABEL_OUTSIDE:
				es = es.in(label);
				break;
			case RegEntry.LABEL_NONE:
				throw new AutomationException(
						dojoClazz.getCanonicalName()
								+ " does not support byLabel method because its label placement propery value is 'none'.");
			default:
				throw new AutomationException("The label placement ["
						+ labelPlacement + "] is unknown.");
			}
		}

		return widget(es, dojoClazz);
	}

	/**
	 * Find a Dojo Widget by the label text provided
	 * 
	 * @param labelText
	 * @param dojoClazz
	 * @param args
	 * @return
	 */
	public static <T extends DojoWidget> T byLabel(String labelText,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		ElementStub label = findLabelElem(labelText, dojoClazz, args);
		return byLabel(new WebElement(label), dojoClazz, args);
	}

	/**
	 * Find a HTML element based on the text provided.
	 * 
	 * @param labelText
	 *            support regular expression, e.g. "\/test.*\/"
	 * @param args
	 * @return
	 */
	private static ElementStub findLabelElem(String labelText,
			Class<? extends DojoWidget> dojoClazz, Object... args) {
		ElementStub label = null;

		ElementStub relElem = extractRelElem(args);
		Where rel = extractWhere(Where.IN, args);

		if (dojoClazz != Button.class) {
			labelText = WBStringUtils.toRegExp(labelText);
		}

		final Browser browser = getBrowser();

		if (dojoClazz == Button.class) {
			label = bind(browser.span(labelText), relElem, rel);
		} else {
			label = bind(browser.label(labelText), relElem, rel);
			if (!label.exists()) {
				label = bind(browser.span(labelText), relElem, rel);
				if (!label.exists()) {
					label = bind(browser.div(labelText), relElem, rel);
					if (!label.exists()) {
						throw new ObjectNotFoundException(
								"Cannot find the label with the text '"
										+ labelText + "'");
					}
				}
			}
		}

		return label;
	}

	/**
	 * Find a general HTML element
	 * 
	 * @param identifer
	 *            This could be a single attribute value or an array of
	 *            attributes. <br>
	 *            Example: <br>
	 *            1) Single attribute: "messageBox" <br>
	 *            2) An array of attributes:
	 *            "{id:'messageBox',className:'messagesBox'}"
	 * @param elemType
	 * @param args
	 * @return
	 */
	public static WebElement webElem(Object identifier, String elemType,
			Object... args) {
		ElementStub relElem = extractRelElem(args);
		Where rel = extractWhere(Where.IN, args);

		String identifierStr = identifier.toString();
		ElementStub es = null;
		if (identifierStr.startsWith("{") && identifierStr.endsWith("}")) {
			identifierStr = identifierStr.replaceAll("\"", "'");
			es = getBrowser(args).accessor(
					"_sahi._" + elemType + "(" + identifierStr + ")");
		} else {
			es = createES(elemType, identifier);
		}

		return new WebElement(bind(es, relElem, rel));
	}

	/**
	 * Return a ElementStub object based on the element type and the identifer.
	 * 
	 * @param elemType
	 * @param identifier
	 * @return
	 */
	protected static ElementStub createES(String elemType, Object identifier) {
		ElementStub es = null;
		try {
			Browser b = getBrowser();
			Object o = Array.newInstance(Object.class, 1);
			Array.set(o, 0, identifier);
			es = (ElementStub) b.getClass().getMethod(elemType, Object[].class)
					.invoke(b, o);
		} catch (Exception e) {
			throw new AutomationException(
					"Failed to create ElementStub object from elemType["
							+ elemType + "] and identifier[" + identifier + "]",
					e);
		}

		return es;
	}

}
