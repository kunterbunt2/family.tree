package de.bushnaq.abdalla.family.tree.hientzsch;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.FamilyTestCases;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class HientzschTest extends FamilyTestCases {

    public HientzschTest() {
        super("hientzsch");
    }

}
