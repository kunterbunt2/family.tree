package de.bushnaq.abdalla.family.person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;
import de.bushnaq.abdalla.util.ErrorMessages;

public abstract class Person extends BasicFamilyMember {
	private static final float	PERSON_BORDER			= 1;
	private static final float	PERSON_BORDER_COMPACT	= 0.5f;
	private static final float	PERSON_HEIGHT			= 64;
	private static final float	PERSON_HEIGHT_COMPACT	= 32;
	private static final float	PERSON_MARGINE			= 1;
	private static final float	PERSON_MARGINE_COMPACT	= 0;
	private static final float	PERSON_WIDTH			= 128;
	private static final float	PERSON_WIDTH_COMPACT	= 64;
	private static final float	PERSON_X_SPACE			= 24;
	private static final float	PERSON_X_SPACE_COMPACT	= 3;
	private static final float	PERSON_Y_SPACE			= 12;
	private static final float	PERSON_Y_SPACE_COMPACT	= 3;

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
		if (context.getParameterOptions().isCompact())
			return PERSON_BORDER_COMPACT * context.getParameterOptions().getZoom();
		else
			return PERSON_BORDER * context.getParameterOptions().getZoom();
	}

	public static float getHeight(Context context) {
		if (context.getParameterOptions().isCompact())
			return PERSON_HEIGHT_COMPACT * context.getParameterOptions().getZoom();
		else
			return PERSON_HEIGHT * context.getParameterOptions().getZoom();
	}

	public static float getMargine(Context context) {
		if (context.getParameterOptions().isCompact())
			return PERSON_MARGINE_COMPACT * context.getParameterOptions().getZoom();
		else
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

	private Attribute					attribute			= new Attribute();
	public Integer						childIndex;
	private PersonList					childrenList;
	public List<String>					errors				= new ArrayList<>();
	private Integer						generation;
	public Integer						nextPersonX			= -1;
	public Integer						nextPersonY			= -1;
	private Person						nextSibling;
	public Integer						pageIndex;													// index of the pdf page this person is located at
	public PersonList					personList;
	private Person						prevSibling;
	private Map<Integer, PersonList>	spouseChildrenList	= new HashMap<>();

	public Integer						spouseIndex;

	private PersonList					spouseList;

	public float						x					= 0;
	public float						y					= 0;

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
		if (childrenList == null) {
			childrenList = new PersonList();
			PersonList spouseList = getSpouseList();
			for (Person spouse : spouseList) {
				childrenList.addAll(getChildrenList(spouse));
//				for (Person child : personList) {
//					if (child.getFather() != null && child.getMother() != null) {
//						if ((child.getFather().equals(this) && child.getMother().equals(spouse)) || (child.getFather().equals(spouse) && child.getMother().equals(this))) {
//							childrenList.add(child);
//						}
//					}
//				}
			}
			Person last = null;
			for (Person child : childrenList) {
				if (last != null) {
					last.nextSibling = child;
					child.prevSibling = last;
				}
				last = child;
			}

		}
		return childrenList;
	}

	public PersonList getChildrenList(Person spouse) {
		if (spouseChildrenList.get(spouse.getId()) == null) {
			PersonList childrenList = new PersonList();
//			Person		last			= null;
			for (Person child : personList) {
				if (child.getFather() != null && child.getMother() != null) {
					if ((child.getFather().equals(this) && child.getMother().equals(spouse)) || (child.getFather().equals(spouse) && child.getMother().equals(this))) {
						childrenList.add(child);
//						if (last != null) {
//							last.nextSibling = child;
//							child.prevSibling = last;
//						}
//						last = child;
					}
				}
			}
			spouseChildrenList.put(spouse.getId(), childrenList);
		}
		return spouseChildrenList.get(spouse.getId());
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

	public Integer getGeneration() {
		return generation;
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

	public Person getNextSibling() {
		return nextSibling;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public Person getPrevSibling() {
		return prevSibling;
	}

	public abstract String getSexCharacter();

	public PersonList getSpouseList() {
		if (spouseList == null) {
			spouseList = new PersonList();
			for (Person p : personList) {
				if (p.getFather() != null && p.getFather().equals(this))
					spouseList.add(p.getMother());
				if (p.getMother() != null && p.getMother().equals(this))
					spouseList.add(p.getFather());
			}
		}
		return spouseList;
	}

	public Person getSpouseParent() {
		Person ps = null;
		if (getFather().isSpouse())
			ps = getFather();
		else if (getMother().isSpouse())
			ps = getMother();
		return ps;
	}

	public Rect getTreeRect() {
		if (!hasChildren())
			return new Rect(x, y, x, y);
		Rect		rect			= new Rect(x, y, x, y);
		PersonList	childrenList	= getChildrenList();
		for (Person child : childrenList) {
			Rect cr = child.getTreeRect();
			rect.expandToInclude(cr);
		}
		return rect;
	}

	public boolean hasChildren() {
		for (Person p : personList) {
			if ((p.getFather() != null && p.getFather().equals(this)) || (p.getMother() != null && p.getMother().equals(this)))
				return true;
		}
		return false;
	}

	protected boolean hasParents() {
		if (getFather() != null || getMother() != null)
			return true;
		return false;
	}

	public void initAttribute(Context context) {
		PersonList	spouseList	= getSpouseList();
		int			spouseIndex	= 0;
		for (Person spouse : spouseList) {
			if (spouse.isMember(context)) {
				// both parents are member of the family
				// ignore any clone that we already have converted
				if (!context.getParameterOptions().isFollowFemales() && isMale() && !(spouse instanceof FemaleClone)) {
					// create a clone of the spouse and shift all child relations to that clone
					FemaleClone	clone			= new FemaleClone(spouse.personList, (Female) spouse);
					PersonList	childrenList	= getChildrenList(spouse);
					for (Person child : childrenList) {
						child.setMother(clone);
					}
					personList.add(clone);
					spouse = clone;
					spouse.spouseIndex = spouseIndex++;
					spouse.setSpouse(true);
				} else if (context.getParameterOptions().isFollowFemales() && isFemale() && !(spouse instanceof MaleClone)) {
					// create a clone of the spouse and shift all child relations to that clone
					MaleClone	clone			= new MaleClone(spouse.personList, (Male) spouse);
					PersonList	childrenList	= getChildrenList(spouse);
					for (Person child : childrenList) {
						child.setFather(clone);
					}
					personList.add(clone);
					spouse = clone;
					spouse.spouseIndex = spouseIndex++;
					spouse.setSpouse(true);
				}
			} else {
				spouse.spouseIndex = spouseIndex++;
				spouse.setSpouse(true);
			}
			if (isLastChild()) {
				spouse.setSpouseOfLastChild(true);
			}
			// children
			int			childIndex		= 0;
			boolean		firstChild		= true;
			PersonList	childrenList	= getChildrenList(spouse);
			for (Person child : childrenList) {
				child.setGeneration(getGeneration() + 1);
				child.childIndex = childIndex++;
				child.setIsChild(true);
				if (firstChild) {
					child.setFirstChild(true);
					firstChild = false;
				}
				if (child.equals(childrenList.last())) {
					child.setLastChild(true);
				}
				child.initAttribute(context);
			}
			resetSpouseList();
		}
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
		if ((getFather() != null) || (getMother() != null) || (this instanceof FemaleClone) || (this instanceof MaleClone)) {
			// has a known father or mother
			// has a child with a member
			return true;
		}
		// not children with spouse that has parents
		for (Person spouse : getSpouseList()) {
			if (spouse.getFather() != null || spouse.getMother() != null || spouse.isRootFather(context)) {
				return false;
			}
		}
		return true;
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

	/**
	 * Is this person a spouse in the tree, that is married to the actual member of the family?
	 *
	 * @return
	 */
	public boolean isSpouse() {
		return attribute.spouse;
	}

	public boolean isSpouseOfLastChild() {
		return attribute.spouseOfLastChild;
	}

	public boolean isVisible() {
		return attribute.visible;
	}

	public void moveTree(float x, float y) {
		if (x != 0 || y != 0) {
			this.x += x;
			this.y += y;
			if (!isSpouse()) {
				PersonList spouseList = getSpouseList();
				for (Person spouse : spouseList) {
					logger.info(String.format("Moving [%d]%s %s x= %d, y = %d.", getId(), getFirstName(), getLastName(), (int) x, (int) y));
					spouse.moveTree(x, y);
					PersonList childrenList = getChildrenList(spouse);
					for (Person child : childrenList) {
						child.moveTree(x, y);
					}
				}

			}
		}
	}

	public void print(Context context) {
		System.out.println(toString(context));
	}

	public void reset() {
		errors.clear();
	}

	private void resetSpouseList() {
		spouseList = null;// lets the list be populated again, as some might have been replaced by clones
	}

	public void setFirstChild(boolean firstChild) {
		this.attribute.firstChild = firstChild;
	}

	public void setFirstFather(boolean firstFather) {
		attribute.firstFather = firstFather;
	}

	public void setGeneration(Integer generation) {
		this.generation = generation;
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
		if (!isMember(context) && getLastName() != null && getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase())) {
			errors.add(String.format(ErrorMessages.ERROR_005_PERSON_UNKNOWN_ORIGINS, context.getParameterOptions().getFamilyName()));
		}
		if (getLastName() == null || getLastName().isEmpty()) {
			errors.add(ErrorMessages.ERROR_004_PERSON_MISSING_LAST_NAME);
		}
		if (getFirstName() == null || getFirstName().isEmpty()) {
			errors.add(ErrorMessages.ERROR_003_PERSON_MISSING_FIRST_NAME);
		}
		if (getPageIndex() == null) {
			errors.add(ErrorMessages.ERROR_002_PAGE_INDEX_NULL);
		}
	}

}
