package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.util.ObfuscatingBase;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class RahjmyuFamilyTest extends Base {
	String					familyName;
	private ObfuscatingBase	ObfuscatingBase	= new ObfuscatingBase();

	public RahjmyuFamilyTest() throws Exception {
		familyName = ObfuscatingBase.obfuscateString("bushnaq");
	}

	@Test
	public void generateHorizontal() throws Exception {
		generate(new String[] { "-input", familyName, "-h" });
	}

	@Test
	public void generateHorizontalFollowFemale() throws Exception {
		generate(new String[] { "-input", familyName, "-h", "follow_females" });
	}

	@Test
	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", familyName, "-h", "follow_females", "exclude_spouse" });
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", familyName, "-h", "exclude_spouse" });
	}

	@Test
	public void generateVertical() throws Exception {
		generate(new String[] { "-input", familyName });
	}

	@Test
	public void generateVerticalFollowFemale() throws Exception {
		generate(new String[] { "-input", familyName, "follow_females" });
	}

	@Test
	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", familyName, "follow_females", "exclude_spouse" });
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", familyName, "exclude_spouse" });
	}

}
