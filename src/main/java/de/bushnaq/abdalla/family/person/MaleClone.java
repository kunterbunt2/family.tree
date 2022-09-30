package de.bushnaq.abdalla.family.person;

public class MaleClone extends Male {

	public MaleClone(PersonList personList, Male male) {
		super(personList, male);
		setFather(null);// we are just a clone
		setMother(null);// we are just a clone
	}

}
