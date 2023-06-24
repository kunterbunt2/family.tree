package de.bushnaq.abdalla.family.person;

import de.bushnaq.abdalla.family.Context;

import java.util.*;

public class PersonList extends LinkedList
        <Person> {

    //    final List<Person> list = new ArrayList<>();
    Set<Person> set = new HashSet<Person>();

//    public PersonList()
//    {
//        super(new PersonComparator());
//    }

    @Override
    public boolean add(Person p) {
        boolean r2 = set.add(p);
        boolean r1 = false;
        if (r2)
            r1 = super.add(p);
        return r1 & r2;
    }

//    public Iterator<Person> descendingIterator() {
//        return  this. descendingIterator();
//    }

    public int findMaxGeneration() {//TODO cache value
        int maxGenration = -1;

        for (Person p : this) {
            if (p.getGeneration() != null)
                maxGenration = Math.max(maxGenration, p.getGeneration());
        }
        return maxGenration;
    }

    //	public void calculateWidths(Graphics2D graphics, Font nameFont, Font livedFont) {
//		for (Person p : this) {
//			p.calculateWidth(graphics, nameFont, livedFont);
//		}
//	}
    public List<Person> findRootFatherList(Context context) {
        List<Person> rootFatherList = new ArrayList<>();
        for (Person p : this) {
            if (p.isRootFather(context)) {
                rootFatherList.add(p);
            }
        }
        return rootFatherList;
    }

//    public Person get(int index) {
//        return list.get(index);
//    }

//    public Person last() {
//        return get(size()-1);
//    }

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
