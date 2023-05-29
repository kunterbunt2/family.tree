package de.bushnaq.abdalla.family.tree;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Male;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.person.Rect;
import de.bushnaq.abdalla.pdf.PdfDocument;

public class VerticalTree extends Tree {

	public VerticalTree(Context context, PersonList personList) {
		super(context, personList);
	}

	@Override
	protected void compact(Context context2, PdfDocument pdfDocument) {
		List<Male>	firstFathers	= findRootFatherList();
		int			maxgeneration	= 2/* findMaxgeneration() */;

		for (Person firstFather : firstFathers) {
//			int g = 2;
			for (int g = maxgeneration - 1; g > 0; g--) {
				logger.info(String.format("compacting children of generation %d", g));
				compactChildren(firstFather, g);
			}
			for (int g = maxgeneration - 1; g > 0; g--) {
				logger.info(String.format("compacting parents of generation %d", g));
				compactParents(firstFather, g);
			}
			Rect	treeRect	= firstFather.getTreeRect();
			int		w			= (int) (treeRect.getX2() - treeRect.getX1() + 1);
			int		h			= (int) (treeRect.getY2() - treeRect.getY1() + 1);
			int		area		= w * h;
			logger.info(String.format("compacted tree to %d X %d = %d", w, h, area));
		}
	}

	/**
	 * this algorithm will pack children and their subtrees together by moving them below each other.<br>
	 * The eldest child lowest and the youngest child highest.
	 *
	 * @param p
	 * @param generation
	 */
	private void compactChildren(Person p, int generation) {
		if (p.getGeneration() != null && p.getGeneration() == generation) {
			// generation found
			if (!p.isSpouse() && p.hasChildren() && p.getChildrenList().size() > 1) {
				float x = 0;
				{
					// iterate over children from last to first
					for (int c = p.getChildrenList().size() - 2; c > -1; c--) {
						Person	child	= p.getChildrenList().get(c);
						Person	next	= p.getChildrenList().get(c + 1);
						x = Math.max(next.getTreeRect().getX2(), x);
						Rect rect = child.getTreeRect();
						if ((rect.getY2() - rect.getY1()) > 0) {
							// has a tree below that is worth moving
							float deltaX;
							if (child.getSpouseList().size() > 1)
								deltaX = x - child.x + context.getParameterOptions().getMinYDistanceBetweenTrees();
							else
								deltaX = x - child.x + context.getParameterOptions().getMinYDistanceBetweenTrees() - 1;
							child.moveTree(deltaX, 0);
							logger.info(String.format("Move [%d]%s %s x = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) deltaX));
						}
					}
				}
				{
					// iterate over all children from first to last
					for (int c = 1; c < p.getChildrenList().size(); c++) {
						Person	child	= p.getChildrenList().get(c);
						Person	prev	= p.getChildrenList().get(c - 1);
						float	deltaY	= child.y - prev.y;
						if (deltaY > 1) {
							// move child tree to be one unit right to the previous child
							float delta = -deltaY + 1;
							child.moveTree(0, delta);
							logger.info(String.format("Move [%d]%s %s y = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) delta));
							// if this is the first child of a spouse, the spouse must be moved too
							if (child.isFirstChild()) {
								child.getSpouseParent().y += delta;
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
	 * Parents that have children packed together can be moved nearer to each other
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
						Person	child	= p.getChildrenList().get(c);
						Person	prev	= p.getChildrenList().get(c - 1);
						Rect	rect	= prev.getTreeRect();
						float	deltaY	= child.y - rect.getY2();
						if (deltaY > 1) {
							// move child tree to be one unit right to the previous child
							child.moveTree(0, -deltaY + 1);
							logger.info(String.format("Move [%d]%s %s y = %d.", child.getId(), child.getFirstName(), child.getLastName(), (int) -deltaY + 1));
							// if this is the first child of a spouse, the spouse must be moved too
							if (child.isFirstChild()) {
								child.getSpouseParent().y += -deltaY + 1;
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
				p.drawVertical(context, pdfDocument, pdfNameFont, pdfNameOLFont, pdfDateFont);
		}
	}

	@Override
	float position(Context context, Person person) {
		person.setVisible(true);
		float pY = person.y;
		if (!person.hasChildren())
			pY = person.y + 1;
		PersonList spouseList = person.getSpouseList();
		for (Person spouse : spouseList) {
			spouse.x = person.x + 1;
			spouse.y = pY;
			spouse.setPageIndex(person.getPageIndex());
			// children
			PersonList childrenList = person.getChildrenList(spouse);
			for (Person child : childrenList) {
				if (!context.getParameterOptions().isExcludeSpouse()) {
					spouse.setVisible(true);
					child.x = spouse.x + 1;
				} else {
					child.x = person.x + 1;
				}
				child.y = pY;
				child.setPageIndex(person.getPageIndex());
				pY = position(context, child);
			}
		}
		return pY;
	}

}
