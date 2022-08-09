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
	public static int			personWidth			= 400;
	public List<String>			additionalNames		= new ArrayList<>();
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

	public Person(PersonList personList, String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		this.personList = personList;
		this.firstName = firstName;
		this.lastName = lastName;
		this.born = born.getTime();
		if (died != null)
			this.died = died.getTime();
		this.father = father;
		this.mother = mother;
	}

	public void addAdditionalName(String additionalName) {
		additionalNames.add(additionalName);
	}

	public void draw(Graphics2D graphics) {
		Color color = Color.white;
		graphics.setColor(color);
		int	mapX1	= personWidth / 2 + x - personWidth / 2 + PERSON_MARGINE;
		int	mapX2	= personWidth / 2 + x + personWidth / 2 - PERSON_MARGINE;
		int	mapY1	= PERSON_HEIGHT / 2 + y - PERSON_HEIGHT / 2 + PERSON_MARGINE;
		int	mapY2	= PERSON_HEIGHT / 2 + y + PERSON_HEIGHT / 2 - PERSON_MARGINE;
		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
		color = Color.black;
		graphics.setColor(color);
		int h = graphics.getFontMetrics().getHeight();
		{
			String	string	= getName();
			int		w		= graphics.getFontMetrics().stringWidth(string);
			graphics.drawString(string, x + personWidth / 2 - w / 2, y + PERSON_HEIGHT / 2 + h / 2 - h / 2);
		}
		{
			String	string	= getLivedString();
			int		w		= graphics.getFontMetrics().stringWidth(string);
			graphics.drawString(string, x + personWidth / 2 - w / 2, y + PERSON_HEIGHT / 2 + h / 2 + h / 2);
		}
	}

	public String getAdditionalNames() {
		String names = "";
		for (String an : additionalNames) {
			names += an;
			names += " ";
		}
		return names;
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
		return String.format("%s%s %s%s", getSexCharacter(), firstName, getAdditionalNames(), lastName);
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
		System.out.println(String.format("%s %s %d %d", getName(), getLivedString(), x, y));
	}

}
