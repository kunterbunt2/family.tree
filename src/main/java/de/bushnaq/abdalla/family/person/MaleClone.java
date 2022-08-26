package de.bushnaq.abdalla.family.person;

import java.util.Date;

public class MaleClone extends Male {

	public MaleClone(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		super(personList, id, firstName, lastName, born, died, father, mother);
	}

}
