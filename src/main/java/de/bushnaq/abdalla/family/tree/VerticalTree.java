package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;

public class VerticalTree extends Tree {

	@Override
	int position(Context context, Person person) {
		logger.info(String.format("positioning %s", person.toString()));
		int			width		= (int) (person.x + person.width + Person.PERSON_X_SPACE);
		PersonList	spouseList	= person.getSpouseList();
		for (Person spouse : spouseList) {
			if (person.isLastChild()) {
				spouse.setSpouseOfLastChild(true);
			}

			spouse.x = width;
			spouse.y = person.y;
			spouse.setSpouse(true);
			person.nextPersonX = width;
			// children
			boolean		firstChild		= true;
//			Person		lastChild		= null;
			PersonList	childrenList	= person.getChildrenList(spouse);
			for (Person child : childrenList) {
				child.setIsChild(true);
				if (firstChild) {
					child.setFirstChild(true);
					firstChild = false;
				}
				if (child.equals(childrenList.last())) {
					child.setLastChild(true);
				}
				child.x = width;
				child.y = (int) (spouse.y + Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE);
				width = position(context, child);
//				lastChild = child;
			}
			spouse.nextPersonX = width;
		}
		return width;
	}

}
