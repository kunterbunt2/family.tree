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
public class RahjmuwFamilyTest extends Base {
	String					familyName;
	private ObfuscatingBase	ObfuscatingBase	= new ObfuscatingBase();

	public RahjmuwFamilyTest() throws Exception {
		familyName = ObfuscatingBase.obfuscateString("bushnaq");
	}

	private String buildFileName() {
		return "examples/" + familyName + ".xlsx";
	}

	@Test
	public void generateHorizontal() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-h" });
	}

	@Test
	public void generateHorizontalFollowFemale() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-h", "-follow_females" });
	}

	@Test
	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-h", "-follow_females", "-exclude_spouse" });
	}

	@Test
	public void generateHorizontalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-h", "-exclude_spouse" });
	}

	@Test
	public void generateVertical() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw" });
	}

	@Test
	public void generateVerticalFollowFemale() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-follow_females" });
	}

	@Test
	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-follow_females", "-exclude_spouse" });
	}

	@Test
	public void generateVerticalWithoutSpouse() throws Exception {
		generate(new String[] { "-input", buildFileName(), "-family_name", "rahjmuw", "-exclude_spouse" });
	}

}
