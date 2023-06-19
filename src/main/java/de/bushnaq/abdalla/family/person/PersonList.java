package de.bushnaq.abdalla.family.person;

import de.bushnaq.abdalla.family.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PersonList extends TreeSet<Person> {

    final List<Person> list = new ArrayList<>();

    public PersonList() {
        super(new PersonComperator());
    }

    @Override
    public boolean add(Person p) {
        boolean r1 = super.add(p);
        boolean r2 = list.add(p);
        return r1 & r2;
    }

    //	public void calculateWidths(Graphics2D graphics, Font nameFont, Font livedFont) {
//		for (Person p : this) {
//			p.calculateWidth(graphics, nameFont, livedFont);
//		}
//	}
    public int findMaxgeneration() {//TODO cache value
        int maxGenration = -1;

        for (Person p : this) {
            if (p.getGeneration() != null)
                maxGenration = Math.max(maxGenration, p.getGeneration());
        }
        return maxGenration;
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
