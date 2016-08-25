package org.waterbear.core.utils;

@FunctionalInterface
public interface WaitForCondition {
	public boolean test();
}
