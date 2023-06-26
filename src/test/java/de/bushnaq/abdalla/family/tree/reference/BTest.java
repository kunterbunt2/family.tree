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
public class BTest extends Base {

    public BTest() {
        super("b");
    }


    @Test
    @DisplayName("3 generations, 2 subtrees, can be compacted")
    public void generate(TestInfo testInfo) throws Exception {
        PersonList personList = generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", getFamilyName(), "-coordinates", "-grid", "-min_iso", "A4"});
        writeResult(personList, testInfo);
        List<ExpectedResult> expectedResultList = readResult(testInfo);
        testResult(personList, expectedResultList, new IsoPage(PDRectangle.A4, "A4"));
    }


}
