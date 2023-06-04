package de.bushnaq.abdalla.family.person;

/**
 * Used to represent a female person in the tree beside her sexual partner.<br>
 * All clones are marked with * as such in the tree.<br>
 * The original person is still displayed as sibling of her parents.
 *
 * @author abdalla
 *
 */
public class FemaleClone extends Female {
	Female original;

	public FemaleClone(PersonList personList, Female female) {
		super(personList, female);
		setFather(null);// we are just a clone
		setMother(null);// we are just a clone
		original = female;
	}

	@Override
	public Integer getGeneration() {
		return original.getGeneration();
	}

	public Person getOriginalFather() {
		return original.getFather();
	}

	@Override
	public void setGeneration(Integer generation) {
		original.setGeneration(generation);
	}

}
