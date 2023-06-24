package de.bushnaq.abdalla.family.tree.bushnaq;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.Base;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class HorizontalTest extends Base {

    public HorizontalTest() {
        super("bushnaq");
    }

    @Test
    public void generate() throws Exception {
        generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-coordinates"});
    }

}
