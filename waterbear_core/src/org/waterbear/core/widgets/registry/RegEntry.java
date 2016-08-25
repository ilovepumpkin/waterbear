package org.waterbear.core.widgets.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.widgets.ET;
import org.waterbear.core.widgets.dijit.DojoWidget;

public class RegEntry {
	private Class<? extends DojoWidget> widgetClazz;
	private String classQName;
	/**
	 * Widget Element Type is the type of the element enclosing the widget.
	 */
	private String widgetElemType;
	/**
	 * There is always an element inside the widget DOM tree (or itself) which
	 * id attribute value is same to the widgetid attribute, like
	 * 'dijit_form_ValidationTextBox_0'; This property stands for it element
	 * type. We call this element Core Element of the widget.
	 */
	private String coreElemType;

	/**
	 * Define the mapped dojo widget prefixes to this widget. For example, for
	 * TextBox.java, it could returns
	 * "new String[]{"dijit_form_ValidationTextBox",
	 * "evo_form_ObjectNameTextBox" }". That means all the dojo widgets in web
	 * page whose widgetid starts with 'dijit_form_ValidationTextBox' or
	 * 'evo_form_ObjectNameTextBox' are mapped to TextBox.java.
	 */
	private List<String> identifiers = new ArrayList<String>();

	/**
	 * Define the label placement to the widget if it has. The default value is
	 * NEAR.
	 */
	private String labelPlacement = LABEL_NEAR;

	public static final String LABEL_INSIDE = "labelinside";
	public static final String LABEL_OUTSIDE = "labeloutside";
	public static final String LABEL_NEAR = "labelnear";
	public static final String LABEL_NONE = "none";

	private static final List<String> VALID_ELEM_TYPES;
	static {
		VALID_ELEM_TYPES = new ArrayList<String>();
		VALID_ELEM_TYPES.add(ET.BOLD);
		VALID_ELEM_TYPES.add(ET.CELL);
		VALID_ELEM_TYPES.add(ET.CHECKBOX);
		VALID_ELEM_TYPES.add(ET.DIV);
		VALID_ELEM_TYPES.add(ET.FILE);
		VALID_ELEM_TYPES.add(ET.HEADING1);
		VALID_ELEM_TYPES.add(ET.HEADING2);
		VALID_ELEM_TYPES.add(ET.HEADING3);
		VALID_ELEM_TYPES.add(ET.HIDDEN);
		VALID_ELEM_TYPES.add(ET.IMAGE);
		VALID_ELEM_TYPES.add(ET.LINK);
		VALID_ELEM_TYPES.add(ET.PASSWORD);
		VALID_ELEM_TYPES.add(ET.RADIO);
		VALID_ELEM_TYPES.add(ET.SPAN);
		VALID_ELEM_TYPES.add(ET.TABLE);
		VALID_ELEM_TYPES.add(ET.TEXTAREA);
		VALID_ELEM_TYPES.add(ET.TEXTBOX);
		VALID_ELEM_TYPES.add(ET.LI);
		VALID_ELEM_TYPES.add(ET.ROW);
	}

	private static final List<String> VALID_LABEL_PLACEMENTS;
	static {
		VALID_LABEL_PLACEMENTS = new ArrayList<String>();
		VALID_LABEL_PLACEMENTS.add(LABEL_INSIDE);
		VALID_LABEL_PLACEMENTS.add(LABEL_OUTSIDE);
		VALID_LABEL_PLACEMENTS.add(LABEL_NEAR);
		VALID_LABEL_PLACEMENTS.add(LABEL_NONE);
	}

	public RegEntry(String[] identifiers, String widgetElemType,
			String coreElemType, String labelPlacement, String classQName) {
		super();
		if (!VALID_ELEM_TYPES.contains(widgetElemType)) {
			throw new AutomationException("Invalid widget element type ["
					+ widgetElemType + "]!");
		}
		if (!VALID_ELEM_TYPES.contains(coreElemType)) {
			throw new AutomationException("Invalid core element type ["
					+ coreElemType + "]!");
		}
		if (!VALID_LABEL_PLACEMENTS.contains(labelPlacement)) {
			throw new AutomationException("Invalid label placement ["
					+ coreElemType + "]!");
		}
		this.widgetElemType = widgetElemType;
		this.coreElemType = coreElemType;
		this.identifiers.addAll(Arrays.asList(identifiers));
		this.labelPlacement = labelPlacement;
		this.classQName = classQName;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends DojoWidget> getWidgetClazz() {
		if (widgetClazz == null) {
			try {
				widgetClazz = (Class<? extends DojoWidget>) Class
						.forName(this.classQName);
			} catch (ClassNotFoundException e) {
				throw new AutomationException(e);
			}
		}
		return widgetClazz;
	}

	public String getClassQName() {
		return classQName;
	}

	public String getWidgetElemType() {
		return widgetElemType;
	}

	public String getCoreElemType() {
		return coreElemType;
	}

	public List<String> getIdentifiers() {
		return identifiers;
	}

	public String getLabelPlacement() {
		return labelPlacement;
	}

	public void addIdentifiers(String[] identifiers) {
		this.identifiers.addAll(Arrays.asList(identifiers));
	}
}
