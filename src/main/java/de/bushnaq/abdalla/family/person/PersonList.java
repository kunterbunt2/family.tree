package de.bushnaq.abdalla.family.person;

import de.bushnaq.abdalla.family.Context;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class PersonList extends LinkedList
        <Person> {

    Set<Person> set = new HashSet<Person>();

    @Override
    public boolean add(Person p) {
        boolean r2 = set.add(p);
        boolean r1 = false;
        if (r2)
            r1 = super.add(p);
        return r1 & r2;
    }

    public void clearVisited() {
        for (Person p : this) {
            p.setVisited(false);
        }
    }

    public int findMaxGeneration() {//TODO cache value
        int maxGenration = -1;

        for (Person p : this) {
            if (p.getGeneration() != null)
                maxGenration = Math.max(maxGenration, p.getGeneration());
        }
        return maxGenration;
    }

    public Person findPersonById(int filter) {
        for (Person person : this) {
            if (person.getId() == filter)
                return person;
        }
        return null;
    }

    /**
     * Find head of every subtree in the list.
     * A subtree head is a person that was moved together with its subtree to a new page to distribute the total tree on more than one page
     *
     * @param context
     * @return
     */
    public PersonList findTreeHeadList(Context context) {
        PersonList treeHeadList = new PersonList();
        for (Person person : findTreeRootList(context)) {
            treeHeadList.addAll(findTreeHeadList(context, person));
        }

//        for (Person p : this) {
//            if (p.isTreeRoot(context) || p.isPaginationClone()) {
//                treeHeadList.add(p);
//            }
//        }
        return treeHeadList;
    }

    public PersonList findTreeHeadList(Context context, Person person) {
        PersonList treeHeadList = new PersonList();
        Person p = person;
        Person paginationClone = person.findPaginationClone();
        if (paginationClone != null) {
            p = paginationClone;
        } else {
        }
        if (p.isTreeRoot(context) || p.isPaginationClone()) {
            treeHeadList.add(p);
        }
        for (Person child : p.getChildrenList()) {
            treeHeadList.addAll(findTreeHeadList(context, child));
        }
        return treeHeadList;
    }

    /**
     * Find root of every tree in the list. A tree root is the first father/mother of a family.
     *
     * @param context
     * @return
     */
    public PersonList findTreeRootList(Context context) {
        PersonList treeRootList = new PersonList();
        for (Person p : this) {
            if (p.isTreeRoot(context)) {
                treeRootList.add(p);
            }
        }
        return treeRootList;
    }

    public int getMaxPageIndex() {
        int maxPageIndex = -1;
        for (Person p : this) {
            if (p.getPageIndex() != null)
                maxPageIndex = Math.max(maxPageIndex, p.getPageIndex());
        }
        return maxPageIndex;
    }

    public int getNumberOfIndividualPeople() {
        int count = 0;
        for (Person p : this) {
            if (!p.isClone())
                count++;
        }
        return count;
    }

//    public void printPersonList(Context context) {
//        for (Person p : this) {
//            p.print(context);
//        }
//    }

    public Rect getTreeRect(int pageIndex) {
        Rect rect = null;

        for (Person p : this) {
            if (p.getPageIndex() != null && p.getPageIndex() == pageIndex) {
                if (rect == null)
                    rect = new Rect(p.getX(), p.getY(), p.getX(), p.getY());
                else
                    rect.expandToInclude(new Rect(p.getX(), p.getY(), p.getX(), p.getY()));
            }
        }

        return rect;
    }

    public void reset() {
        for (Person p : this) {
            p.reset();
        }
    }

}
