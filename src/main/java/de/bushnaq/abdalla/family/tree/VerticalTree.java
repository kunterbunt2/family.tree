package de.bushnaq.abdalla.family.tree;

import java.io.IOException;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.pdf.PdfDocument;

public class VerticalTree extends Tree {

	public VerticalTree(Context context, PersonList personList) {
		super(context, personList);
	}

	@Override
	protected void compact(Context context2, PdfDocument pdfDocument) {
		// TODO Auto-generated method stub

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
