package de.bushnaq.abdalla.family.person;

import java.awt.Color;
import java.util.Date;

public class Male extends Person {

	public Male(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		super(personList, id, firstName, lastName, born, died, father, mother, new Color(0x2d, 0xb1, 0xff, 64));
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
