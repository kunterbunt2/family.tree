package de.bushnaq.abdalla.family.tree.walczak;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.FamilyTestCases;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class WalczakTest extends FamilyTestCases {

    public WalczakTest() {
        super("walczak");
    }

}
