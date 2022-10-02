package de.bushnaq.abdalla.family.tree;

import java.awt.Graphics2D;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;

public class VerticalTree extends Tree {

	public VerticalTree(Context context) {
		super(context);
	}

	@Override
	int calclateImageWidth() {
		int	minX	= Integer.MAX_VALUE;
		int	maxX	= Integer.MIN_VALUE;
		for (Person p : personList) {
			int x = p.x * (p.width + Person.PERSON_X_SPACE);
			minX = Math.min(minX, (x));
			maxX = Math.max(maxX, x + p.width);
		}
		return maxX;
	}

	@Override
	int calculateImageHeight() {
		int	minY	= Integer.MAX_VALUE;
		int	maxY	= Integer.MIN_VALUE;
		for (Person p : personList) {
			int y = p.y * (Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}
		return maxY + Person.PERSON_HEIGHT;
	}

	@Override
	void draw(Context context, Graphics2D graphics) {
		for (Person p : personList) {
			p.drawVertical(context, graphics, nameFont, livedFont);
		}
	}

	@Override
	int position(Context context, Person person) {
		person.setVisible(true);
		int pY = person.y;
		if (!person.hasChildren())
			pY = person.y + 1;
		PersonList spouseList = person.getSpouseList();
		for (Person spouse : spouseList) {
			spouse.x = person.x + 1;
			spouse.y = pY/* person.y + spouse.spouseIndex */;
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
				pY = position(context, child);
			}
		}
		return pY;
	}

}
