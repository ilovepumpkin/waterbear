package org.waterbear.core.utils.asserter;

import java.util.Arrays;

public interface TextTestStrategy extends TestStrategy<String> {
	default TestStrategy<String> ignoreCase() {
		return (t, args) -> test(t.toLowerCase(), Arrays.asList(args).stream()
				.map((s) -> s.toLowerCase()).toArray(String[]::new));
	};
}
