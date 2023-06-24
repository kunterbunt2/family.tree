package de.bushnaq.abdalla.family.tree.bushnaq;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class HorizontalSplitTest extends Base {
//    final String familyName;

    public HorizontalSplitTest() {
        super("bushnaq");
    }

    @Test
    public void splitBottomUp() throws Exception {
        generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-split", "BOTTOM_UP"});
    }

    @Test
    public void splitTopDown() throws Exception {
        generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-split", "TOP_DOWN"});
    }

}
