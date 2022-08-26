package de.bushnaq.abdalla.family.person;

import java.util.Comparator;

public class PersonComperator implements Comparator<Person> {

	@Override
	public int compare(Person o1, Person o2) {
		if (o1.getBorn() != null && o2.getBorn() != null) {
			int compare = o1.getBorn().compareTo(o2.getBorn());
			if (compare != 0) {
				return compare;
			}
		}
		if (o1.isClone() && !o2.isClone()) {
			return o1.getId() + 1000 - o2.getId();
		}
		if (!o1.isClone() && o2.isClone()) {
			return o1.getId() - o2.getId() - 1000;
		}

		return o1.getId() - o2.getId();
	}

}
