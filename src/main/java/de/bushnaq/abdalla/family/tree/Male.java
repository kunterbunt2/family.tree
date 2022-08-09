package de.bushnaq.abdalla.family.tree;

import java.util.Calendar;

public class Male extends Person {

	public Male(PersonList personList, String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		super(personList, firstName, lastName, born, died, father, mother);
	}

	@Override
	public String getSexCharacter() {
		return "\u2642 ";
	}

	@Override
	public boolean isFemale() {
		return false;
	}

	@Override
	public boolean isMale() {
		return true;
	}

}
