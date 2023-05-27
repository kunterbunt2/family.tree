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
		List<Male>	firstFathers	= findRootFatherList();
		int			maxgeneration	= findMaxgeneration();

//		for (int g = maxgeneration; g > 0; g--) {
		for (int g = 3; g > 0; g--) {
			logger.info(String.format("compacting children of generation %d", g));

			for (Person firstFather : firstFathers) {
				compactChildren(firstFather, g);
			}
//			for (Person firstFather : firstFathers) {
//				compactGeneration(firstFather, g);
//			}
		}
		for (int g = 3; g > 0; g--) {
			logger.info(String.format("compacting parents of generation %d", g));
			for (Person firstFather : firstFathers) {
				compactGeneration(firstFather, g);
			}

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
					Person				lastChild	= null;
					Iterator<Person>	di			= p.getChildrenList().descendingIterator();
					while (di.hasNext()) {
						Person c = di.next();
						if (y != 0) {
							// move each child tree to be below the next child
							Rect rect = c.getTreeRect();
							if ((rect.getX2() - rect.getX1()) > 0) {
								float delta;
								if (c.getSpouseList().size() > 1)
									delta = y - c.y + context.getParameterOptions().getMinYDistanceBetweenTrees();
								else
									delta = y - c.y + context.getParameterOptions().getMinYDistanceBetweenTrees() - 1;
								c.moveTree(0, delta);
								logger.info(String.format("Move [%d]%s %s y = %d.", c.getId(), c.getFirstName(), c.getLastName(), (int) delta));
							}
						}
						Rect rect = c.getTreeRect();
						y = Math.max(rect.getY2(), y);// we can go more compact, but need to take care of second spouse situation
						lastChild = c;
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
							float	deltaX	= c.x - lastChild.x;
							float	delta	= -deltaX + 1;
							c.moveTree(delta, 0);
							logger.info(String.format("Move [%d]%s %s x = %d.", c.getId(), c.getFirstName(), c.getLastName(), (int) delta));
							// if this is the first child of a spouse, the spouse must be moved too
							if (c.isFirstChild()) {
								c.getSpouseParent().x += delta;
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
						if (lastChild != null) {
							Rect rect = lastChild.getTreeRect();
							if (c.x - rect.getX2() > 1) {
								// move child tree to be one unit right to the previous child
								float deltaX = c.x - rect.getX2();
								c.moveTree(-deltaX + 1, 0);
								logger.info(String.format("Move [%d]%s %s x = %d.", c.getId(), c.getFirstName(), c.getLastName(), (int) -deltaX + 1));
								// if this is the first child of a spouse, the spouse must be moved too
								if (c.isFirstChild()) {
									c.getSpouseParent().x += -deltaX + 1;
								}
							}
						}
						lastChild = c;
					}
				}
			}
		} else if (p.getGeneration() != null && p.getGeneration() < generation - 1)

		{
			// generation not found yet
			Iterator<Person> di = p.getChildrenList().descendingIterator();
			while (di.hasNext()) {
				Person c = di.next();
				compactGeneration(c, generation);
			}
		}

	}

	@Override
	void draw(Context context, PdfDocument pdfDocument, int pageIndex) throws IOException {
		for (Person p : personList) {
			if (p.isVisible() && pageIndex == p.getPageIndex())
				p.drawHorizontal(context, pdfDocument, pdfNameFont, pdfNameOLFont, pdfDateFont);
		}
	}

	private int findMaxgeneration() {
		int maxGenration = -1;

		for (Person p : personList) {
			if (p.getGeneration() != null)
				maxGenration = Math.max(maxGenration, p.getGeneration());
		}
		return maxGenration;
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
