package de.bushnaq.abdalla.family.tree;

import java.awt.Graphics2D;
import java.util.TreeSet;

public class PersonList extends TreeSet<Person> {

	public PersonList() {
		super(new PersonComperator());
	}

	public int getHeight() {
		int	minY	= Integer.MAX_VALUE;
		int	maxY	= Integer.MIN_VALUE;
		for (Person p : this) {
			minY = Math.min(minY, p.y);
			maxY = Math.max(maxY, p.y);
		}
		return maxY + Person.PERSON_HEIGHT;
	}

	public int getWidth() {
		int	minX	= Integer.MAX_VALUE;
		int	maxX	= Integer.MIN_VALUE;
		for (Person p : this) {
			minX = Math.min(minX, p.x - p.width / 2);
			maxX = Math.max(maxX, p.x + p.width / 2);
		}
		return maxX;
	}

	public void printPersonList() {
		for (Person p : this) {
			p.print();
		}
	}

	public void calculateWidths(Graphics2D graphics) {
		for (Person p : this) {
			p.calculateWidth(graphics);
		}
	}

}
