package org.waterbear.core.utils.asserter;

import java.util.Arrays;

public class TestStrategies {

	/**
	 * Test if the target text contains each of the specified texts.
	 */
	public static TextTestStrategy CONTAINS = (target, args) -> {
		return Arrays.asList(args).stream()
				.allMatch((arg) -> target.contains(arg));
	};

	/**
	 * Test if the target text equals to the specified text (the first one).
	 */
	public static TextTestStrategy EQUALS = (target, args) -> {
		return target.equals(args[0]);
	};

	/**
	 * Test if the target text is found in the specified text collection.
	 */
	public static TextTestStrategy IN = (target, args) -> {
		return Arrays.asList(args).contains(target);
	};

	public static TextTestStrategy STARTS_WITH = (target, args) -> {
		return target.startsWith(args[0]);
	};

	public static TestStrategy<Boolean> TRUE = (target, args) -> {
		return Boolean.compare(target, true) == 0;
	};
}
