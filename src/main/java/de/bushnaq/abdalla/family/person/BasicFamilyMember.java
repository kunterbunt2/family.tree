package de.bushnaq.abdalla.family.person;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.util.FlexibleDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicFamilyMember {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private FlexibleDate born;
    private FlexibleDate died;
    private Male father;
    private String firstName;
    private String firstNameOriginalLanguage;
    private int id;
    private String lastName;
    private String lastNameOriginalLanguage;
    private Female mother;

    public BasicFamilyMember(BasicFamilyMember member) {
        this.id = member.id;
        this.setFirstName(member.firstName);
        this.setFirstNameOriginalLanguage(member.firstNameOriginalLanguage);
        this.setLastName(member.lastName);
        this.setLastNameOriginalLanguage(member.lastNameOriginalLanguage);
        this.setBorn(member.born);
        this.setDied(member.died);
        this.setFather(member.father);
        this.setMother(member.mother);
    }

    public BasicFamilyMember(Integer id) {
        this.id = id;
    }

    public FlexibleDate getBorn() {
        return born;
    }

    public void setBorn(FlexibleDate born) {
        this.born = born;
    }

    public FlexibleDate getDied() {
        return died;
    }

    public void setDied(FlexibleDate died) {
        this.died = died;
    }

    public Male getFather() {
        return father;
    }

    public void setFather(Male father) {
        this.father = father;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstNameOriginalLanguage() {
        return firstNameOriginalLanguage;
    }

    public void setFirstNameOriginalLanguage(String firstNameOriginalLanguage) {
        this.firstNameOriginalLanguage = firstNameOriginalLanguage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastNameOriginalLanguage() {
        return lastNameOriginalLanguage;
    }

    public void setLastNameOriginalLanguage(String lastNameOriginalLanguage) {

        this.lastNameOriginalLanguage = lastNameOriginalLanguage;
    }

    public Female getMother() {
        return mother;
    }

    public void setMother(Female mother) {
        this.mother = mother;
    }

    public String toString(Context context) {
        return toString();
    }

}
