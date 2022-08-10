package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class Person {
	public static final int		PERSON_HEIGHT		= 50;
	public static final int		PERSON_MARGINE		= 2;
	public static final int		PERSON_BORDER		= 1;
	public static final int		PERSON_X_SPACE		= 50;
	public static final int		PERSON_Y_SPACE		= 25;
//	public static int			personWidth			= 400;
	public Date					born				= null;
	public Date					died				= null;
	public Male					father;
	public String				firstName;
	public int					id;
	public String				lastName;
	public Female				mother;
	public PersonList			personList;
	public boolean				positionSet			= false;
	private SimpleDateFormat	simpleDateFormat	= new SimpleDateFormat("yyyy-MM-dd");
	public int					x					= 0;
	public int					y					= 0;
	public int					width;
	private Color				backgroundColor;

	public Person(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother, Color backgroundColor) {
		this.personList = personList;
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.born = born;
		this.died = died;
		this.father = father;
		this.mother = mother;
		this.backgroundColor = backgroundColor;
	}

	public void draw(Graphics2D graphics) {
		int	mapX1	= x;
		int	mapX2	= x + width;
		int	mapY1	= y;
		int	mapY2	= y + PERSON_HEIGHT;
		graphics.setColor(backgroundColor);
		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
		graphics.setColor(Color.black);
		graphics.drawRect(mapX1, mapY1, mapX2 - mapX1 - 1, mapY2 - mapY1 - 1);
		int h = graphics.getFontMetrics().getHeight();
		{
			String	string	= getName();
			int		w		= graphics.getFontMetrics().stringWidth(string);
			graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + h / 2 - h / 2);
		}
		{
			String	string	= getLivedString();
			int		w		= graphics.getFontMetrics().stringWidth(string);
			graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + h / 2 + h / 2);
		}
	}

	private String getBornString() {
		if (born != null)
			return "\u002A" + simpleDateFormat.format(born);
		return "?";
	}

	private String getDiedString() {
		if (died != null)
			return "\u271D" + simpleDateFormat.format(died);
		return "?";
	}

	private String getLivedString() {
		return String.format("%s-%s", getBornString(), getDiedString());
	}

	public String getName() {
		return String.format("%s%s %s", getSexCharacter(), firstName, lastName);
	}

	public abstract String getSexCharacter();

	public List<Female> getWifeList() {
		List<Female> wifeList = new ArrayList<>();
		for (Person p : personList) {
			if (p.father != null && p.father.equals(this))
				wifeList.add(p.mother);
		}
		return wifeList;
	}

	public boolean hasChildren() {
		for (Person p : personList) {
			if ((p.father != null && p.father.equals(this)) || (p.mother != null && p.mother.equals(this)))
				return true;
		}
		return false;
	}

	public abstract boolean isFemale();

	public abstract boolean isMale();

	public void print() {
		System.out.println(String.format("%d %s %s %d %d", id, getName(), getLivedString(), x, y));
	}

	public void calculateWidth(Graphics2D graphics) {
		width = Math.max(graphics.getFontMetrics().stringWidth(getName()), graphics.getFontMetrics().stringWidth(getLivedString())) /* + Person.PERSON_MARGINE * 2 */ + Person.PERSON_BORDER;
	}

}
