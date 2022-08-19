package de.bushnaq.abdalla.family.person;

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
	public static final int		PERSON_MARGINE		= 4;
	public static final int		PERSON_X_SPACE		= 24;
	public static final int		PERSON_Y_SPACE		= 12;
	private Color				backgroundColor;
	private Color				borderColoe			= new Color(0, 0, 0, 64);
	public Date					born				= null;
	private boolean				child				= false;								// child of a member of the family
	public Date					died				= null;
	public Male					father;
	private boolean				firstChild			= false;								// first child born of a sexual relation
	public String				firstName;
	public int					id;
	public int					idWidth				= 0;
	private boolean				lastChild			= false;								// last child born of a sexual relation
	public String				lastName;
	public Female				mother;
	public int					nextPersonX;
	public PersonList			personList;
	public boolean				positionSet			= false;
	private SimpleDateFormat	simpleDateFormat	= new SimpleDateFormat("yyyy-MM-dd");
	private boolean				spouse				= false;								// spouse of member of family
	private boolean				spouseOfLastChild	= false;								// spouse of the last child of this branch of the family, used by algorithm to decide where to draw the line for children
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

	private void drawBox(Graphics2D graphics, Font nameFont, Font livedFont, int mapX1, int mapX2, int mapY1, int mapY2) {
		graphics.setColor(backgroundColor);
		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
		graphics.setColor(borderColoe);
		graphics.drawRect(mapX1, mapY1, mapX2 - mapX1 - 1, mapY2 - mapY1 - 1);
		graphics.setColor(textColoe);
		{
			graphics.setFont(nameFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// first name
			{
				String		string			= getFirstName();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent - h);
			}
			// last name
			{
				String		string			= getLastName();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent);
			}
		}
		{
			// ID
			graphics.setFont(livedFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			{
				String		string			= "" + id;
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + 2, y + metrics.getHeight());
			}
		}
		{
			graphics.setFont(livedFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// born
			{
				String		string			= getBornString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
			}
//			{
//				String		string			= "-";
//				LineMetrics	metrics			= font.getLineMetrics(string, frc);
//				float		descent			= metrics.getDescent();
//				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
//				float		w				= (float) stringBounds.getWidth();
//				float		h				= (float) stringBounds.getHeight();
//				graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
//			}
			// died
			{
				String		string			= getDiedString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h + h);
			}
		}
	}

	private void drawConnectors(Graphics2D graphics, int mapX1, int mapX2, int mapY1, int mapY2) {
		// only child and no children
		if (isFirstChild() && isLastChild() && !hasChildren()) {
			Person spouse = mother;
			if (father.x > mother.x)
				spouse = father;
			int	px	= spouse.x;
			int	pw	= (int) spouse.width;
			// vertical connector
			graphics.fillRect(px + (pw) / 2, mapY1 - PERSON_Y_SPACE, 1, PERSON_Y_SPACE);
		} else {
			// first child
			if (isFirstChild()) {
				Person spouse = mother;
				if (father.x > mother.x)
					spouse = father;
				int	px	= spouse.x;
				int	pw	= (int) spouse.width;
				int	uw	= mapX2 - mapX1;
				graphics.setColor(Color.black);
				// horizontal connector
				graphics.fillRect(px + (pw) / 2, mapY1 - PERSON_Y_SPACE / 2, (mapX2 - mapX1) / 2 + 1 + PERSON_X_SPACE / 2, 1);
				// vertical connector
				graphics.fillRect(px + (pw) / 2, mapY1 - PERSON_Y_SPACE, 1, PERSON_Y_SPACE);
			}
			// last child
			else if (isLastChild() && !hasChildren()) {
				graphics.setColor(Color.black);
				graphics.fillRect(mapX1 - PERSON_X_SPACE / 2, mapY1 - PERSON_Y_SPACE / 2, (mapX2 - mapX1) / 2 + PERSON_X_SPACE / 2, 1);
			}
		}
		// all the children in between
		if (!isFirstChild() && (!isLastChild() || hasChildren()) && !isSpouseOfLastChild()) {
			graphics.setColor(Color.black);
//			graphics.fillRect(mapX1 - PERSON_X_SPACE / 2, mapY1 - PERSON_Y_SPACE / 2,nextPersonX -(mapX1 - PERSON_X_SPACE / 2), 1);
			graphics.fillRect(mapX1 - PERSON_X_SPACE / 2, mapY1 - PERSON_Y_SPACE / 2, mapX2 - mapX1 + PERSON_X_SPACE, 1);
		}
		if (isSpouseOfLastChild()) {
			graphics.setColor(Color.black);
			graphics.fillRect(mapX1 - PERSON_X_SPACE / 2, mapY1 - PERSON_Y_SPACE / 2, (mapX2 - mapX1) / 2 + PERSON_X_SPACE / 2, 1);
		}

		// vertical connector
		if (!isFirstChild() && !isSpouse()) {
			graphics.fillRect(mapX1 + (mapX2 - mapX1) / 2, mapY1 - PERSON_Y_SPACE / 2, 1, PERSON_Y_SPACE / 2);
		}

		// sexual relation connector
		if (hasChildren() && isChild()) {
			graphics.setColor(Color.black);
			graphics.fillRect(mapX2, mapY1 + (mapY2 - mapY1) / 2, PERSON_X_SPACE, 1);
		}
	}

	public void drawHorizontal(Graphics2D graphics, Font nameFont, Font livedFont) {
		int	mapX1	= x;
		int	mapX2	= (int) (x + width);
		int	mapY1	= y;
		int	mapY2	= (int) (y + PERSON_HEIGHT);
		drawBox(graphics, nameFont, livedFont, mapX1, mapX2, mapY1, mapY2);
		drawConnectors(graphics, mapX1, mapX2, mapY1, mapY2);
	}

	public String getBornString() {
		if (born != null)
			return "\u002A" + simpleDateFormat.format(born);
		return "";
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
		return "";
	}

	public String getFirstName() {
		return String.format("%s", firstName);
	}

	public String getLastName() {
		return String.format("%s", lastName);
	}

	private String getLivedString() {
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
		return child;
	}

	public abstract boolean isFemale();

	public boolean isFirstChild() {
		return firstChild;
	}

	public boolean isLastChild() {
		return lastChild;
	}

	public abstract boolean isMale();

	public boolean isSpouse() {
		return spouse;
	}

	public boolean isSpouseOfLastChild() {
		return spouseOfLastChild;
	}

	public void print() {
		System.out.println(toString());
	}

	public void setFirstChild(boolean firstChild) {
		this.firstChild = firstChild;
	}

	public void setIsChild(boolean child) {
		this.child = child;
	}

	public void setLastChild(boolean lastChild) {
		this.lastChild = lastChild;
	}

	public void setSpouse(boolean spouse) {
		this.spouse = spouse;
	}

	public void setSpouseOfLastChild(boolean spouseOfLastChild) {
		this.spouseOfLastChild = spouseOfLastChild;
	}

	@Override
	public String toString() {
		return String.format("%d %s %s %d %d", id, getName(), getLivedString(), x, y);
	}

}
