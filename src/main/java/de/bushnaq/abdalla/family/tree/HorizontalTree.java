package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.person.Rect;
import de.bushnaq.abdalla.pdf.PdfDocument;

import java.io.IOException;
import java.util.Iterator;

public class HorizontalTree extends Tree {

    public HorizontalTree(Context context, PersonList personList) {
        super(context, personList);
    }

    /**
     * compact starting from a specific father and all children that have getGeneration() <= includingGeneration
     */
    @Override
    protected void compact(Context context, PdfDocument pdfDocument, Person rootFather, int includingGeneration) {
        int maxGeneration = personList.findMaxGeneration();

        for (int g = maxGeneration; g >= 0; g--) {
            logger.info(String.format("compacting children of generation %d", g));
            compactChildren(rootFather, g, includingGeneration);
        }
        for (int g = maxGeneration; g >= 0; g--) {
//			logger.info(String.format("compacting parents of generation %d", g));
            compactParents(rootFather, g, includingGeneration);
        }
        Rect treeRect = rootFather.getTreeRect(includingGeneration);
        int w = (int) (treeRect.getX2() - treeRect.getX1() + 1);
        int h = (int) (treeRect.getY2() - treeRect.getY1() + 1);
        int area = w * h;
//		logger.info(String.format("compacted tree to %d X %d = %d", w, h, area));
    }

//    @Override
//    protected void compact(Context context, PdfDocument pdfDocument) {
//        List<Person> firstFathers = personList.findRootFatherList(context);
//        int maxgeneration = personList.findMaxGeneration();
//
//        for (Person firstFather : firstFathers) {
//            for (int g = maxgeneration - 1; g > 0; g--) {
////				logger.info(String.format("compacting children of generation %d", g));
//                compactChildren(firstFather, g);
//            }
//            for (int g = maxgeneration - 1; g > 0; g--) {
////				logger.info(String.format("compacting parents of generation %d", g));
//                compactParents(firstFather, g);
//            }
//            Rect treeRect = firstFather.getTreeRect();
//            int w = (int) (treeRect.getX2() - treeRect.getX1() + 1);
//            int h = (int) (treeRect.getY2() - treeRect.getY1() + 1);
//            int area = w * h;
////			logger.info(String.format("compacted tree to %d X %d = %d", w, h, area));
//        }
//    }

