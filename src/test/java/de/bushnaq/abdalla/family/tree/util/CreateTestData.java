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

        filterSubtree(personList, 148);// Ali Bushnaq (Rizvanbeg)
        removeSubTree(personList, 150);// Muhammad Bushnaq (Rizvanbeg) (child of Amina Duzdar)
        removeSubTree(personList, 157);// La'iqa Bushnaq (Rizvanbeg) (child of Amina Duzdar)
        removeSubTree(personList, 163);// Nawzat Bushnaq (Rizvanbeg) (child of Amina Duzdar)
        removeSubTree(personList, 172);// Sadiq Bushnaq (Rizvanbeg) (child of Amina Duzdar)

        removeSubTree(personList, 178);// Sami Bushnaq (Rizvanbeg) (child of Amina Bushnaq (Hadzajic))
        removeSubTree(personList, 188);// Said Bushnaq (Rizvanbeg) (child of Amina Bushnaq (Hadzajic))
        removeSubTree(personList, 193);// Sa'adat Bushnaq (Rizvanbeg) (child of Amina Bushnaq (Hadzajic))
        logger.info(String.format("Left %d person in tree.", personList.size()));
        writeExcel(outputFileName, personList);
    }


}
