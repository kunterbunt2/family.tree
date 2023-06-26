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

    @Test
    @DisplayName("bushnaq family, split bottom up on A4 pages")
    public void splitBottomUp(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", getFamilyName(), "-coordinates", "-grid", "-max_iso", "A4", "-split", "BOTTOM_UP"});
    }

    @Test
    @DisplayName("bushnaq family, split top down on A4 pages")
    public void splitTopDown(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", getFamilyName(), "-coordinates", "-grid", "-max_iso", "A4", "-split", "TOP_DOWN"});
    }

}
