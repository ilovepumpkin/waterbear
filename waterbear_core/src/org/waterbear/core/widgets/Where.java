package org.waterbear.core.widgets;

public class Where {
	private String value;

	public String getValue() {
		return value;
	}

	public static final Where IN = new Where("in");
	public static final Where NEAR = new Where("near");
	public static final Where UNDER = new Where("under");

	public Where(String relation) {
		this.value = relation;
	}

	public boolean equals(Where rel) {
		return rel.getValue().equals(getValue()) ? true : false;
	}

	public String sahiFuncName() {
		return "_" + value;
	}
}
