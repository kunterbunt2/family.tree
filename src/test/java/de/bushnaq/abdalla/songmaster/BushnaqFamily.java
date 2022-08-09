package de.bushnaq.abdalla.songmaster;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import de.bushnaq.abdalla.family.tree.Female;
import de.bushnaq.abdalla.family.tree.Male;
import de.bushnaq.abdalla.family.tree.TreeMaster;

public class BushnaqFamily {
	TreeMaster treeMaster = new TreeMaster();

	private Female addFemale(String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		return treeMaster.addFemale(firstName, lastName, born, died, father, mother);
	}

	private Male addMale(String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		return treeMaster.addMale(firstName, lastName, born, died, father, mother);
	}

	@Test
	public void generate() throws Exception {
		{
			// NABIL BUSHNAQ
			Male	nabilBushnaq	= addMale("Nabil", "Bushnaq", new GregorianCalendar(1940, Calendar.JANUARY, 8), null, null, null);	// birth year tbd
			Female	utePlato		= addFemale("Ute", "Plato", new GregorianCalendar(1943, Calendar.FEBRUARY, 7), null, null, null);
			{
				Male	abdallaBushnaq		= addMale("Abdalla", "Bushnaq", new GregorianCalendar(1964, Calendar.DECEMBER, 22), null, nabilBushnaq, utePlato);
				Female	jessicaHientzsch	= addFemale("Jessica", "Hientzsch", new GregorianCalendar(1965, Calendar.APRIL, 19), null, null, null);
				{
					Male luciusBushnaq = addMale("Lucius", "Bushnaq", new GregorianCalendar(1994, Calendar.MARCH, 5), null, abdallaBushnaq, jessicaHientzsch);
					luciusBushnaq.addAdditionalName("Nabil");
				}
			}
			{
				Female abierBushnaq = addFemale("Abier", "Bushnaq", new GregorianCalendar(1965, Calendar.APRIL, 25), null, nabilBushnaq, utePlato);
			}
			{
				Male	hikmarBushnaq	= addMale("Hikmat", "Bushnaq", new GregorianCalendar(1970, Calendar.OCTOBER, 17), null, nabilBushnaq, utePlato);// birth year tbd
				Female	coraJosting		= addFemale("Cora", "Josting", new GregorianCalendar(1970, Calendar.FEBRUARY, 7), null, null, null);			// birth year tbd
				{
					Female constanzeShuruqBushnaqJosting = addFemale("Constanze", "Bushnaq Josting", new GregorianCalendar(1998, Calendar.AUGUST, 18), null, hikmarBushnaq, coraJosting);// birth year tbd
					constanzeShuruqBushnaqJosting.addAdditionalName("Shuruq");
					constanzeShuruqBushnaqJosting.addAdditionalName("Magnolia");
				}
			}
		}
		treeMaster.generate("bushnaq");
	}

}
