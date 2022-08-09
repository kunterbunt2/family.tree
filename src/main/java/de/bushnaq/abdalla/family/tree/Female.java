package de.bushnaq.abdalla.family.tree;

import java.util.Calendar;

public class Female extends Person {

	public Female(PersonList personList, String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		super(personList, firstName, lastName, born, died, father, mother);
	}

	@Override
	public String getSexCharacter() {
		return "\u2640 ";
	}

	@Override
	public boolean isFemale() {
		return true;
	}

	@Override
	public boolean isMale() {
		return false;
	}

}
