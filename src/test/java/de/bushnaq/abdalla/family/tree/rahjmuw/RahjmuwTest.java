package de.bushnaq.abdalla.family.tree.rahjmuw;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.tree.util.FamilyTestCases;
import de.bushnaq.abdalla.family.tree.util.Obfuscator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class RahjmuwTest extends FamilyTestCases {

    public RahjmuwTest() {
        super(new Obfuscator().obfuscateString("bushnaq"));
    }

}
