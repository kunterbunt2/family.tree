package de.bushnaq.abdalla.family.tree;

import java.io.IOException;
import java.util.List;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Male;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.pdf.PdfDocument;

public class VerticalTree extends Tree {

	public VerticalTree(Context context) {
		super(context);
	}

	@Override
	float calclateImageWidth() {
		float	minX	= Integer.MAX_VALUE;
		float	maxX	= Integer.MIN_VALUE;
		for (Person p : personList) {
			float x = p.x * (Person.getWidth(context) + Person.getXSpace(context));
			minX = Math.min(minX, (x));
			maxX = Math.max(maxX, x + Person.getWidth(context));
		}
		return maxX;
	}

	@Override
	float calculateImageHeight(Context context) {
		float	minY	= Integer.MAX_VALUE;
		float	maxY	= Integer.MIN_VALUE;
		for (Person p : personList) {
			float y = p.y * (Person.getHeight(context) + Person.getYSpace(context));
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}
		return maxY + Person.getHeight(context);
	}

//	@Override
//	void draw(Context context, Graphics2D graphics) {
//		for (Person p : personList) {
//			p.drawVertical(context, graphics, nameFont, livedFont);
//		}
//	}

	@Override
	void draw(Context context, PdfDocument pdfDocument, int numberOfRootFathers) throws IOException {
		{
			List<Male>	rootFatherList	= findRootFatherList();
			int			pageIndex		= 0;
			for (Person rootFather : rootFatherList) {
				String outputDecorator = context.getParameterOptions().getOutputDecorator();
				pdfDocument.createPage(pageIndex++, rootFather.getFirstName() + context.getParameterOptions().getOutputDecorator());
			}
		}
		for (int pageIndex = 0; pageIndex < numberOfRootFathers; pageIndex++) {
//			int i = 10;
			for (Person p : personList) {
				if (p.isVisible() && pageIndex == p.getPageIndex())
					p.drawVertical(context, pdfDocument, pdfNameFont, pdfNameOLFont, pdfDateFont);
			}
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
