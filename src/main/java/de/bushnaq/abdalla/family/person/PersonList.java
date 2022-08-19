package de.bushnaq.abdalla.family.person;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.TreeSet;

public class PersonList extends TreeSet<Person> {

	public PersonList() {
		super(new PersonComperator());
	}

	public void calculateWidths(Graphics2D graphics, Font nameFont, Font livedFont) {
		for (Person p : this) {
			p.calculateWidth(graphics, nameFont, livedFont);
		}
	}

	public int getHeight() {
		int	minY	= Integer.MAX_VALUE;
		int	maxY	= Integer.MIN_VALUE;
		for (Person p : this) {
			minY = Math.min(minY, p.y);
			maxY = Math.max(maxY, p.y);
		}
		return (int) (maxY + Person.PERSON_HEIGHT);
	}

	public int getWidth() {
		int	minX	= Integer.MAX_VALUE;
		int	maxX	= Integer.MIN_VALUE;
		for (Person p : this) {
			minX = Math.min(minX, (p.x));
			maxX = Math.max(maxX, (int) (p.x + p.width));
		}
		return maxX;
	}

	public void printPersonList() {
		for (Person p : this) {
			p.print();
		}
	}

}
