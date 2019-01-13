package org.focus;

import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<String> {
	Map<String, Float> base;

	public ValueComparator(Map<String, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.
	public int compare(String a, String b) {
		float va = base.get(a);
		float vb = base.get(b);
		if (va > vb) {
			return -1;
		} else if (va == vb) {
			return a.compareTo(b);
		} else {
			return 1;
		}
		// returning 0 would merge keys
	}
}
