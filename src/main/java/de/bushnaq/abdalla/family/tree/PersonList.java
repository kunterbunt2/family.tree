package de.bushnaq.abdalla.family.tree;

import java.awt.Graphics2D;
import java.util.Calendar;
import java.util.TreeSet;

public class PersonList extends TreeSet<Person> {

	public PersonList() {
		super(new PersonComperator());
	}

	Female addFemale(String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		Female female = new Female(this, firstName, lastName, born, died, father, mother);
		add(female);
		generateIds();
		return female;
	}

	Male addMale(String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		Male male = new Male(this, firstName, lastName, born, died, father, mother);
		add(male);
		generateIds();
		return male;
	}

	public int calculateMaxNameWidth(Graphics2D graphics) {
		int w = Integer.MIN_VALUE;
		for (Person p : this) {
			int tw = graphics.getFontMetrics().stringWidth(p.getName());
			w = Math.max(w, tw);
		}
		return w;
	}

	private void generateIds() {
//		Collections.sort(this, new Comparator<Person>() {
//			@Override
//			public int compare(final Person o1, final Person o2) {
//				int compare = o1.born.compareTo(o2.born);
//				if (compare != 0) {
//					return compare;
//				}
//				return o1.firstName.compareToIgnoreCase(o2.firstName);
//			}
//		});
		int id = 0;
		for (Person p : this) {
			p.id = id++;
		}
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
			minX = Math.min(minX, p.x);
			maxX = Math.max(maxX, p.x);
		}
		return maxX + Person.personWidth;
	}

	public void printPersonList() {
		for (Person p : this) {
			p.print();
		}
	}

}
