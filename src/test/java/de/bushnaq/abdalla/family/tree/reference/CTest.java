package de.bushnaq.abdalla.family.tree.reference;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.family.tree.util.ExpectedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class CTest extends Base {

    public CTest() {
        super("c");
    }


    @Test
    @DisplayName("3 generations, 2 subtrees, 2 spouse, can be compacted")
    public void generate() throws Exception {
        PersonList personList = generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-coordinates"});
        writeResult(personList);

        ExpectedResult[] expectedResult = {//
                new ExpectedResult( 2, 0f, 0f ),//
                new ExpectedResult( 3, 0f, 1f ),//
                new ExpectedResult( 4, 0f, 4f ),//
                new ExpectedResult( 5, 0f, 5f ),//
                new ExpectedResult( 6, 0f, 6f ),//
                new ExpectedResult( 7, 1f, 6f ),//
                new ExpectedResult( 8, 2f, 6f ),//
                new ExpectedResult( 9, 3f, 5f ),//
                new ExpectedResult( 10, 3f, 6f ),//
                new ExpectedResult( 11, 4f, 6f ),//
                new ExpectedResult( 12, 5f, 6f ),//
                new ExpectedResult( 13, 1f, 2f ),//
                new ExpectedResult( 14, 1f, 3f ),//
                new ExpectedResult( 15, 1f, 4f ),//
                new ExpectedResult( 16, 2f, 4f ),//
                new ExpectedResult( 17, 3f, 4f ),//
        };
        testResult(personList, expectedResult);
    }


}
