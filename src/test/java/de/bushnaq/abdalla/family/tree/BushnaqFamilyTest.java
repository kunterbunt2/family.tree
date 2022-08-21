package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.tree.util.Base;

public class BushnaqFamilyTest extends Base {

	@Test
	public void generateHorizontal() throws Exception {
		Context context = new Context();
		generateHorizontal(context, "bushnaq", "bushnaq-h");
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		generateHorizontal(context, "bushnaq", "bushnaq-h-no-spouse");
	}

	@Test
	public void generateVertical() throws Exception {
		Context context = new Context();
		generateVertical(context, "bushnaq", "bushnaq-v");
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		generateVertical(context, "bushnaq", "bushnaq-v-no-spouse");
	}

}
