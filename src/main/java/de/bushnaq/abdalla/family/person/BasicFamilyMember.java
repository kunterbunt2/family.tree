package de.bushnaq.abdalla.family.person;

import java.util.Date;

public class BasicFamilyMember {
	private Date	born	= null;
	private Date	died	= null;
	private Male	father;
	private String	firstName;
	private String	firstNameOriginalLanguage;
	public String getFirstNameOriginalLanguage() {
		return firstNameOriginalLanguage;
	}

	public void setFirstNameOriginalLanguage(String firstNameOriginalLanguage) {
		this.firstNameOriginalLanguage = firstNameOriginalLanguage;
	}

	public String getLastNameOriginalLanguage() {
		return lastNameOriginalLanguage;
	}

	public void setLastNameOriginalLanguage(String lastNameOriginalLanguage) {
		this.lastNameOriginalLanguage = lastNameOriginalLanguage;
	}

	private int		id;
	private String	lastName;
	private String	lastNameOriginalLanguage;
	private Female	mother;

	public BasicFamilyMember(int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		this.id = id;
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setBorn(born);
		this.setDied(died);
		this.setFather(father);
		this.setMother(mother);
	}

	public BasicFamilyMember(Integer id) {
		this.id = id;
	}

	public Date getBorn() {
		return born;
	}

	public Date getDied() {
		return died;
	}

	public Male getFather() {
		return father;
	}

	public String getFirstName() {
		return firstName;
	}

	public int getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public Female getMother() {
		return mother;
	}

	public void setBorn(Date born) {
		this.born = born;
	}

	public void setDied(Date died) {
		this.died = died;
	}

	public void setFather(Male father) {
		this.father = father;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMother(Female mother) {
		this.mother = mother;
	}

}
