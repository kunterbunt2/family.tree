package de.bushnaq.abdalla.family.person;

/**
 * Used to represent a female person in the tree beside her sexual partner.<br>
 * All clones are marked with * as such in the tree.<br>
 * The original person is still displayed as sibling of her parents.
 *
 * @author abdalla
 */
public class FemaleSpouseClone extends Female {
    Female original;

    public FemaleSpouseClone(PersonList personList, Female female) {
        super(personList, female);
        setFather(null);// we are just a clone
        setMother(null);// we are just a clone
        original = female;
    }

    @Override
    public Integer getGeneration() {
        return original.getGeneration();
    }

    @Override
    public void setGeneration(Integer generation) {
        original.setGeneration(generation);
    }

    public Person getOriginalFather() {
        return original.getFather();
    }

}
