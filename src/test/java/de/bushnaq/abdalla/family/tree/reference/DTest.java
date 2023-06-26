package de.bushnaq.abdalla.family.tree.reference;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.family.tree.util.ExpectedResult;
import de.bushnaq.abdalla.pdf.IsoPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class DTest extends Base {

    public DTest() {
        super("d");
    }

    @Test
    @DisplayName("2 generations, split bottom up, cannot be compacted, does not fit page")
    public void splitBottomUp(TestInfo testInfo) throws Exception {
        PersonList personList = generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", getFamilyName(), "-coordinates", "-grid", "-max_iso", "A4", "-min_iso", "A4", "-split", "BOTTOM_UP"});
        writeResult(personList, testInfo);
        List<ExpectedResult> expectedResultList = readResult(testInfo);
        testResult(personList, expectedResultList, new IsoPage(PDRectangle.A4, "A4"));
    }

    @Test
    @DisplayName("2 generations, split top down, cannot be compacted, does not fit page")
    public void splitTopDown(TestInfo testInfo) throws Exception {
        PersonList personList = generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", getFamilyName(), "-coordinates", "-grid", "-max_iso", "A4", "-min_iso", "A4", "-split", "TOP_DOWN"});
        writeResult(personList, testInfo);
        List<ExpectedResult> expectedResultList = readResult(testInfo);
        testResult(personList, expectedResultList, new IsoPage(PDRectangle.A4, "A4"));
    }

}
