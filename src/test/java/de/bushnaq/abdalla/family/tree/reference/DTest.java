package de.bushnaq.abdalla.family.tree.reference;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.family.tree.util.ExpectedResult;
import de.bushnaq.abdalla.pdf.IsoPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class DTest extends Base {

    public DTest() {
        super("d");
    }


    @Test
    @DisplayName("2 generations, cannot be compacted, does not fit page")
    public void generate() throws Exception {
        PersonList personList = generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-coordinates", "-grid", "-max_iso", "A4", "-min_iso", "A4", "-split", "TOP_DOWN"});
        writeResult(personList);

        ExpectedResult[] expectedResult = {//
                new ExpectedResult(2, 0f, 0f, 0),//
                new ExpectedResult(3, 0f, 1f, 0),//
                new ExpectedResult(4, 0f, 2f, 0),//
                new ExpectedResult(5, 1f, 2f, 0),//
                new ExpectedResult(6, 2f, 2f, 0),//
                new ExpectedResult(7, 3f, 2f, 0),//
                new ExpectedResult(8, 4f, 2f, 0),//
                new ExpectedResult(9, 5f, 2f, 0),//
                new ExpectedResult(10, 6f, 2f, 0),//
                new ExpectedResult(11, 7f, 2f, 0),//
                new ExpectedResult(12, 8f, 2f, 0),//
                new ExpectedResult(13, 9f, 2f, 0),//
        };
        testResult(personList, expectedResult, new IsoPage(PDRectangle.A4, "A4"));
    }


}
