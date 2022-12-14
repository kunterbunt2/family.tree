package de.bushnaq.abdalla.family.person;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.util.FlexibleDate;

public class BasicFamilyMember {
	private FlexibleDate	born;
	private FlexibleDate	died;
	private Male			father;
	private String			firstName;
	private String			firstNameOriginalLanguage;
	private int				id;
	private String			lastName;
	private String			lastNameOriginalLanguage;
	private Female			mother;

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

	public FlexibleDate getDied() {
		return died;
	}

	public Male getFather() {
		return father;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getFirstNameOriginalLanguage() {
		return firstNameOriginalLanguage;
	}

	public int getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getLastNameOriginalLanguage() {
		return lastNameOriginalLanguage;
	}

	public Female getMother() {
		return mother;
	}

	public void setBorn(FlexibleDate born) {
		this.born = born;
	}

	public void setDied(FlexibleDate died) {
		this.died = died;
	}

	public void setFather(Male father) {
		this.father = father;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setFirstNameOriginalLanguage(String firstNameOriginalLanguage) {
		this.firstNameOriginalLanguage = firstNameOriginalLanguage;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setLastNameOriginalLanguage(String lastNameOriginalLanguage) {
		this.lastNameOriginalLanguage = lastNameOriginalLanguage;
	}

	public void setMother(Female mother) {
		this.mother = mother;
	}

	public String toString(Context context) {
		return toString();
	}

}
