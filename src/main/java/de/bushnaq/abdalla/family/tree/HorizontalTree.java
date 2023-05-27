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

public class HorizontalTree extends Tree {

	public HorizontalTree(Context context, PersonList personList) {
		super(context, personList);
	}

	@Override
	protected void compact(Context context2, PdfDocument pdfDocument) {
		List<Male> firstFathers = findRootFatherList();
		for (Person firstFather : firstFathers) {
			compactChildren(firstFather, 1);
		}
		for (Person firstFather : firstFathers) {
			compactGeneration(firstFather, 1);
		}
	}

	private void compactChildren(Person p, int generation) {
		if (p.getGeneration() != null && p.getGeneration() == generation) {
			// generation found
			if (!p.isSpouse() && p.hasChildren()) {
				// has a tree below that is worth moving
				float y = 0;
				{
					// iterate over children from last to first
					Iterator<Person> di = p.getChildrenList().descendingIterator();
					while (di.hasNext()) {
						Person c = di.next();
						if (y != 0) {
							// move each child tree to be below the next child
							c.moveTree(0, y - c.y - 1);
						}
						Rect rect = c.getTreeRect();
						if ((rect.getX2() - rect.getX1()) > 0) {
							y = rect.getY2() + 1;
						}
					}
				}
				{
					// iterate over all children from first to last
					Person				lastChild	= null;
					Iterator<Person>	di			= p.getChildrenList().iterator();
					while (di.hasNext()) {
						Person c = di.next();
						if (lastChild != null && (c.x - lastChild.x > 1)) {
							// move child tree to be one unit right to the previous child
							float deltaX = c.x - lastChild.x;
							c.moveTree(-deltaX + 1, 0);
							// if this is the first child of a spouse, the spouse must be moved too
							if (c.isFirstChild()) {
								c.getSpouseParent().x += -deltaX + 1;
							}
						}
						lastChild = c;

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

	private void compactGeneration(Person p, int generation) {
		if (p.getGeneration() != null && p.getGeneration() == generation - 1) {
			// generation found
			if (!p.isSpouse() && p.hasChildren()) {
				// has a tree below that is worth moving
				{
					// iterate over all children from first to last
					Person				lastChild	= null;
					Iterator<Person>	di			= p.getChildrenList().iterator();
					while (di.hasNext()) {
						Person c = di.next();
						// move all children of that generation together
						if (lastChild != null && (c.x - lastChild.x > 1)) {
							// move child tree to be one unit right to the previous child
							Rect	rect	= lastChild.getTreeRect();
							float	deltaX	= c.x - rect.getX2();
							c.moveTree(-deltaX + 1, 0);
							// if this is the first child of a spouse, the spouse must be moved too
							if (c.isFirstChild()) {
								c.getSpouseParent().x += -deltaX + 1;
							}
						}
						lastChild = c;

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

	@Override
	void draw(Context context, PdfDocument pdfDocument, int firstPage, int lastPage) throws IOException {
		for (int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++) {
			for (Person p : personList) {
				if (p.isVisible() && pageIndex == p.getPageIndex())
					p.drawHorizontal(context, pdfDocument, pdfNameFont, pdfNameOLFont, pdfDateFont);
			}
		}
	}

	@Override
	float position(Context context, Person person) {
		person.setVisible(true);
		float pX = person.x;
		if (!person.hasChildren())
			pX = person.x + 1;
		PersonList spouseList = person.getSpouseList();
		for (Person spouse : spouseList) {
			spouse.y = person.y + 1;
			spouse.x = pX;
			spouse.setPageIndex(person.getPageIndex());
			// children
			PersonList childrenList = person.getChildrenList(spouse);
			for (Person child : childrenList) {
				if (!context.getParameterOptions().isExcludeSpouse()) {
					spouse.setVisible(true);
					child.y = spouse.y + 1;
				} else {
					child.y = person.y + 1;
				}
				child.x = pX;
				child.setPageIndex(person.getPageIndex());
				pX = position(context, child);
			}
		}
		return pX;
	}

}
