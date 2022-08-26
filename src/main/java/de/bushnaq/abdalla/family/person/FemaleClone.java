package de.bushnaq.abdalla.family.person;

import java.util.Date;

public class FemaleClone extends Female {

	public FemaleClone(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		super(personList, id, firstName, lastName, born, died, father, mother);
	}

}
