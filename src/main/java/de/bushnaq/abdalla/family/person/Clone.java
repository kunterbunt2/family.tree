package de.bushnaq.abdalla.family.person;

/**
 * Used to represent a female person in the tree beside her sexual partner.<br>
 * All clones are marked with * as such in the tree.<br>
 * The original person is still displayed as sibling of her parents.
 *
 * @author abdalla
 */
public class Clone extends DrawablePerson {
    final Person original;
    CloneType cloneType;

    public Clone(PersonList personList, Person person, CloneType cloneType) {
        super(personList, (DrawablePerson) person, ((DrawablePerson) person).getBackgroundColor());
        setFather(null);// we are just a clone
        setMother(null);// we are just a clone
        original = person;
        this.cloneType = cloneType;
        if (cloneType.equals(CloneType.pagination))
            setVisible(true);
    }

    public static Clone createPaginationClone(PersonList personList, Person person) {
        Clone clone = new Clone(personList, person, CloneType.pagination);
        return clone;
    }

    public static Clone createSpouseClone(PersonList personList, Person person) {
        Clone clone = new Clone(personList, person, CloneType.spouse);
        return clone;
    }

    @Override
    public Integer getGeneration() {
        return original.getGeneration();
    }

    public Person getOriginal() {
        return original;
    }

    public Person getOriginalFather() {
        return original.getFather();
    }

    public boolean isPaginationClone() {
        return cloneType == CloneType.pagination;
    }

    public boolean isSpouseClone() {
        return cloneType == CloneType.spouse;
    }

    @Override
    public void setGeneration(Integer generation) {
        original.setGeneration(generation);
    }
}
