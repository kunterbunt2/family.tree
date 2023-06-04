package de.bushnaq.abdalla.family.person;

/**
 * Used to represent a male person in the tree beside his sexual partner.<br>
 * All clones are marked with * as such in the tree.<br>
 * The original person is still displayed as sibling of his parents.
 *
 * @author abdalla
 *
 */
public class MaleClone extends Male {
	Male original;

	public MaleClone(PersonList personList, Male male) {
		super(personList, male);
		setFather(null);// we are just a clone
		setMother(null);// we are just a clone
		original = male;
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