    /**
     * this algorithm will pack children of parents of a specific generation and their subtrees together by moving them below each other.<br>
     * The eldest child lowest and the youngest child highest.
     *
     * @param p
     * @param generation
     */
    private void compactChildren(Person p, int generation, int includingGeneration) {
//        if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1)
        if (p.getGeneration() != null && p.getGeneration() == generation) {
            // generation found
            float y = 0;
            {
                // iterate over children from last to first
                for (int c = p.getChildrenList().size() - 2; c > -1; c--) {
                    Person child = p.getChildrenList().get(c);
//                    if (child.getGeneration() != null && child.getGeneration() == generation)
                    {
                        Person next = p.getChildrenList().get(c + 1);
                        y = Math.max(next.getTreeRect(includingGeneration).getY2(), y);
                        Rect rect = child.getTreeRect(includingGeneration);
                        if ((rect.getX2() - rect.getX1()) > 0) {
                            // has a tree below that is worth moving
                            float delta;
                            if (child.getSpouseList().size() > 1)
                                delta = Math.max(y - child.getY() + context.getParameterOptions().getMinYDistanceBetweenTrees(), 0);
                            else
                                delta = Math.max(y - child.getY() + context.getParameterOptions().getMinYDistanceBetweenTrees() - 1, 0f);
                            child.moveTree(0, delta);
                            logger.info(String.format("Move [%d]%s %s y = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
                        }
                    }
                }
            }
            {
                // iterate over all children from first to last
                for (int c = 1; c < p.getChildrenList().size(); c++) {
                    Person child = p.getChildrenList().get(c);
                    if (child.getGeneration() != null && child.getGeneration() == generation) {
                        Person prev = p.getChildrenList().get(c - 1);
                        float deltaX = child.getX() - prev.getX();
                        if (deltaX > 1) {
                            // move child tree to be one unit right to the previous child
                            float delta = -deltaX + 1;
                            child.moveTree(delta, 0);
                            logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
                            // if this is the first child of a spouse, the spouse must be moved too
                            if (child.isFirstChild()) {
                                child.getSpouseParent().setX(child.getSpouseParent().getX() + delta);
                            }
                        }
                    }

                }
            }
        } else if (p.getGeneration() != null && p.getGeneration() < generation) {
            // generation not found yet
            Iterator<Person> di = p.getChildrenList().descendingIterator();
            while (di.hasNext()) {
                Person c = di.next();
                compactChildren(c, generation, includingGeneration);
            }
        }

    }

//    /**
//     * this algorithm will pack children of parents of a specific generation and their subtrees together by moving them below each other.<br>
//     * The eldest child lowest and the youngest child highest.
//     *
//     * @param p
//     * @param generation
//     */
//    private void compactChildren(Person p, int generation) {
//        if (p.getGeneration() != null && p.getGeneration() == generation) {
//            // generation found
//            if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
//                float y = 0;
//                {
//                    // iterate over children from last to first
//                    for (int c = p.getChildrenList().size() - 2; c > -1; c--) {
//                        Person child = p.getChildrenList().get(c);
//                        Person next = p.getChildrenList().get(c + 1);
//                        y = Math.max(next.getTreeRect().getY2(), y);
//                        Rect rect = child.getTreeRect();
//                        if ((rect.getX2() - rect.getX1()) > 0) {
//                            // has a tree below that is worth moving
//                            float delta;
//                            if (child.getSpouseList().size() > 1)
//                                delta = Math.max(y - child.y + context.getParameterOptions().getMinYDistanceBetweenTrees(), 0);
//                            else
//                                delta = Math.max(y - child.y + context.getParameterOptions().getMinYDistanceBetweenTrees() - 1, 0f);
//                            child.moveTree(0, delta);
////							logger.info(String.format("Move [%d]%s %s y = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
//                        }
//                    }
//                }
//                {
//                    // iterate over all children from first to last
//                    for (int c = 1; c < p.getChildrenList().size(); c++) {
//                        Person child = p.getChildrenList().get(c);
//                        Person prev = p.getChildrenList().get(c - 1);
//                        float deltaX = child.x - prev.x;
//                        if (deltaX > 1) {
//                            // move child tree to be one unit right to the previous child
//                            float delta = -deltaX + 1;
//                            child.moveTree(delta, 0);
////							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
//                            // if this is the first child of a spouse, the spouse must be moved too
//                            if (child.isFirstChild()) {
//                                child.getSpouseParent().x += delta;
//                            }
//                        }
//
//                    }
//                }
//            }
//        } else if (p.getGeneration() != null && p.getGeneration() < generation) {
//            // generation not found yet
//            Iterator<Person> di = p.getChildrenList().descendingIterator();
//            while (di.hasNext()) {
//                Person c = di.next();
//                compactChildren(c, generation);
//            }
//        }
//
//    }

    /**
     * Move parents that are one generation older than specified nearer to each other ignoring all children that are younger than clipGenration
     *
     * @param p
     * @param generation
     */
    private void compactParents(Person p, int generation, int includeGeneration) {
        if (p.getGeneration() != null && p.getGeneration() == generation - 1) {
            // found target generation
            if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
                // has a tree below that is worth moving
                {
                    // iterate over all children from first to last
                    for (int c = 1; c < p.getChildrenList().size(); c++) {
                        Person child = p.getChildrenList().get(c);
                        Person prev = p.getChildrenList().get(c - 1);
                        Rect rect = prev.getTreeRect(includeGeneration);
                        float x;
//                        if (prev.hasChildren()) {
//                            Person person = prev.getSpouseList().get(prev.getSpouseList().size() - 1);
//                            x = prev.getSpouseList().get(prev.getSpouseList().size() - 1).getX();
//                        } else
                        x = prev.getX();

                        float deltaX = child.getX() - x;
                        if (deltaX > 1) {
                            // move child tree to be one unit right to the previous child
                            child.moveTree(-deltaX + 1, 0);
//							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) -deltaX + 1));
                            // if this is the first child of a spouse, the spouse must be moved too
                            if (child.isFirstChild()) {
                                child.getSpouseParent().setX(child.getSpouseParent().getX() + -deltaX + 1);
                            }
                        }
                    }
                }
            }
        } else if (p.getGeneration() != null && p.getGeneration() < generation - 1) {
            // generation not found yet
            Iterator<Person> di = p.getChildrenList().descendingIterator();
            while (di.hasNext()) {
                Person c = di.next();
                compactParents(c, generation, includeGeneration);
            }
        }

    }

//    /**
//     * Move parents that are one generation older than specified nearer to each other
//     *
//     * @param p
//     * @param generation
//     */
//    private void compactParents(Person p, int generation) {
//        if (p.getGeneration() != null && p.getGeneration() == generation - 1) {
//            // generation found
//            if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
//                // has a tree below that is worth moving
//                {
//                    // iterate over all children from first to last
//                    for (int c = 1; c < p.getChildrenList().size(); c++) {
//                        Person child = p.getChildrenList().get(c);
//                        Person prev = p.getChildrenList().get(c - 1);
//                        Rect rect = prev.getTreeRect();
//                        float deltaX = child.x - rect.getX2();
//                        if (deltaX > 1) {
//                            // move child tree to be one unit right to the previous child
//                            child.moveTree(-deltaX + 1, 0);
////							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) -deltaX + 1));
//                            // if this is the first child of a spouse, the spouse must be moved too
//                            if (child.isFirstChild()) {
//                                child.getSpouseParent().x += -deltaX + 1;
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (p.getGeneration() != null && p.getGeneration() < generation - 1) {
//            // generation not found yet
//            Iterator<Person> di = p.getChildrenList().descendingIterator();
//            while (di.hasNext()) {
//                Person c = di.next();
//                compactParents(c, generation);
//            }
//        }
//
//    }

    @Override
    void draw(Context context, PdfDocument pdfDocument, int pageIndex) throws IOException {
        for (Person p : personList) {
            if (p.isVisible() && pageIndex == p.getPageIndex())
                p.drawHorizontal(context, pdfDocument);
        }
    }

    @Override
    float position(Context context, Person person, int includingGeneration) {
        person.setVisible(true);
        float pX = person.getX();
        if (!person.hasChildren() || person.getGeneration() == includingGeneration)
            pX = person.getX() + 1;
        PersonList spouseList = person.getSpouseList();
        for (Person spouse : spouseList) {
            spouse.setY(person.getY() + 1);
            spouse.setX(pX);
            spouse.setPageIndex(person.getPageIndex());
            // children
            PersonList childrenList = person.getChildrenList(spouse);
            for (Person child : childrenList) {
                if (child.getGeneration() != null && child.getGeneration() <= includingGeneration) {
                    if (!context.getParameterOptions().isExcludeSpouse()) {
                        spouse.setVisible(true);
                        child.setY(spouse.getY() + 1);
                    } else {
                        child.setY(person.getY() + 1);
                    }
                    child.setX(pX);
                    child.setPageIndex(person.getPageIndex());
//                    child.setFamilyLetter(person.getFamilyLetter());
                    pX = position(context, child, includingGeneration);
                }
            }
        }
        return pX;
    }

}
