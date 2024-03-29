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
    private static final float PERSON_MARGIN = 1;
    private static final float PERSON_MARGIN_COMPACT = 0;
    private static final float PERSON_WIDTH = 128;
    private static final float PERSON_WIDTH_COMPACT = 64;
    private static final float PERSON_X_SPACE = 24;
    private static final float PERSON_X_SPACE_COMPACT = 3;
    private static final float PERSON_Y_SPACE = 24;
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
    //    public Person minx = null;
//    public Person maxx = null;
//    public Person miny = null;
//    public Person maxy = null;
    private float x = 0;
    private float y = 0;
    private PersonList childrenList;
    private Integer generation;
    private Person nextSibling;
    private Person prevSibling;
    private PersonList spouseList;
    private boolean visited;

    public Person(PersonList personList, Integer id, String familyLetter) {
        super(id, familyLetter);
        this.personList = personList;
    }

    public Person(PersonList personList, Person person) {
        super(person);
        this.attribute.sex = person.attribute.sex;
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

    public static Person createFemale(PersonList personList, Integer id, String familyLetter) {
        Person female = new DrawablePerson(personList, id, familyLetter, new Color(0xff, 0x62, 0xb0, 32));
        female.setFemale();
        return female;
    }

    public static Person createMale(PersonList personList, Integer id, String familyLetter) {
        Person female = new DrawablePerson(personList, id, familyLetter, new Color(0x2d, 0xb1, 0xff, 32));
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

    public static float getMargin(Context context) {
        if (context.getParameterOptions().isCompact())
            return PERSON_MARGIN_COMPACT * context.getParameterOptions().getZoom();
        else
            return PERSON_MARGIN * context.getParameterOptions().getZoom();
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
            if (spouse.isFamilyMember(context)) {
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
                if (child.equals(childrenList.getLast())) {
                    child.setLastChild(true);
                }
//                child.setFamilyLetter(getFamilyLetter());
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

    public int compare(Person o1, Person o2) {
        if (o1.getBorn() != null && o2.getBorn() != null) {
            int compare = o1.getBorn().compareTo(o2.getBorn());
            if (compare != 0) {
                return compare;
            }
        }
        if (o1.isClone() && !o2.isClone()) {
            return o1.getId() + 1000 - o2.getId();
        }
        if (!o1.isClone() && o2.isClone()) {
            return o1.getId() - o2.getId() - 1000;
        }

        return o1.getId() - o2.getId();
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

    /**
     * find any existing spouse clone of this person
     *
     * @return
     */
    public Person findSpouseClone() {
        for (Person clone : personList) {
            if (clone.isSpouseClone()) {
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

    public Integer getChildIndex() {
        return childIndex;
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

    public PersonList getDescendantList() {
        PersonList descendantList = new PersonList();
        for (Person person : getChildrenList()) {
            descendantList.add(person);
            descendantList.addAll(person.getDescendantList());
        }

        return descendantList;
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

    public String getSex() {
        return attribute.sex.name();
    }

    public PersonList getSpouseList() {
        if (spouseList == null) {
            spouseList = new PersonList();
            for (Person p : personList) {
                if (p.getFather() != null && p.getFather().equals(this) && p.getMother() != null)
                    spouseList.add(p.getMother());
                if (p.getMother() != null && p.getMother().equals(this) && p.getFather() != null)
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

    public PersonList getSubTree() {
        PersonList subTreeList = new PersonList();
        if (!isSpouse()) {
            for (Person spouse : getSpouseList()) {
                subTreeList.add(spouse);
            }
        }
        for (Person child : getChildrenList()) {
            subTreeList.add(child);
            subTreeList.addAll(child.getSubTree());
        }

        return subTreeList;
    }

//	public abstract String getSexCharacter();

    /**
     * return the parent that heads this little tree
     *
     * @return
     */
    public Person getTreeHead() {
        if (getFather() != null && !getFather().isSpouse()) {
            return getFather();
        } else if (getMother() != null && !getMother().isSpouse()) {
            return getMother();
        }
        return null;// we are teh head of this tree
    }

    public Rect getTreeRect() {
        if (!hasChildren())
            return new Rect(getX(), getY(), getX(), getY());
        Rect rect = new Rect(getX(), getY(), getX(), getY());
        PersonList childrenList = getChildrenList();
        for (Person child : childrenList) {
            Rect cr = child.getTreeRect();
            rect.expandToInclude(cr);
        }
        return rect;
    }

    public Rect getTreeRect(int includingGeneration) {
        if (!hasChildren() || (getGeneration() != null && getGeneration() >= includingGeneration))
            return new Rect(getX(), getY(), getX(), getY());
        Rect rect = new Rect(getX(), getY(), getX(), getY());
        PersonList childrenList = getChildrenList();
        for (Person child : childrenList) {
            Rect cr = child.getTreeRect(includingGeneration);
            rect.expandToInclude(cr);
        }
        return rect;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
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

    /**
     * is member of one of the family trees
     *
     * @param context
     * @return
     */
    public boolean isFamilyMember(Context context) {
        if ((getFather() != null) || (getMother() != null) || this.isSpouseClone() || this.isPaginationClone()) {
            // has a known father or mother
            // has a child with a member and had to be cloned
            return true;
        }
        if (context.getParameterOptions().getFamilyName() == null) {
            // family name cannot be empty
            return false;
        }
        if (!getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase())) {
            // family name must match tree family name
            return false;
        }

        // not children with spouse that has parents
//        for (Person spouse : getSpouseList()) {
//            if (spouse.isPaginationClone())
//                return false;// pagination clones must have parents
//            if (spouse.getFather() != null || spouse.getMother() != null || spouse.isTreeRoot(context)) {
//                // spouse cannot have father
//                // spouse cannot have a mother
//                // spouse cannot be tree root
//                return false;
//            }
//        }
        return true;
    }

    public boolean isFemale() {
        return attribute.sex == Sex.Female;
    }

    public boolean isFirstChild() {
        return attribute.firstChild;
    }

    public boolean isFirstNameOl(Context context) {
        if (context.getParameterOptions().isOriginalLanguage()) {
            return getFirstNameOriginalLanguage() != null;
        }
        return false;
    }

    public boolean isLastChild() {
        return attribute.lastChild;
    }

    public boolean isLastNameOl(Context context) {
        if (context.getParameterOptions().isOriginalLanguage()) {
            return getLastNameOriginalLanguage() != null;
        }
        return false;
    }

    public boolean isMale() {
        return attribute.sex == Sex.Male;
    }

    public boolean isPaginationClone() {
        return false;
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

    /**
     * definition of root father = is male, has no parents, is a member of the family, has children with a spouse that has no parents
     *
     * @return
     */
    public boolean isTreeRoot(Context context) {
        if (isFemale() || (getFather() != null) || (getMother() != null) || !isFamilyMember(context)) {
            // cannot be a Female
            // cannot have a father
            // cannot have a mother
            // must be member of the family
            return false;
        }
        if (isClone()) {
            // cannot be a pagination clone or spouse clone.
            return false;
        }
        if (!hasChildren()) {
            // must have children to be root of tree
            return false;
        }
//        for (Person spouse : getSpouseList()) {
//            if (spouse.getFather() != null || spouse.getMother() != null) {
//                // spouse must not have father or mother
//                // special case for #TODO find member name
//                return false;
//            }
//        }
        if (context.getParameterOptions().getFamilyName() == null) {
            // family name cannot be empty
            return false;
        }
        if (!getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase())) {
            // family name must match tree family name
            return false;
        }

        return true;
    }

    public boolean isVisible() {
        return attribute.visible;
    }

    public boolean isVisited() {
        return visited;
    }

    public void moveSpouseTree(float x, float y) {
        if (x != 0 || y != 0) {
            this.setX(this.getX() + x);
            this.setY(this.getY() + y);
            if (isSpouse()) {
                PersonList spouseList = getSpouseList();
                for (Person spouse : spouseList) {
//					logger.info(String.format("Moving [%d]%s %s x= %d, y = %d.", getId(), getFirstName(), getLastName(), (int) x, (int) y));
//                    spouse.moveTree(x, y);
                    PersonList childrenList = getChildrenList(spouse);
                    for (Person child : childrenList) {
                        child.moveTree(x, y);
                    }
                }

            }
        }
    }

    public void moveTree(float x, float y) {
        if (x != 0 || y != 0) {
            this.setX(this.getX() + x);
            this.setY(this.getY() + y);
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
        System.out.println(toString());
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

    public void resetSpouseList() {
        spouseList = null;// lets the list be populated again, as some might have been replaced by clones
    }


    public void setFemale() {
        attribute.sex = Sex.Female;
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
        attribute.sex = Sex.Male;
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

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String toString() {
        if (isClone())
            if (getPageIndex() != null)
                return String.format("[%d][%s %s]{%.0f.%.0f} page=%d clone", getId(), getFirstName(), getLastName(), x, y, getPageIndex());
            else
                return String.format("[%d][%s %s]{%.0f.%.0f} clone", getId(), getFirstName(), getLastName(), x, y);
        else if (getPageIndex() != null)
            return String.format("[%d][%s %s]{%.0f.%.0f} page:%d", getId(), getFirstName(), getLastName(), x, y, getPageIndex());
        else
            return String.format("[%d][%s %s]{%.0f.%.0f}", getId(), getFirstName(), getLastName(), x, y);
    }

    public void validate(Context context) throws Exception {
        if (errors.isEmpty()) {
            if (isVisible() && getPageIndex() == null) {
                throw new Exception(String.format("[%d] %s is visible but pageIndex == null.", getId(), getClass().getName()));
            }
            if (!isFamilyMember(context) && getLastName() != null && getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase())) {
                errors.add(String.format(ErrorMessages.ERROR_005_PERSON_UNKNOWN_ORIGINS, this, context.getParameterOptions().getFamilyName()));
            }
            if (getLastName() == null || getLastName().isEmpty()) {
                errors.add(String.format(ErrorMessages.ERROR_004_PERSON_MISSING_LAST_NAME, this));
            }
            if (getFirstName() == null || getFirstName().isEmpty()) {
                errors.add(String.format(ErrorMessages.ERROR_003_PERSON_MISSING_FIRST_NAME, this));
            }
            if (getPageIndex() == null) {
                errors.add(String.format(ErrorMessages.ERROR_002_PAGE_INDEX_NULL, this));
            }
            if (context.getParameterOptions().getFamilyName() != null && getLastName().toLowerCase().contains(context.getParameterOptions().getFamilyName().toLowerCase())) {
                Person spouseClone = findSpouseClone();
                if (spouseClone == null && !isClone()) {
                    for (Person spouse : getSpouseList()) {
                        if (spouse.getFather() != null || spouse.getMother() != null || spouse.isTreeRoot(context)) {
                            // spouse cannot have father
                            // spouse cannot have a mother
                            // spouse cannot be tree root
                            // special case for #6 Abd al Asis Bushnaq
                            errors.add("Family member missing father and mother.");
                        }

                        if (spouse.getFather() != null || spouse.getMother() != null) {
                            // spouse must not have father or mother
                        }
                    }
                }
            }
        }
    }
}
