package de.bushnaq.abdalla.family.person;

import java.awt.Color;
import java.util.Date;

public class Female extends DrawablePerson {

	public Female(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		super(personList, id, firstName, lastName, born, died, father, mother, new Color(0xff, 0x62, 0xb0, 32));
	}

	public Female(PersonList personList, Integer id) {
		super(personList, id, new Color(0xff, 0x62, 0xb0, 32));
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
