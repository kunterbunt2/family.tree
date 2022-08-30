package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.util.ObfuscatingBase;

public class BushnaqFamilyTest extends Base {

	@Test
	public void generateHorizontal() throws Exception {
		obfuscate = false;
		Context context = new Context();
		generateHorizontal(context, "bushnaq", "-h");
	}

	@Test
	public void generateHorizontalFollowFemale() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.followMale = false;
		generateHorizontal(context, "bushnaq", "-h-followFemale");
	}

	@Test
	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.includeSpouse = false;
		context.followMale = false;
		generateHorizontal(context, "bushnaq", "-h-followFemale-noSpouse");
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.includeSpouse = false;
		generateHorizontal(context, "bushnaq", "-h-noSpouse");
	}

	@Test
	public void generateVertical() throws Exception {
		obfuscate = false;
		Context context = new Context();
		generateVertical(context, "bushnaq", "-v");
	}

	@Test
	public void generateVerticalFollowFemale() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.followMale = false;
		generateVertical(context, "bushnaq", "-v-followFemale");
	}

	@Test
	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.includeSpouse = false;
		context.followMale = false;
		generateVertical(context, "bushnaq", "-v-followFemale-noSpouse");
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.includeSpouse = false;
		generateVertical(context, "bushnaq", "-v-noSpouse");
	}

	@Test
	public void generateVerticalOL() throws Exception {
		obfuscate = false;
		Context context = new Context();
		context.originalLanguage = true;
		generateVertical(context, "bushnaq", "-v-ol");
	}

}
