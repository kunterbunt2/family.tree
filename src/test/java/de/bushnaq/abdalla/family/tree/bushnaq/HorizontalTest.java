package de.bushnaq.abdalla.family.tree.bushnaq;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class HorizontalTest extends Base {
//    final String familyName;

    public HorizontalTest() {
        super("bushnaq");
    }

//    private String buildFileName() {
//        return "bushnaq/" + familyName + ".xlsx";
//    }

    @Test
    public void generate() throws Exception {
        generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-coordinates"});
    }

}
