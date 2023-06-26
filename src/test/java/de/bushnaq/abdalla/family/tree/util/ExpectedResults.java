package de.bushnaq.abdalla.family.tree.util;

import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;

import java.util.ArrayList;

public class ExpectedResults extends ArrayList<ExpectedResult> {
    public ExpectedResults(PersonList personList) {
        for (Person person : personList) {
            add(new ExpectedResult(person.getId(), person.getX(), person.getY(), person.getPageIndex()));
        }

    }
}
