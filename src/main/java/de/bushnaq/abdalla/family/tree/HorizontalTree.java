package de.bushnaq.abdalla.family.tree;

import java.awt.Graphics2D;
import java.io.IOException;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.pdf.PdfDocument;

public class HorizontalTree extends Tree {

	public HorizontalTree(Context context) {
		super(context);
	}

	@Override
	int calclateImageWidth() {
		int	minX	= Integer.MAX_VALUE;
		int	maxX	= Integer.MIN_VALUE;
		for (Person p : personList) {
			int x = p.x * (Person.getWidth(context) + Person.getXSpace(context));
			minX = Math.min(minX, (x));
			maxX = Math.max(maxX, x + Person.getWidth(context));
		}
		return maxX;
	}

	@Override
	int calculateImageHeight(Context context) {
		int	minY	= Integer.MAX_VALUE;
		int	maxY	= Integer.MIN_VALUE;
		for (Person p : personList) {
			int y = p.y * (Person.getHeight(context) + Person.getYSpace(context));
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
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
	void draw(Context context, PdfDocument pdfDocument) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	int position(Context context, Person person) {
		person.setVisible(true);
		int pX = person.x;
		if (!person.hasChildren())
			pX = person.x + 1;
		PersonList spouseList = person.getSpouseList();
		for (Person spouse : spouseList) {
			spouse.y = person.y + 1;
			spouse.x = pX;
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
				pX = position(context, child);
			}
		}
		return pX;
//		person.setVisible(true);
//		if (!context.getParameterOptions().isExcludeSpouse()) {
//			int pX;
//			if (person.getSpouseList().size() == 1)
//				pX = (person.x);
//			else
//				pX = person.x + Person.getWidth(context) + Person.getXSpace(context);
//			person.nextPersonX = pX;
//			PersonList spouseList = person.getSpouseList();
//			for (Person spouse : spouseList) {
//
//				if (person.getSpouseList().size() == 1)
//					spouse.x = pX + Person.getWidth(context) + Person.getXSpace(context);
//				else
//					spouse.x = pX;
//				spouse.y = person.y;
//				person.nextPersonX = pX;
//				// children
//				PersonList childrenList = person.getChildrenList(spouse);
//				for (Person child : childrenList) {
//					spouse.setVisible(true);
//					child.x = pX;
//					child.y = spouse.y + Person.getHeight(context) + Person.getYSpace(context);
//					pX = position(context, child);
//				}
//				if (person.getSpouseList().size() == 1) {
//					pX = Math.max(pX, spouse.x + Person.getWidth(context) + Person.getXSpace(context));
//					person.nextPersonX = pX;
//					spouse.nextPersonX = pX;
//				} else {
//					spouse.nextPersonX = pX;
//				}
//			}
//			return pX;
//		} else {
//			int			pX;
//			PersonList	childrenList	= person.getChildrenList();
//			if (person.hasChildren())
//				pX = (person.x);
//			else
//				pX = person.x + Person.getWidth(context) + Person.getXSpace(context);
//			person.nextPersonX = pX;
//			for (Person child : childrenList) {
//				child.x = pX;
//				child.y = person.y + Person.getHeight(context) + Person.getYSpace(context);
//				pX = position(context, child);
//			}
//			pX = Math.max(pX, person.x + Person.getWidth(context) + Person.getXSpace(context));
//			person.nextPersonX = pX;
//			return (pX);
//		}
	}

}
