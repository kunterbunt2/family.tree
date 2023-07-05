package de.bushnaq.abdalla.family.tree.util;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.person.PersonList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
//@TestPropertySource
public class CreateTestData extends Base {

    private String testClassName;
//    private String testMethod;

    public CreateTestData() {
        super("bushnaq");
    }

    protected String buildInputFileName(TestInfo testInfo) {
        return String.format("reference/%s/%s.xlsx", getFamilyName(), getFamilyName());
    }

    protected String buildOutputFileName(TestInfo testInfo) throws IOException {
        return String.format("reference/%s/%s.xlsx", testClassName, testClassName);
    }

    @Test
    @DisplayName("generate date for ETest")
    public void generateETestData(TestInfo testInfo) throws Exception {
        testClassName = "ETest";
        String[] args = new String[]{//
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
                , "-coordinates"//
                , "-grid"//
                , "-min_iso", "A4"//
        };

        context.getParameterOptions().start(args);

        String inputFileName = context.getParameterOptions().getInput();
        String outputFileName = context.getParameterOptions().getOutput();

        PersonList personList = main.importPersonList(inputFileName);

        filterSubtree(personList, 147);
//        removeSubTree(personList,148);
        removeSubTree(personList, 149);
        removeSubTree(personList, 156);
        removeSubTree(personList, 162);
        removeSubTree(personList, 171);

        removeSubTree(personList, 177);
        removeSubTree(personList, 187);
        removeSubTree(personList, 192);
        logger.info(String.format("Left %d person in tree.", personList.size()));
        writeExcel(outputFileName, personList);
    }


}
