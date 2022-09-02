package de.bushnaq.abdalla.family.person;

import java.awt.Font;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.bushnaq.abdalla.family.Context;

public abstract class Person extends BasicFamilyMember {
	public static final int	PERSON_BORDER	= 1;
	public static final int	PERSON_HEIGHT	= 50;
	public static final int	PERSON_MARGINE	= 4;
	public static final int	PERSON_WIDTH	= 100;
	public static final int	PERSON_X_SPACE	= 24;
	public static final int	PERSON_Y_SPACE	= 12;
	private Attribute		attribute		= new Attribute();
	public Integer			childIndex		= null;
	public Integer			generation		= null;
	public int				height			= Person.PERSON_HEIGHT;
	public int				idWidth			= 0;
	public Integer			nextPersonX		= -1;
	public Integer			nextPersonY		= -1;
	public PersonList		personList		= null;
	public Integer			spouseIndex		= null;
	public int				width			= 0;
	public int				x				= 0;
	public int				y				= 0;

	public Person(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		super(id, firstName, lastName, born, died, father, mother);
		this.personList = personList;
	}

	public Person(PersonList personList, Integer id) {
		super(id);
		this.personList = personList;
	}

	public boolean bornBefore(Person person) {
		if (getBorn() != null && person.getBorn() != null) {
			return getBorn().before(person.getBorn());
		}
		return getId() < person.getId();
	}

	public void calculateWidth(Graphics2D graphics, Font nameFont, Font livedFont) {
		graphics.setFont(livedFont);
		idWidth = graphics.getFontMetrics().stringWidth("" + getId());

//		width = 0;
//		graphics.setFont(nameFont);
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getFirstName()));
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getLastName()));
//		graphics.setFont(livedFont);
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getBornString()));
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getDiedString()));
//		width += Person.PERSON_MARGINE * 2 + Person.PERSON_BORDER * 2 + idWidth;
		width = PERSON_WIDTH;
	}

	public abstract void drawHorizontal(Context context, Graphics2D graphics, Font nameFont, Font livedFont);

	public abstract void drawVertical(Context context, Graphics2D graphics, Font nameFont, Font livedFont);

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Person))
			return false;
		Person other = (Person) o;
		if ((other.isClone() && !this.isClone()) || (!other.isClone() && this.isClone()))
			return false;
		return this.getId() == other.getId();
	}

	public String getBornString() {
		if (getBorn() != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return "\u002A" + simpleDateFormat.format(getBorn());
		}
		return "";
	}

	public PersonList getChildrenList() {
		PersonList	childrenList	= new PersonList();
		PersonList	spouseList		= getSpouseList();
		for (Person spouse : spouseList) {
			for (Person child : personList) {
				if (child.getFather() != null && child.getMother() != null) {
					if ((child.getFather().equals(this) && child.getMother().equals(spouse)) || (child.getFather().equals(spouse) && child.getMother().equals(this))) {
						childrenList.add(child);
					}
				}
			}
		}
		return childrenList;
	}

	public PersonList getChildrenList(Person spouse) {
		PersonList childrenList = new PersonList();
		for (Person child : personList) {
			if (child.getFather() != null && child.getMother() != null) {
				if ((child.getFather().equals(this) && child.getMother().equals(spouse)) || (child.getFather().equals(spouse) && child.getMother().equals(this))) {
					childrenList.add(child);
				}
			}
		}
		return childrenList;
	}

	protected String getDiedString() {
		if (getDied() != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return "\u271D" + simpleDateFormat.format(getDied());
		}
		return "";
	}

	public String getFirstNameAsString(Context context) {
		String name = getFirstName();
		if (context.getParameterOptions().isOriginalLanguage()) {
			if (getFirstNameOriginalLanguage() != null)
				name = getFirstNameOriginalLanguage();
		}
		return String.format("%s", name);
	}

	public String getLastNameAsString(Context context) {
		String name = getLastName();
		if (context.getParameterOptions().isOriginalLanguage()) {
			if (getLastNameOriginalLanguage() != null)
				name = getLastNameOriginalLanguage();
		}
		if (name != null)
			return String.format("%s", name);
		else
			return "";
	}

	protected String getLivedString() {
		if (getDied() != null)
			return String.format("%s   -   %s", getBornString(), getDiedString());
		else
			return String.format("%s   -   %s", getBornString(), getBornString());
	}

	public String getName(Context context) {
		return String.format("%s %s", getFirstNameAsString(context), getLastNameAsString(context));
	}

	public abstract String getSexCharacter();

	public PersonList getSpouseList() {
		PersonList spouseList = new PersonList();
		for (Person p : personList) {
			if (p.getFather() != null && p.getFather().equals(this))
				spouseList.add(p.getMother());
			if (p.getMother() != null && p.getMother().equals(this))
				spouseList.add(p.getFather());
		}
		return spouseList;
	}

	public boolean hasChildren() {
		for (Person p : personList) {
			if ((p.getFather() != null && p.getFather().equals(this)) || (p.getMother() != null && p.getMother().equals(this)))
				return true;
		}
		return false;
	}

	public boolean isChild() {
		return attribute.child;
	}

	public boolean isClone() {
		return (this instanceof FemaleClone) || (this instanceof MaleClone);
	}

	public abstract boolean isFemale();

	public boolean isFirstChild() {
		return attribute.firstChild;
	}

	public boolean isFirstFather() {
		return attribute.firstFather;
	}

	public boolean isLastChild() {
		return attribute.lastChild;
	}

	public abstract boolean isMale();

	public boolean isMember() {
		return getFather() != null || getMother() != null || isFirstFather() || (this instanceof FemaleClone) || (this instanceof MaleClone);
	}

	public boolean isSpouse() {
		return attribute.spouse;
	}

	public boolean isSpouseOfLastChild() {
		return attribute.spouseOfLastChild;
	}

	public boolean isVisible() {
		return attribute.visible;
	}

	public void print() {
		System.out.println(toString());
	}

	public void setFirstChild(boolean firstChild) {
		this.attribute.firstChild = firstChild;
	}

	public void setFirstFather(boolean firstFather) {
		attribute.firstFather = firstFather;
	}

	public void setIsChild(boolean child) {
		this.attribute.child = child;
	}

	public void setLastChild(boolean lastChild) {
		this.attribute.lastChild = lastChild;
	}

	public void setSpouse(boolean spouse) {
		this.attribute.spouse = spouse;
	}

	public void setSpouseOfLastChild(boolean spouseOfLastChild) {
		this.attribute.spouseOfLastChild = spouseOfLastChild;
	}

	public void setVisible(boolean child) {
		this.attribute.visible = child;
	}

}
