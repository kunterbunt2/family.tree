package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Person {
	public static final int		PERSON_BORDER		= 1;
	public static final float	PERSON_HEIGHT		= 50;
	public static final int		PERSON_MARGINE		= 5;
	public static final int		PERSON_X_SPACE		= 25;
	public static final int		PERSON_Y_SPACE		= 13;
	private Color				backgroundColor;
	private Color				borderColoe			= new Color(0, 0, 0, 64);
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
	private Color				textColoe			= new Color(0, 0, 0);
	public float				width;
	public int					x					= 0;
	public int					y					= 0;

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

	public void calculateWidth(Graphics2D graphics) {
		width = 0;
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getFirstName()));
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getLastName()));
		width = Math.max(width, graphics.getFontMetrics().stringWidth(getLivedString()));
		width += Person.PERSON_MARGINE * 2 + Person.PERSON_BORDER;
	}

	public void draw(Graphics2D graphics) {
		int	mapX1	= x;
		int	mapX2	= (int) (x + width);
		int	mapY1	= y;
		int	mapY2	= (int) (y + PERSON_HEIGHT);
		graphics.setColor(backgroundColor);
		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
		graphics.setColor(borderColoe);
		graphics.drawRect(mapX1, mapY1, mapX2 - mapX1 - 1, mapY2 - mapY1 - 1);
		graphics.setColor(textColoe);
		FontRenderContext	frc		= graphics.getFontRenderContext();
		Font				font	= graphics.getFont();
		{
			String		string			= getFirstName();
			LineMetrics	metrics			= font.getLineMetrics(string, frc);
			float		descent			= metrics.getDescent();
			Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
			float		w				= (float) stringBounds.getWidth();
			float		h				= (float) stringBounds.getHeight();
			graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent - h);
		}
		{
			String		string			= getLastName();
			LineMetrics	metrics			= font.getLineMetrics(string, frc);
			float		descent			= metrics.getDescent();
			Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
			float		w				= (float) stringBounds.getWidth();
			float		h				= (float) stringBounds.getHeight();
			graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent);
		}
		{
			String		string			= getLivedString();
			LineMetrics	metrics			= font.getLineMetrics(string, frc);
			float		descent			= metrics.getDescent();
			Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
			float		w				= (float) stringBounds.getWidth();
			float		h				= (float) stringBounds.getHeight();
			graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
		}
	}

	private String getBornString() {
		if (born != null)
			return "\u002A" + simpleDateFormat.format(born);
		return "?";
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

	private String getDiedString() {
		if (died != null)
			return "\u271D" + simpleDateFormat.format(died);
		return "?";
	}

	public String getFirstName() {
		return String.format("%s", firstName);
	}

	public String getLastName() {
		return String.format("%s", lastName);
	}

	private String getLivedString() {
		return String.format("%s-%s", getBornString(), getDiedString());
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

	public abstract boolean isFemale();

	public abstract boolean isMale();

	public void print() {
		System.out.println(toString());
	}

	@Override
	public String toString() {
		return String.format("%d %s %s %d %d", id, getName(), getLivedString(), x, y);
	}

}
