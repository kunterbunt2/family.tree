package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;

public class HorizontalTree extends Tree {

	@Override
	int position(Context context, Person person) {
		person.attribute.show = true;
		person.attribute.member = true;
		logger.info(String.format("positioning %s", person.toString()));
		if (context.includeSpouse) {
			int pX;
			if (person.getSpouseList().size() == 1)
				pX = (int) (person.x);
			else
				pX = (int) (person.x + person.width + Person.PERSON_X_SPACE);
			person.nextPersonX = pX;
			PersonList	spouseList	= person.getSpouseList();
			int			childIndex	= 0;
			for (Person spouse : spouseList) {
				if (person.isLastChild()) {
					spouse.setSpouseOfLastChild(true);
				}

				if (person.getSpouseList().size() == 1)
					spouse.x = (int) (pX + person.width + Person.PERSON_X_SPACE);
				else
					spouse.x = pX;
				spouse.y = person.y;
				spouse.setSpouse(true);
				person.nextPersonX = pX;
				// children
				boolean		firstChild		= true;
				PersonList	childrenList	= person.getChildrenList(spouse);
				for (Person child : childrenList) {
					child.childIndex = childIndex++;
					spouse.attribute.show = true;
					child.setIsChild(true);
					if (firstChild) {
						child.setFirstChild(true);
						firstChild = false;
					}
					if (child.equals(childrenList.last())) {
						child.setLastChild(true);
					}
					child.x = pX;
					child.y = (int) (spouse.y + Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE);
					pX = position(context, child);
				}
				if (person.getSpouseList().size() == 1) {
					pX = Math.max(pX, (int) (spouse.x + spouse.width + Person.PERSON_X_SPACE));
					person.nextPersonX = pX;
					spouse.nextPersonX = pX;
				} else {
					spouse.nextPersonX = pX;
				}
			}
			return pX;
		} else {
			int			pX;
			boolean		firstChild		= true;
			PersonList	childrenList	= person.getChildrenList();
			if (person.hasChildren())
				pX = (int) (person.x);
			else
				pX = (int) (person.x + person.width + Person.PERSON_X_SPACE);
			person.nextPersonX = pX;
			int childIndex = 0;
			for (Person child : childrenList) {
				child.childIndex = childIndex++;
				child.setIsChild(true);
				if (firstChild) {
					child.setFirstChild(true);
					firstChild = false;
				}
				if (child.equals(childrenList.last())) {
					child.setLastChild(true);
				}
				child.x = pX;
				child.y = (int) (person.y + Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE);
				pX = position(context, child);
			}
			pX = Math.max(pX, (int) (person.x + person.width + Person.PERSON_X_SPACE));
			person.nextPersonX = pX;
			return (int) (pX);
		}
	}

}
