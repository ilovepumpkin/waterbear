package org.waterbear.projects.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.waterbear.core.exception.AutomationException;

public class RandomCollection<T> implements Cloneable {

	private List<T> itemList;

	public RandomCollection(T... items) {
		itemList = new ArrayList<T>(Arrays.asList(items));
	}

	public RandomCollection(List<T> list) {
		itemList = new ArrayList<T>(list);
	}

	public T pickUp() {
		if (itemList.size() == 0) {
			throw new AutomationException("No items!");
		}
		return itemList.get(new Random().nextInt(itemList.size()));
	}

	public List<T> pickUp(int count) {
		if (count > itemList.size()) {
			throw new AutomationException("[" + count + "] is out of range ["
					+ itemList.size() + "]");
		}

		RandomCollection<T> c = null;
		c = this.clone();

		List<T> results = new ArrayList<T>();
		for (int i = 0; i < count; i++) {
			T item = c.pickUp();
			results.add(item);
			c.exclude(item);
		}
		return results;
	}

	public RandomCollection<T> exclude(T excluded) {
		itemList.remove(excluded);
		return this;
	}

	public RandomCollection<T> exclude(T... excluded) {
		itemList.removeAll(Arrays.asList(excluded));
		return this;
	}

	public RandomCollection<T> exclude(Collection<T> excluded) {
		itemList.removeAll(excluded);
		return this;
	}

	@Override
	public String toString() {
		return "RandomCollection [itemList=" + itemList + "]";
	}

	public RandomCollection<T> clone() {
		RandomCollection<T> o = new RandomCollection<T>(itemList);
		return o;
	}
}
