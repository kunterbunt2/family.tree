package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class BushnaqFamilyTest extends Base {
	String familyName;

	public BushnaqFamilyTest() throws Exception {
		familyName = "bushnaq";
	}

	private String buildFileName() {
		return "bushnaq/" + familyName + ".xlsx";
	}

	@Test
	public void generateHorizontal() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-h" });
	}

	@Test
	public void generateHorizontalFollowFemale() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-h", "-follow_females" });
	}

	@Test
	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-h", "-follow_females", "-exclude_spouse" });
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-h", "-exclude_spouse" });
	}

	@Test
	public void generateVertical() throws Exception {
		generate(new String[] { "-input", buildFileName() });
	}

	@Test
	public void generateVerticalFollowFemale() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-follow_females" });
	}

	@Test
	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-follow_females", "-exclude_spouse" });
	}

	@Test
	public void generateVerticalOL() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-ol" });
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-exclude_spouse" });
	}

}
