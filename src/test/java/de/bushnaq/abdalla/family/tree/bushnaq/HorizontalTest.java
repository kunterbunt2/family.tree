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

    @Test
    @DisplayName("bushnaq family")
    public void generate(TestInfo testInfo) throws Exception {
        generate(new String[]{"-input", buildInputFileName(testInfo), "-output", buildOutputFileName(testInfo), "-family_name", getFamilyName()});
    }

}
