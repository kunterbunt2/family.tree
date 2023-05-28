package de.bushnaq.abdalla.family.person;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import de.bushnaq.abdalla.family.Context;

public class PersonList extends TreeSet<Person> {

	List<Person> list = new ArrayList<>();

	public PersonList() {
		super(new PersonComperator());
	}

//	public void calculateWidths(Graphics2D graphics, Font nameFont, Font livedFont) {
//		for (Person p : this) {
//			p.calculateWidth(graphics, nameFont, livedFont);
//		}
//	}

	@Override
	public boolean add(Person p) {
		boolean	r1	= super.add(p);
		boolean	r2	= list.add(p);
		return r1 & r2;
	}

	public Person get(int index) {
		return list.get(index);
	}

	public void printPersonList(Context context) {
		for (Person p : this) {
			p.print(context);
		}
	}

	public void reset() {
		for (Person p : this) {
			p.reset();
		}
	}

}
