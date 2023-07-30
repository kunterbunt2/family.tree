package de.bushnaq.abdalla.family.tree.bushnaq;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class HorizontalTest extends Base {

    public HorizontalTest() {
        super("bushnaq");
    }

    protected String buildInputFileName(TestInfo testInfo) {
        return "reference/bushnaq/bushnaq.xlsx";
    }

    @Test
    @DisplayName("full bushnaq family tree")
    public void fullBushnaqTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                //, "-coordinates"//
                //, "-grid"//
                , "-cover_page"//
                , "-min_iso", "A4"//
        });
    }
    @Test
    @DisplayName("full bushnaq family Arabic tree")
    public void fullBushnaqArabicTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                //, "-coordinates"//
                //, "-grid"//
                ,"-ol"
                , "-cover_page"//
                , "-min_iso", "A4"//
        });
    }

}
