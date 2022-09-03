package de.bushnaq.abdalla.family.person;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.TreeSet;

import de.bushnaq.abdalla.family.Context;

public class PersonList extends TreeSet<Person> {

	public PersonList() {
		super(new PersonComperator());
	}

	public void calculateWidths(Graphics2D graphics, Font nameFont, Font livedFont) {
		for (Person p : this) {
			p.calculateWidth(graphics, nameFont, livedFont);
		}
	}

	public void printPersonList(Context context) {
		for (Person p : this) {
			p.print(context);
		}
	}

}
