package de.bushnaq.abdalla.family.person;

import java.util.Date;

public class BasicFamilyMember {
	public Date		born	= null;
	public Date		died	= null;
	public Male		father;
	public String	firstName;
	public int		id;
	public String	lastName;
	public Female	mother;

	public BasicFamilyMember(int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.born = born;
		this.died = died;
		this.father = father;
		this.mother = mother;
	}

}
