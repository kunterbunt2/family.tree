package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Male;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.person.Rect;
import de.bushnaq.abdalla.pdf.PdfDocument;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class HorizontalTree extends Tree {

    public HorizontalTree(Context context, PersonList personList) {
        super(context, personList);
    }

    /**
     * compact starting from a specific father and ignoring all children from clipGeneration and higher
     */
    @Override
    protected void compact(Context context2, PdfDocument pdfDocument, Person rootFather, int includeGneration) {
        int maxgeneration = findMaxgeneration();

        for (int g = maxgeneration - 1; g > 0; g--) {
//			logger.info(String.format("compacting children of generation %d", g));
            compactChildren(rootFather, g, includeGneration);
        }
        for (int g = maxgeneration - 1; g > 0; g--) {
//			logger.info(String.format("compacting parents of generation %d", g));
            compactParents(rootFather, g, includeGneration);
        }
        Rect treeRect = rootFather.getTreeRect(includeGneration);
        int w = (int) (treeRect.getX2() - treeRect.getX1() + 1);
        int h = (int) (treeRect.getY2() - treeRect.getY1() + 1);
        int area = w * h;
//		logger.info(String.format("compacted tree to %d X %d = %d", w, h, area));
    }

    @Override
    protected void compact(Context context, PdfDocument pdfDocument) {
        List<Male> firstFathers = findRootFatherList();
        int maxgeneration = findMaxgeneration();

        for (Person firstFather : firstFathers) {
            for (int g = maxgeneration - 1; g > 0; g--) {
//				logger.info(String.format("compacting children of generation %d", g));
                compactChildren(firstFather, g);
            }
            for (int g = maxgeneration - 1; g > 0; g--) {
//				logger.info(String.format("compacting parents of generation %d", g));
                compactParents(firstFather, g);
            }
            Rect treeRect = firstFather.getTreeRect();
            int w = (int) (treeRect.getX2() - treeRect.getX1() + 1);
            int h = (int) (treeRect.getY2() - treeRect.getY1() + 1);
            int area = w * h;
//			logger.info(String.format("compacted tree to %d X %d = %d", w, h, area));
        }
    }

    /**
     * this algorithm will pack children of parents of a specific generation and their subtrees together by moving them below each other.<br>
     * The eldest child lowest and the youngest child highest.
     *
     * @param p
     * @param generation
     */
    private void compactChildren(Person p, int generation, int clipGenration) {
        // generation found
        if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
            float y = 0;
            {
                // iterate over children from last to first
                for (int c = p.getChildrenList().size() - 2; c > -1; c--) {
                    Person child = p.getChildrenList().get(c);
                    if (child.getGeneration() != null && child.getGeneration() == generation) {
                        Person next = p.getChildrenList().get(c + 1);
                        y = Math.max(next.getTreeRect(clipGenration - 1).getY2(), y);
                        Rect rect = child.getTreeRect(clipGenration - 1);
                        if ((rect.getX2() - rect.getX1()) > 0) {
                            // has a tree below that is worth moving
                            float delta;
                            if (child.getSpouseList().size() > 1)
                                delta = Math.max(y - child.y + context.getParameterOptions().getMinYDistanceBetweenTrees(), 0);
                            else
                                delta = Math.max(y - child.y + context.getParameterOptions().getMinYDistanceBetweenTrees() - 1, 0f);
                            child.moveTree(0, delta);
//							logger.info(String.format("Move [%d]%s %s y = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
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
                        float deltaX = child.x - prev.x;
                        if (deltaX > 1) {
                            // move child tree to be one unit right to the previous child
                            float delta = -deltaX + 1;
                            child.moveTree(delta, 0);
//							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
                            // if this is the first child of a spouse, the spouse must be moved too
                            if (child.isFirstChild()) {
                                child.getSpouseParent().x += delta;
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
                compactChildren(c, generation, clipGenration);
            }
        }

    }

    /**
     * this algorithm will pack children of parents of a specific generation and their subtrees together by moving them below each other.<br>
     * The eldest child lowest and the youngest child highest.
     *
     * @param p
     * @param generation
     */
    private void compactChildren(Person p, int generation) {
        if (p.getGeneration() != null && p.getGeneration() == generation) {
            // generation found
            if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
                float y = 0;
                {
                    // iterate over children from last to first
                    for (int c = p.getChildrenList().size() - 2; c > -1; c--) {
                        Person child = p.getChildrenList().get(c);
                        Person next = p.getChildrenList().get(c + 1);
                        y = Math.max(next.getTreeRect().getY2(), y);
                        Rect rect = child.getTreeRect();
                        if ((rect.getX2() - rect.getX1()) > 0) {
                            // has a tree below that is worth moving
                            float delta;
                            if (child.getSpouseList().size() > 1)
                                delta = Math.max(y - child.y + context.getParameterOptions().getMinYDistanceBetweenTrees(), 0);
                            else
                                delta = Math.max(y - child.y + context.getParameterOptions().getMinYDistanceBetweenTrees() - 1, 0f);
                            child.moveTree(0, delta);
//							logger.info(String.format("Move [%d]%s %s y = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
                        }
                    }
                }
                {
                    // iterate over all children from first to last
                    for (int c = 1; c < p.getChildrenList().size(); c++) {
                        Person child = p.getChildrenList().get(c);
                        Person prev = p.getChildrenList().get(c - 1);
                        float deltaX = child.x - prev.x;
                        if (deltaX > 1) {
                            // move child tree to be one unit right to the previous child
                            float delta = -deltaX + 1;
                            child.moveTree(delta, 0);
//							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
                            // if this is the first child of a spouse, the spouse must be moved too
                            if (child.isFirstChild()) {
                                child.getSpouseParent().x += delta;
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
                compactChildren(c, generation);
            }
        }

    }

    /**
     * Move parents that are one generation older than specified nearer to each other ignoring all children that are younger than clipGenration
     *
     * @param p
     * @param generation
     */
    private void compactParents(Person p, int generation, int includeGeneration) {
        if (p.getGeneration() != null && p.getGeneration() == generation - 1) {
            // generation found
            if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
                // has a tree below that is worth moving
                {
                    // iterate over all children from first to last
                    for (int c = 1; c < p.getChildrenList().size(); c++) {
                        Person child = p.getChildrenList().get(c);
                        Person prev = p.getChildrenList().get(c - 1);
                        Rect rect = prev.getTreeRect(includeGeneration);
                        float deltaX = child.x - rect.getX2();
                        if (deltaX > 1) {
                            // move child tree to be one unit right to the previous child
                            child.moveTree(-deltaX + 1, 0);
//							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) -deltaX + 1));
                            // if this is the first child of a spouse, the spouse must be moved too
                            if (child.isFirstChild()) {
                                child.getSpouseParent().x += -deltaX + 1;
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

    /**
     * Move parents that are one generation older than specified nearer to each other
     *
     * @param p
     * @param generation
     */
    private void compactParents(Person p, int generation) {
        if (p.getGeneration() != null && p.getGeneration() == generation - 1) {
            // generation found
            if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
                // has a tree below that is worth moving
                {
                    // iterate over all children from first to last
                    for (int c = 1; c < p.getChildrenList().size(); c++) {
                        Person child = p.getChildrenList().get(c);
                        Person prev = p.getChildrenList().get(c - 1);
                        Rect rect = prev.getTreeRect();
                        float deltaX = child.x - rect.getX2();
                        if (deltaX > 1) {
                            // move child tree to be one unit right to the previous child
                            child.moveTree(-deltaX + 1, 0);
//							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) -deltaX + 1));
                            // if this is the first child of a spouse, the spouse must be moved too
                            if (child.isFirstChild()) {
                                child.getSpouseParent().x += -deltaX + 1;
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
                compactParents(c, generation);
            }
        }

    }

    @Override
    void draw(Context context, PdfDocument pdfDocument, int pageIndex) throws IOException {
        for (Person p : personList) {
            if (p.isVisible() && pageIndex == p.getPageIndex())
                p.drawHorizontal(context, pdfDocument);
        }
    }

    @Override
    float position(Context context, Person person, int treeMaxGeneration) {
        person.setVisible(true);
        float pX = person.x;
        if (!person.hasChildren() || person.getGeneration() == treeMaxGeneration)
            pX = person.x + 1;
        PersonList spouseList = person.getSpouseList();
        for (Person spouse : spouseList) {
            spouse.y = person.y + 1;
            spouse.x = pX;
            spouse.setPageIndex(person.getPageIndex());
            // children
            PersonList childrenList = person.getChildrenList(spouse);
            for (Person child : childrenList) {
                if (child.getGeneration() != null && child.getGeneration() <= treeMaxGeneration) {
                    if (!context.getParameterOptions().isExcludeSpouse()) {
                        spouse.setVisible(true);
                        child.y = spouse.y + 1;
                    } else {
                        child.y = person.y + 1;
                    }
                    child.x = pX;
                    child.setPageIndex(person.getPageIndex());
                    pX = position(context, child, treeMaxGeneration);
                }
            }
        }
        return pX;
    }

}
