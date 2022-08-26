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
	public void generateHorizontalFollowFemale() throws Exception {
		Context context = new Context();
		context.followMale = false;
		generateHorizontal(context, "bushnaq", "bushnaq-h-followFemale");
	}

	@Test
	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		context.followMale = false;
		generateHorizontal(context, "bushnaq", "bushnaq-h-followFemale-noSpouse");
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		generateHorizontal(context, "bushnaq", "bushnaq-h-noSpouse");
	}

	@Test
	public void generateVertical() throws Exception {
		Context context = new Context();
		generateVertical(context, "bushnaq", "bushnaq-v");
	}

	@Test
	public void generateVerticalFollowFemale() throws Exception {
		Context context = new Context();
		context.followMale = false;
		generateVertical(context, "bushnaq", "bushnaq-v-followFemale");
	}

	@Test
	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		context.followMale = false;
		generateVertical(context, "bushnaq", "bushnaq-v-followFemale-noSpouse");
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		Context context = new Context();
		context.includeSpouse = false;
		generateVertical(context, "bushnaq", "bushnaq-v-noSpouse");
	}

}
