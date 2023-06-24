package de.bushnaq.abdalla.family.tree.reference;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.family.tree.util.ExpectedResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class ATest extends Base {

    public ATest() {
        super("a");
    }


    @Test
    public void twoGenerations() throws Exception {
        PersonList personList = generate(new String[]{"-input", buildFileName(), "-family_name", getFamilyName(), "-coordinates"});
        writeResult(personList);

        ExpectedResult[] expectedResult = {//
                new ExpectedResult(2, 0f, 0f),//
                new ExpectedResult(3, 0f, 1f),//
                new ExpectedResult(4, 0f, 2f),//
                new ExpectedResult(5, 0f, 3f),//
                new ExpectedResult(6, 0f, 4f),//
                new ExpectedResult(7, 1f, 4f),//
                new ExpectedResult(8, 2f, 4f),//
        };
        testResult(personList, expectedResult);
//        for (int i = 0; i < personList.size(); i++) {
//            Person person = personList.get(i);
//            assertEquals(expectedResult[i].getX(), person.getX(), String.format("[%d] bad x", person.getId()));
//            assertEquals(expectedResult[i].getY(), person.getY(), String.format("[%d] bad y", person.getId()));
//        }
//        assertEquals( 0, main.getPageErrors().size(),"Unexpected number of errors");
    }

//    private void writeResult(PersonList personList) throws Exception {
//        FileWriter fileWriter = new FileWriter("reference/a.csv");
//        PrintWriter printWriter = new PrintWriter(fileWriter);
//        printWriter.printf("        ExpectedResult[] expectedResult = {//\n");
//        for (Person person : personList) {
//            printWriter.printf("                new ExpectedResult( %d, %.0ff, %.0ff ),//\n", person.getId(), person.getX(), person.getY());
//        }
//        printWriter.printf("        };\n");
//        printWriter.close();
//    }

}
