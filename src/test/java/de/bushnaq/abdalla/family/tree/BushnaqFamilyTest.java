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

	@Test
	public void generateHorizontal() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-h" });
	}

	@Test
	public void generateHorizontalFollowFemale() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-h", "-follow_females" });
	}

	@Test
	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-h", "-follow_females", "exclude_spouse" });
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-h", "exclude_spouse" });
	}

	@Test
	public void generateVertical() throws Exception {
		generate(new String[] { "-input", "bushnaq" });
	}

	@Test
	public void generateVerticalFollowFemale() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-follow_females" });
	}

	@Test
	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-follow_females", "exclude_spouse" });
	}

	@Test
	public void generateVerticalOL() throws Exception {
		generate(new String[] { "-input", "bushnaq", "-ol" });
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", "bushnaq", "exclude_spouse" });
	}

}
