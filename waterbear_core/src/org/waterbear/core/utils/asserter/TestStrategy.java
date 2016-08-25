package org.waterbear.core.utils.asserter;

import java.util.Objects;

@FunctionalInterface
public interface TestStrategy<T> {

	boolean test(T t, @SuppressWarnings("unchecked") T... args);

	default TestStrategy<T> negate() {
		return (t, args) -> !test(t, args);
	};

	default TestStrategy<T> and(TestStrategy<T> other) {
		Objects.requireNonNull(other);
		return (t, args) -> test(t, args) && other.test(t, args);
	};

	default TestStrategy<T> or(TestStrategy<T> other) {
		Objects.requireNonNull(other);
		return (t, args) -> test(t, args) || other.test(t, args);
	};

}
