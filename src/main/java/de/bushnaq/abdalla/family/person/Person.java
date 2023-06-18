package de.bushnaq.abdalla.family.person;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.util.ErrorMessages;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Person extends BasicFamilyMember {
    private static final float PERSON_BORDER = 1;
    private static final float PERSON_BORDER_COMPACT = 0.5f;
    private static final float PERSON_HEIGHT = 64;
    private static final float PERSON_HEIGHT_COMPACT = 30;
    private static final float PERSON_IMAGE_HEIGHT = 64;
    private static final float PERSON_IMAGE_WIDTH = 54;
    private static final float PERSON_MARGINE = 1;
    private static final float PERSON_MARGINE_COMPACT = 0;
    private static final float PERSON_WIDTH = 128;
    private static final float PERSON_WIDTH_COMPACT = 64;
    private static final float PERSON_X_SPACE = 24;
    private static final float PERSON_X_SPACE_COMPACT = 3;
    private static final float PERSON_Y_SPACE = 12;
    private static final float PERSON_Y_SPACE_COMPACT = 4;
    public final PersonList personList;
    private final Attribute attribute = new Attribute();
    private final Map<Integer, PersonList> spouseChildrenList = new HashMap<>();
    public Integer childIndex;
    public List<String> errors = new ArrayList<>();
    public Integer nextPersonX = -1;
    public Integer nextPersonY = -1;
    public Integer pageIndex;                                // index of the pdf page this person is located at
    public Integer spouseIndex;
    public float x = 0;
    public float y = 0;
    public Person minx = null;
    public Person maxx = null;
    public Person miny = null;
    public Person maxy = null;
    private PersonList childrenList;
    private Integer generation;
    private Person nextSibling;
    private Person prevSibling;
    private PersonList spouseList;

    public Person(PersonList personList, Integer id) {
        super(id);
        this.personList = personList;
    }

    public Person(PersonList personList, Person person) {
        super(person);
        this.pageIndex = person.pageIndex;
        this.personList = personList;
        person.errors = errors;
    }

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

    public static Person createFemale(PersonList personList, Integer id) {
        Person female = new DrawablePerson(personList, id, new Color(0xff, 0x62, 0xb0, 32));
        female.setFemale();
        return female;
    }

    public static Person createMale(PersonList personList, Integer id) {
        Person female = new DrawablePerson(personList, id, new Color(0x2d, 0xb1, 0xff, 32));
        female.setMale();
        return female;
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

    public static float getImageHeight(Context context) {
        return PERSON_IMAGE_HEIGHT * context.getParameterOptions().getZoom();
    }

    public static float getImageWidth(Context context) {
        return PERSON_IMAGE_WIDTH * context.getParameterOptions().getZoom();
    }

    public static float getMargine(Context context) {
        if (context.getParameterOptions().isCompact())
            return PERSON_MARGINE_COMPACT * context.getParameterOptions().getZoom();
        else
            return PERSON_MARGINE * context.getParameterOptions().getZoom();
    }

    public static float getPageMargin(Context context) {
        return context.getParameterOptions().getPageMargin() * context.getParameterOptions().getZoom();
    }

    public static float getPersonHeight(Context context) {
        return getHeight(context) + Person.getYSpace(context);
    }

    public static float getPersonWidth(Context context) {
        if (context.getParameterOptions().isShowImage())
            return getImageWidth(context) + getWidth(context) + getXSpace(context);
        else
            return getWidth(context) + getXSpace(context);
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

    public void analyzeTree(Context context) {
        PersonList spouseList = getSpouseList();
        int spouseIndex = 0;
        for (Person spouse : spouseList) {
            if (spouse.isMember(context)) {
                // both parents are member of the family
                // ignore any clone that we already have converted
                if (!context.getParameterOptions().isFollowFemales() && isMale() && !(spouse.isSpouseClone())) {
                    // create a clone of the spouse and shift all child relations to that clone
                    Clone clone = Clone.createSpouseClone(spouse.personList, spouse);
                    PersonList childrenList = getChildrenList(spouse);
                    for (Person child : childrenList) {
                        child.setMother(clone);
                    }
                    personList.add(clone);
                    spouse.resetChildrenList();
                    spouse = clone;
                    spouse.spouseIndex = spouseIndex++;
                    spouse.setSpouse(true);
                } else if (context.getParameterOptions().isFollowFemales() && isFemale() && !(spouse.isSpouseClone())) {
                    // create a clone of the spouse and shift all child relations to that clone
                    Person clone = Clone.createSpouseClone(spouse.personList, spouse);
                    PersonList childrenList = getChildrenList(spouse);
                    for (Person child : childrenList) {
                        child.setFather(clone);
                    }
                    personList.add(clone);
                    spouse.resetChildrenList();
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
            int childIndex = 0;
            boolean firstChild = true;
            PersonList childrenList = getChildrenList(spouse);
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
                child.analyzeTree(context);
            }
            resetSpouseList();
        }
    }

    public boolean bornBefore(Person person) {
        if (getBorn() != null && person.getBorn() != null) {
            return getBorn().before(person.getBorn());
        }
        return getId() < person.getId();
    }

    public abstract void drawHorizontal(Context context, PdfDocument pdfDocument) throws IOException;

    public abstract void drawVertical(Context context, PdfDocument pdfDocument) throws IOException;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Person other))
            return false;
        if ((other.isClone() && !this.isClone()) || (!other.isClone() && this.isClone()))
            return false;
        return this.getId() == other.getId();
    }

    public Person findPaginationClone() {
        for (Person clone : personList) {
            if (clone.isPaginationClone()) {
                Person original = clone.getOriginal();
                if (this.equals(original))
                    return clone;
            }
        }
        return null;
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
            for (Person child : personList) {
                if (child.getFather() != null && child.getMother() != null) {
                    if ((child.getFather().equals(this) && child.getMother().equals(spouse)) || (child.getFather().equals(spouse) && child.getMother().equals(this))) {
                        childrenList.add(child);
                    }
                }
            }
            spouseChildrenList.put(spouse.getId(), childrenList);
        }
        return spouseChildrenList.get(spouse.getId());
    }

    /**
     * return all descendants, that is children of children of a specific generation
     *
     * @param targetGeneration
     * @return
     */
    public PersonList getDescendantList(int targetGeneration) {
        PersonList decendantList = new PersonList();
        for (Person person : getChildrenList()) {
            if (person.getGeneration() == targetGeneration)
                decendantList.add(person);
            else if (person.getGeneration() < targetGeneration)
                decendantList.addAll(person.getDescendantList(targetGeneration));
        }

        return decendantList;
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

    public Person getOriginal() {
        return null;
    }

    protected Person getOriginalFather() {
        return null;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public Person getPrevSibling() {
        return prevSibling;
    }

//	public abstract String getSexCharacter();

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
        Rect rect = new Rect(x, y, x, y);
        PersonList childrenList = getChildrenList();
        for (Person child : childrenList) {
            Rect cr = child.getTreeRect();
            rect.expandToInclude(cr);
        }
        return rect;
    }

    public Rect getTreeRect(int includingGeneration) {
        if (!hasChildren() || getGeneration() >= includingGeneration)
            return new Rect(x, y, x, y);
        Rect rect = new Rect(x, y, x, y);
        PersonList childrenList = getChildrenList();
        for (Person child : childrenList) {
            Rect cr = child.getTreeRect(includingGeneration);
            rect.expandToInclude(cr);
        }
        return rect;
    }

    public boolean hasChildren() {
        return getChildrenList() != null && !getChildrenList().isEmpty();
    }

    protected boolean hasParents() {
        return getFather() != null || getMother() != null;
    }

    public boolean isChild() {
        return attribute.child;
    }

    public boolean isClone() {
        return this.isSpouseClone() || this.isPaginationClone();
    }

    public boolean isFemale() {
        return attribute.sex == Sex.female;
    }

    public boolean isFirstChild() {
        return attribute.firstChild;
    }

    boolean isFirstNameOl(Context context) {
        if (context.getParameterOptions().isOriginalLanguage()) {
            return getFirstNameOriginalLanguage() != null;
        }
        return false;
    }

    public boolean isLastChild() {
        return attribute.lastChild;
    }

    boolean isLastNameOl(Context context) {
        if (context.getParameterOptions().isOriginalLanguage()) {
            return getLastNameOriginalLanguage() != null;
        }
        return false;
    }

    public boolean isMale() {
        return attribute.sex == Sex.male;
    }

    public boolean isMember(Context context) {
        if ((getFather() != null) || (getMother() != null) || this.isSpouseClone() || this.isPaginationClone()) {
            // has a known father or mother
            // has a child with a member and had to be cloned
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

    public boolean isPaginationClone() {
        return false;
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

        return context.getParameterOptions().getFamilyName() != null && getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase());
    }

    /**
     * Is this person a spouse in the tree, that is married to the actual member of the family?
     *
     * @return
     */
    public boolean isSpouse() {
        return attribute.spouse;
    }

    public boolean isSpouseClone() {
        return false;
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
//					logger.info(String.format("Moving [%d]%s %s x= %d, y = %d.", getId(), getFirstName(), getLastName(), (int) x, (int) y));
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

    public void resetChildrenList() {
        childrenList = null;
        spouseChildrenList.clear();
        for (Person spouse : getSpouseList()) {
            spouse.resetSpouseList();

        }

        spouseList = null;
    }

    private void resetSpouseList() {
        spouseList = null;// lets the list be populated again, as some might have been replaced by clones
    }

    public void setFemale() {
        attribute.sex = Sex.female;
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

    public void setMale() {
        attribute.sex = Sex.male;
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
