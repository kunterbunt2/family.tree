package de.bushnaq.abdalla.family.person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

public abstract class Person extends BasicFamilyMember {
	private static final float	PERSON_BORDER			= 1;
	private static final float	PERSON_HEIGHT			= 64;
	private static final float	PERSON_HEIGHT_COMPACT	= 32;
	private static final float	PERSON_MARGINE			= 1;
	private static final float	PERSON_WIDTH			= 128;
	private static final float	PERSON_WIDTH_COMPACT	= 64;
	private static final float	PERSON_X_SPACE			= 24;
	private static final float	PERSON_X_SPACE_COMPACT	= 12;
	private static final float	PERSON_Y_SPACE			= 12;
	private static final float	PERSON_Y_SPACE_COMPACT	= 6;

	private static String bidiReorder(String text) {
		if (text == null)
			return text;
		try {
			Bidi bidi = new Bidi((new ArabicShaping(ArabicShaping.LETTERS_SHAPE)).shape(text), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		} catch (ArabicShapingException ase3) {
			return text;
		}
	}

	public static float getBorder(Context context) {
		return PERSON_BORDER * context.getParameterOptions().getZoom();
	}

	public static float getHeight(Context context) {
		if (context.getParameterOptions().isCompact())
			return PERSON_HEIGHT_COMPACT * context.getParameterOptions().getZoom();
		else
			return PERSON_HEIGHT * context.getParameterOptions().getZoom();
	}

	public static float getMargine(Context context) {
		return PERSON_MARGINE * context.getParameterOptions().getZoom();
	}

	public static float getWidth(Context context) {
		if (context.getParameterOptions().isCompact())
			return PERSON_WIDTH_COMPACT * context.getParameterOptions().getZoom();
		else
			return PERSON_WIDTH * context.getParameterOptions().getZoom();
	}

	public static float getXSpace(Context context) {
		if (context.getParameterOptions().isCompact())
			return PERSON_X_SPACE_COMPACT * context.getParameterOptions().getZoom();
		else
			return PERSON_X_SPACE * context.getParameterOptions().getZoom();
	}

	public static float getYSpace(Context context) {
		if (context.getParameterOptions().isCompact())
			return PERSON_Y_SPACE_COMPACT * context.getParameterOptions().getZoom();
		else
			return PERSON_Y_SPACE * context.getParameterOptions().getZoom();
	}

	private Attribute	attribute	= new Attribute();
	public Integer		childIndex	= null;
	public List<String>	errors		= new ArrayList<>();
	public Integer		generation	= null;
	public Integer		nextPersonX	= -1;
	public Integer		nextPersonY	= -1;
	public Integer		pageIndex	= null;				// index of the pdf page this person is located at
	public PersonList	personList	= null;
	public Integer		spouseIndex	= null;
	public float		x			= 0;
	public float		y			= 0;

	public Person(PersonList personList, Integer id) {
		super(id);
		this.personList = personList;
	}

	public Person(PersonList personList, Person person) {
		super(person);
		this.personList = personList;
		person.errors = errors;
	}

	public boolean bornBefore(Person person) {
		if (getBorn() != null && person.getBorn() != null) {
			return getBorn().before(person.getBorn());
		}
		return getId() < person.getId();
	}

//	public void calculateWidth(Graphics2D graphics, Font nameFont, Font livedFont) {
//		graphics.setFont(livedFont);
//		idWidth = graphics.getFontMetrics().stringWidth("" + getId());

//		width = 0;
//		graphics.setFont(nameFont);
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getFirstName()));
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getLastName()));
//		graphics.setFont(livedFont);
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getBornString()));
//		width = Math.max(width, graphics.getFontMetrics().stringWidth(getDiedString()));
//		width += Person.PERSON_MARGINE * 2 + Person.PERSON_BORDER * 2 + idWidth;
//		width = PERSON_WIDTH;
//	}

//	public abstract void drawHorizontal(Context context, Graphics2D graphics, Font nameFont, Font livedFont);

//	public abstract void drawVertical(Context context, Graphics2D graphics, Font nameFont, Font livedFont);
	public abstract void drawHorizontal(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException;

	public abstract void drawVertical(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont livedFont, PdfFont pdfDateFont) throws IOException;

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
			return "\u002A" + getBorn().getString();
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
			return /* "\u271D" */"+" + getDied().getString();
		}
		return "";
	}

	public String getFirstNameAsString(Context context) {
		String name = getFirstName();
		if (context.getParameterOptions().isOriginalLanguage()) {
			if (getFirstNameOriginalLanguage() != null)
				name = bidiReorder(getFirstNameOriginalLanguage());// assuming Arabic language
		}
		return String.format("%s", name);
	}

	public String getLastNameAsString(Context context) {
		String name = getLastName();
		if (context.getParameterOptions().isOriginalLanguage()) {
			if (getLastNameOriginalLanguage() != null)
				name = bidiReorder(getLastNameOriginalLanguage());// assuming Arabic language
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

	public Integer getPageIndex() {
		return pageIndex;
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

	boolean isFirstNameOl(Context context) {
		if (context.getParameterOptions().isOriginalLanguage()) {
			if (getFirstNameOriginalLanguage() != null)
				return true;
		}
		return false;
	}

//	public boolean isFirstFather() {
//		return attribute.firstFather;
//	}

	public boolean isLastChild() {
		return attribute.lastChild;
	}

	boolean isLastNameOl(Context context) {
		if (context.getParameterOptions().isOriginalLanguage()) {
			if (getLastNameOriginalLanguage() != null)
				return true;
		}
		return false;
	}

	public abstract boolean isMale();

	public boolean isMember(Context context) {
		if ((getFather() != null) || (getMother() != null) || (this instanceof FemaleClone) || (this instanceof MaleClone))
			return true;
		// not children with spouse that has parents
		for (Person spouse : getSpouseList()) {
			if (spouse.getFather() != null || spouse.getMother() != null || spouse.isRootFather(context))
				return false;
		}
		return true;
//		return getFather() != null || getMother() != null || isFirstFather() || (this instanceof FemaleClone) || (this instanceof MaleClone);
	}

	/**
	 * definition of root father = is male, has no parents, is a member of the family, has children with a spouse that has no parents
	 *
	 * @return
	 */
	public boolean isRootFather(Context context) {
		if (isFemale() || (getFather() != null) || (getMother() != null) || !isMember(context))
			return false;
		for (Person spouse : getSpouseList()) {
			if (spouse.getFather() != null || spouse.getMother() != null)
				return false;
		}

		if (context.getParameterOptions().getFamilyName() == null || !getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase())) {
			return false;
		}
		return true;
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

	public void print(Context context) {
		System.out.println(toString(context));
	}

	public void reset() {
		errors.clear();
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

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
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

	public void validate(Context context) {
		if (!isMember(context) && getLastName() != null && getLastName().toLowerCase().contains("bushnaq")) {
			errors.add("a bushnaq with unknown origins");
		}
		if (getLastName() == null || getLastName().isEmpty()) {
			errors.add("missing last name");
		}
		if (getFirstName() == null || getFirstName().isEmpty()) {
			errors.add("missing first name");
		}
		if (getPageIndex() == null) {
			errors.add("Page index null");
		}
	}

}
