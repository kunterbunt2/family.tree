package de.bushnaq.abdalla.family.person;

import java.util.Comparator;

public class PersonComperator implements Comparator<Person> {

	@Override
	public int compare(Person o1, Person o2) {
		if (o1.born != null && o2.born != null) {
			int compare = o1.born.compareTo(o2.born);
			if (compare != 0) {
				return compare;
			}
		}
		return o1.id - o2.id;
	}

}