package de.bushnaq.abdalla.family.person;

import java.awt.Font;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.bushnaq.abdalla.family.Context;

public abstract class Person extends BasicFamilyMember {
	public static final int		PERSON_BORDER	= 1;
	public static final float	PERSON_HEIGHT	= 50;
	public static final int		PERSON_MARGINE	= 4;
	public static final int		PERSON_X_SPACE	= 24;
	public static final int		PERSON_Y_SPACE	= 12;
	public Attribute			attribute		= new Attribute();
	public int					idWidth			= 0;
	public int					nextPersonX;
	public PersonList			personList;
	public float				width;
	public int					x				= 0;
	public int					y				= 0;

	public Person(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother) {
		super(id, firstName, lastName, born, died, father, mother);
		this.personList = personList;
	}

	public boolean bornBefore(Person person) {
		if (born != null && person.born != null) {
			return born.before(person.born);
		}
		return id < person.id;
	}

	public void calculateWidth(Graphics2D graphics, Font nameFont, Font livedFont) {
		graphics.setFont(livedFont);
		idWidth = graphics.getFontMetrics().stringWidth("" + id);

		width = 0;
		graphics.setFont(nameFont);
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getFirstName()));
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getLastName()));
		graphics.setFont(livedFont);
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getBornString()));
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getDiedString()));
		width += Person.PERSON_MARGINE * 2 + Person.PERSON_BORDER * 2 + idWidth;
	}

	public abstract void drawHorizontal(Context context, Graphics2D graphics, Font nameFont, Font livedFont);

	public String getBornString() {
		if (born != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return "\u002A" + simpleDateFormat.format(born);
		}
		return "";
	}

	public PersonList getChildrenList() {
		PersonList	childrenList	= new PersonList();
		PersonList	spouseList		= getSpouseList();
		for (Person spouse : spouseList) {
			for (Person child : personList) {
				if (child.father != null && child.mother != null) {
					if ((child.father.equals(this) && child.mother.equals(spouse)) || (child.father.equals(spouse) && child.mother.equals(this))) {
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
			if (child.father != null && child.mother != null) {
				if ((child.father.equals(this) && child.mother.equals(spouse)) || (child.father.equals(spouse) && child.mother.equals(this))) {
					childrenList.add(child);
				}
			}
		}
		return childrenList;
	}

	protected String getDiedString() {
		if (died != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return "\u271D" + simpleDateFormat.format(died);
		}
		return "";
	}

	public String getFirstName() {
		return String.format("%s", firstName);
	}

	public String getLastName() {
		if (lastName != null)
			return String.format("%s", lastName);
		else
			return "";
	}

	protected String getLivedString() {
		if (died != null)
			return String.format("%s   -   %s", getBornString(), getDiedString());
		else
			return String.format("%s   -   %s", getBornString(), getBornString());
	}

	public String getName() {
		return String.format("%s %s", firstName, lastName);
	}

	public abstract String getSexCharacter();

	public PersonList getSpouseList() {
		PersonList spouseList = new PersonList();
		for (Person p : personList) {
			if (p.father != null && p.father.equals(this))
				spouseList.add(p.mother);
			if (p.father != null && p.mother.equals(this))
				spouseList.add(p.father);
		}
		return spouseList;
	}

	public boolean hasChildren() {
		for (Person p : personList) {
			if ((p.father != null && p.father.equals(this)) || (p.mother != null && p.mother.equals(this)))
				return true;
		}
		return false;
	}

	public boolean isChild() {
		return attribute.child;
	}

	public abstract boolean isFemale();

	public boolean isFirstChild() {
		return attribute.firstChild;
	}

	public boolean isLastChild() {
		return attribute.lastChild;
	}

	public abstract boolean isMale();

	public boolean isSpouse() {
		return attribute.spouse;
	}

	public boolean isSpouseOfLastChild() {
		return attribute.spouseOfLastChild;
	}

	public void print() {
		System.out.println(toString());
	}

	public void setFirstChild(boolean firstChild) {
		this.attribute.firstChild = firstChild;
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

}
