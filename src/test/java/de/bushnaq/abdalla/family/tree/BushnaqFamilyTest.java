package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

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
    public void generate() throws Exception {
        generate(new String[]{"-input", buildFileName(), "-family_name", "bushnaq", "-split"});
    }

//	@Test
//	public void generateHorizontal() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-h" });
//	}
//
//	@Test
//	public void generateHorizontalCompact() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-h", "-c" });
//	}
//
//	@Test
//	public void generateHorizontalFollowFemale() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-h", "-follow_females" });
//	}
//
//	@Test
//	public void generateHorizontalFollowFemaleWithoutSpouse() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-h", "-follow_females", "-exclude_spouse" });
//	}
//
//	@Test
//	public void generateHorizontalWithoutSpouse() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-h", "-exclude_spouse" });
//	}
//
//	@Test
//	public void generateVertical() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq" });
//	}
//
//	@Test
//	public void generateVerticalCompact() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-c" });
//	}
//
//	@Test
//	public void generateVerticalCompactOL() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-c", "-ol" });
//	}
//
//	@Test
//	public void generateVerticalFollowFemale() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-follow_females" });
//	}
//
//	@Test
//	public void generateVerticalFollowFemaleWithoutSpouse() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-follow_females", "-exclude_spouse" });
//	}
//
//	@Test
//	public void generateVerticalOL() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-ol" });
//	}
//
//	@Test
//	public void generateVerticalWithoutSpouse() throws Exception {
//		generate(new String[] { "-input", buildFileName(), "-family_name", "bushnaq", "-exclude_spouse" });
//	}

}
