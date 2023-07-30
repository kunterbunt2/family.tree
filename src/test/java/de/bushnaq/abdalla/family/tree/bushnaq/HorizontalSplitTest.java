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
public class HorizontalSplitTest extends Base {

    public HorizontalSplitTest(TestInfo testInfo) {
        super("bushnaq");
    }

    protected String buildInputFileName(TestInfo testInfo) {
        return "reference/bushnaq/bushnaq.xlsx";
    }

    @Test
    @DisplayName("bushnaq family, split bottom up on A4 pages")
    public void splitBottomUpBushnaqTree(TestInfo testInfo) throws Exception {
        generate(new String[]{//
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                //,"-coordinates"//
                //,"-grid"//
                , "-cover_page"//
                , "-min_iso", "A6"//
                , "-max_iso", "A4"//
                , "-split", "BOTTOM_UP"//
        });
    }

    @Test
    @DisplayName("bushnaq family, split bottom up, Arabic, on A4 pages")
    public void splitBottomUpBushnaqArabicTree(TestInfo testInfo) throws Exception {
        generate(new String[]{//
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                //,"-coordinates"//
                //,"-grid"//
                , "-ol"
                , "-cover_page"//
                , "-min_iso", "A6"//
                , "-max_iso", "A4"//
                , "-split", "BOTTOM_UP"//
        });
    }
    @Test
    @DisplayName("bushnaq family, split top down on A4 pages")
    public void splitTopDownBushnaqTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                //, "-coordinates"//
                //, "-grid"//
                , "-cover_page"//
                , "-max_iso", "A4"//
                , "-split", "TOP_DOWN"//
        });
    }
    @Test
    @DisplayName("bushnaq family, split top down, Arabic, on A4 pages")
    public void splitTopDownBushnaqAraicTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                //, "-coordinates"//
                //, "-grid"//
                ,"-ol"
                , "-cover_page"//
                , "-max_iso", "A4"//
                , "-split", "TOP_DOWN"//
        });
    }

}
