package de.bushnaq.abdalla.family.person;

/**
 * Used to represent a female person as root of tree on a new page.<br>
 * All clones are marked with * as such in the tree.<br>
 * The original person is still displayed as sibling of her parents.
 *
 * @author abdalla
 */
public class FemalePaginationClone extends Female {
    final Female original;

    public FemalePaginationClone(PersonList personList, Female female) {
        super(personList, female);
        setFather(null);// we are just a clone
        setMother(null);// we are just a clone
        original = female;
    }

    @Override
    public Integer getGeneration() {
        return original.getGeneration();
    }

    public Person getOriginal() {
        return original;
    }

    //public Person getOriginalFather() {
    //return original.getFather();
    //}

    @Override
    public boolean isPaginationClone() {
        return true;
    }

    @Override
    public void setGeneration(Integer generation) {
        original.setGeneration(generation);
    }

//	@Override
//	public PersonList getDescendantList(int targetGeneration) {
//		PersonList decendantList = new PersonList();
//
//		for (Person person : original.getChildrenList()) {
//			if (person.getGeneration() == targetGeneration)
//				decendantList.add(person);
//			else if (person.getGeneration() < targetGeneration)
//				decendantList.addAll(person.getDescendantList(targetGeneration));
//		}
//
//		return decendantList;
//	}

}
