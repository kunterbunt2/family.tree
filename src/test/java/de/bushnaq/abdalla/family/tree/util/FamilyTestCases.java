package de.bushnaq.abdalla.family.tree.util;

import org.junit.jupiter.api.*;

@Disabled("Not intended to be executed by its own, but rather extended")
public class FamilyTestCases extends Base {
    public FamilyTestCases(String familyName) {
        super(familyName);
    }

    protected String buildInputFileName(TestInfo testInfo) {
        return "reference/" + getFamilyName() + "/" + getFamilyName() + ".xlsx";
    }

    @Test
    @Order(2)
    @DisplayName("full Arabic tree")
    public void fullArabicTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-ol"
                , "-cover_page"//
                , "-min_iso", "A4"//
        });
    }

    @Test
    @Order(1)
    @DisplayName("full tree")
    public void fullTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-cover_page"//
                , "-min_iso", "A4"//
        });
    }

    @Test
    @Order(4)
    @DisplayName("split bottom-up Arabic tree, on A4 pages")
    public void splitBottomUpArabicTree(TestInfo testInfo) throws Exception {
        generate(new String[]{//
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-ol"
                , "-cover_page"//
                , "-min_iso", "A6"//
                , "-max_iso", "A4"//
                , "-split", "BOTTOM_UP"//
        });
    }

    @Test
    @Order(3)
    @DisplayName("split bottom-up tree on A4 pages")
    public void splitBottomUpTree(TestInfo testInfo) throws Exception {
        generate(new String[]{//
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-cover_page"//
                , "-min_iso", "A6"//
                , "-max_iso", "A4"//
                , "-split", "BOTTOM_UP"//
        });
    }

    @Test
    @Order(6)
    @DisplayName("split top-down arabic tree, on A4 pages")
    public void splitTopDownArabicTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-ol"
                , "-cover_page"//
                , "-max_iso", "A4"//
                , "-split", "TOP_DOWN"//
        });
    }

    @Test
    @Order(5)
    @DisplayName("split top-down tree, on A4 pages")
    public void splitTopDownTree(TestInfo testInfo) throws Exception {
        generate(new String[]{
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-cover_page"//
                , "-max_iso", "A4"//
                , "-split", "TOP_DOWN"//
        });
    }
}
