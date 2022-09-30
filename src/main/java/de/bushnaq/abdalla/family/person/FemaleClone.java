package de.bushnaq.abdalla.family.person;

public class FemaleClone extends Female {

	public FemaleClone(PersonList personList, Female female) {
		super(personList, female);
		setFather(null);// we are just a clone
		setMother(null);// we are just a clone
	}

}
