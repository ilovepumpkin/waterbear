package org.waterbear.core.widgets.dijit.validators;

import org.waterbear.core.widgets.WebElement;

@FunctionalInterface
public interface IValidator {
	public void validate(WebElement widget);
}
