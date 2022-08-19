package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.tree.util.Base;

public class BushnaqFamilyTest extends Base {

	@Test
	public void generate() throws Exception {
		Context context = new Context();
		generate(context, "bushnaq", "bushnaq");
	}

	@Test
	public void generateWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		generate(context, "bushnaq", "bushnaq-no-spouse");
	}

}
