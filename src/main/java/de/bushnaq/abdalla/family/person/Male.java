package de.bushnaq.abdalla.family.person;

import java.awt.Color;

public class Male extends DrawablePerson {

	private static final Color BACKGROUND_COLOR = new Color(0x2d, 0xb1, 0xff, 32);

	public Male(PersonList personList, Integer id) {
		super(personList, id, BACKGROUND_COLOR);
	}

	public Male(PersonList personList, Male male) {
		super(personList, male, BACKGROUND_COLOR);
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
