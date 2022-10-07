package de.bushnaq.abdalla.family.tree;

import java.awt.Graphics2D;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;

public class HorizontalTree extends Tree {
	public HorizontalTree(Context context) {
		super(context);
	}

	@Override
	int calclateImageWidth() {
		int	minX	= Integer.MAX_VALUE;
		int	maxX	= Integer.MIN_VALUE;
		for (Person p : personList) {
			minX = Math.min(minX, (p.x));
			maxX = Math.max(maxX, p.x + Person.getWidth(context));
		}
		return maxX;
	}

	@Override
	int calculateImageHeight(Context context) {
		int	minY	= Integer.MAX_VALUE;
		int	maxY	= Integer.MIN_VALUE;
		for (Person p : personList) {
			minY = Math.min(minY, p.y);
			maxY = Math.max(maxY, p.y);
		}
		return maxY + Person.getHeight(context);
	}

	@Override
	void draw(Context context, Graphics2D graphics) {
		for (Person p : personList) {
			p.drawHorizontal(context, graphics, nameFont, livedFont);
		}
	}

	@Override
	int position(Context context, Person person) {
		person.setVisible(true);
//		person.attribute.member = true;
		if (!context.getParameterOptions().isExcludeSpouse()) {
			int pX;
			if (person.getSpouseList().size() == 1)
				pX = (person.x);
			else
				pX = person.x + Person.getWidth(context) + Person.getXSpace(context);
			person.nextPersonX = pX;
			PersonList spouseList = person.getSpouseList();
//			int			childIndex	= 0;
			for (Person spouse : spouseList) {
//				if (person.isLastChild()) {
//					spouse.setSpouseOfLastChild(true);
//				}

				if (person.getSpouseList().size() == 1)
					spouse.x = pX + Person.getWidth(context) + Person.getXSpace(context);
				else
					spouse.x = pX;
				spouse.y = person.y;
//				spouse.setSpouse(true);
				person.nextPersonX = pX;
				// children
//				boolean		firstChild		= true;
				PersonList childrenList = person.getChildrenList(spouse);
				for (Person child : childrenList) {
//					child.childIndex = childIndex++;
					spouse.setVisible(true);
//					child.setIsChild(true);
//					if (firstChild) {
//						child.setFirstChild(true);
//						firstChild = false;
//					}
//					if (child.equals(childrenList.last())) {
//						child.setLastChild(true);
//					}
					child.x = pX;
					child.y = spouse.y + Person.getHeight(context) + Person.getYSpace(context);
					pX = position(context, child);
				}
				if (person.getSpouseList().size() == 1) {
					pX = Math.max(pX, spouse.x + Person.getWidth(context) + Person.getXSpace(context));
					person.nextPersonX = pX;
					spouse.nextPersonX = pX;
				} else {
					spouse.nextPersonX = pX;
				}
			}
			return pX;
		} else {
			int			pX;
//			boolean		firstChild		= true;
			PersonList	childrenList	= person.getChildrenList();
			if (person.hasChildren())
				pX = (person.x);
			else
				pX = person.x + Person.getWidth(context) + Person.getXSpace(context);
			person.nextPersonX = pX;
//			int childIndex = 0;
			for (Person child : childrenList) {
//				child.childIndex = childIndex++;
//				child.setIsChild(true);
//				if (firstChild) {
//					child.setFirstChild(true);
//					firstChild = false;
//				}
//				if (child.equals(childrenList.last())) {
//					child.setLastChild(true);
//				}
				child.x = pX;
				child.y = person.y + Person.getHeight(context) + Person.getYSpace(context);
				pX = position(context, child);
			}
			pX = Math.max(pX, person.x + Person.getWidth(context) + Person.getXSpace(context));
			person.nextPersonX = pX;
			return (pX);
		}
	}

}
