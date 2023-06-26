package de.bushnaq.abdalla.family.tree.rahjmuw;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.util.ObfuscatingBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class RahjmuwFamilyTest extends Base {
    String familyName;

    public RahjmuwFamilyTest() throws Exception {
        super(new ObfuscatingBase().obfuscateString("bushnaq"));
    }

    @Test
    public void generateHorizontal(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-h"});
    }

    @Test
    public void generateHorizontalFollowFemale(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-h", "-follow_females"});
    }

    @Test
    public void generateHorizontalFollowFemaleWithoutSpouse(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-h", "-follow_females", "-exclude_spouse"});
    }

    @Test
    public void generateHorizontalWithoutSpouse(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-h", "-exclude_spouse"});
    }

    @Test
    public void generateVertical(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw"});
    }

    @Test
    public void generateVerticalFollowFemale(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-follow_females"});
    }

    @Test
    public void generateVerticalFollowFemaleWithoutSpouse(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-follow_females", "-exclude_spouse"});
    }

    @Test
    public void generateVerticalWithoutSpouse(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", "rahjmuw", "-exclude_spouse"});
    }

}
