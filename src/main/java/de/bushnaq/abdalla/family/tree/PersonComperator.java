package de.bushnaq.abdalla.family.tree;

import java.util.Comparator;

public class PersonComperator implements Comparator<Person> {

	@Override
	public int compare(Person o1, Person o2) {
		int compare = o1.born.compareTo(o2.born);
		if (compare != 0) {
			return compare;
		}
		return o1.firstName.compareToIgnoreCase(o2.firstName);
	}

}
