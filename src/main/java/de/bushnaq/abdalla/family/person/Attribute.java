package de.bushnaq.abdalla.family.person;

public class Attribute {
    public boolean firstFather = false;    // the up most father in the tree belonging to the family
    // public boolean member = false; // member of the family, at this moment this also means the root of his siblings
    public boolean visible = false;    // only draw this person if true
    boolean child = false;    // child of a member of the family
    boolean firstChild = false;    // first child born of a sexual relation
    boolean lastChild = false;    // last child born of a sexual relation
    boolean spouse = false;    // spouse of member of family
    boolean spouseOfLastChild = false;    // spouse of the last child of this branch of the family, used by algorithm to decide where to draw the line for children
    Sex sex = Sex.male;
}
