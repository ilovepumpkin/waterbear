package org.waterbear.core.utils.asserter;

import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;

public class WBAsserter {

	public static void assertText(TestStrategy<String> ts, String target,
			String... args) {
		boolean result = ts.test(target, args);
		assertTrue("Assertion failed. Target text [" + target
				+ "] , Argument texts [" + Arrays.asList(args) + "]", result);
	}

	public static void assertBoolean(TestStrategy<Boolean> ts, Boolean target) {
		boolean result = ts.test(target);
		assertTrue("Assertion failed. Target boolean value [" + target + "]",
				result);
	}
}
