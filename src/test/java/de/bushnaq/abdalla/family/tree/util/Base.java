package de.bushnaq.abdalla.family.tree.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.Main;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.pdf.IsoPage;
import de.bushnaq.abdalla.pdf.PdfDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected Context context;
    @Autowired
    protected Main main;
    Map<Person, Integer> personToIndexMap = new HashMap<>();
    private String familyName = null;

    public Base(String familyName) {
        this.familyName = familyName;
    }

    public Base() {
    }


    protected String buildInputFileName(TestInfo testInfo) {
        String testClassName = testInfo.getTestClass().get().getSimpleName();
        return String.format("reference/%s/%s.xlsx", testClassName, testClassName);
    }

    protected String buildOutputFileName(TestInfo testInfo) throws IOException {
        String testClassName = testInfo.getTestClass().get().getSimpleName();
        String testMethod = testInfo.getTestMethod().get().getName();
        Files.createDirectories(Paths.get(String.format("output/%s", testClassName)));
        return String.format("output/%s/%s-%s.pdf", testClassName, testMethod, getFootertext());
    }


    public PersonList generate(String[] args) throws Exception {
        PersonList personList = main.start(args);
        return personList;
    }

    public String getFamilyName() {
        return familyName;
    }

    private String getFootertext() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        //get current date time with Date()
        String date = dateFormat.format(new Date());
        return String.format("%s", date);
    }

    protected List<ExpectedResult> readResult(TestInfo testInfo) throws Exception {
        String testClassName = testInfo.getTestClass().get().getSimpleName();
        String testMethod = testInfo.getTestMethod().get().getName();
        String fileName = String.format("reference/%s/%s.gson", testClassName, testMethod);
        Type listType = new TypeToken<List<ExpectedResult>>() {
        }.getType();
        JsonReader reader = new JsonReader(new FileReader(fileName));
        Gson gson = new Gson();
        List<ExpectedResult> expectedResultList = gson.fromJson(reader, listType);
        return expectedResultList;
    }


    protected void testResult(PersonList personList, List<ExpectedResult> expectedResult, IsoPage minPage) {
        for (int i = 0; i < personList.size(); i++) {
            Person person = personList.get(i);
            assertEquals(expectedResult.get(i).getId(), person.getId(), String.format("[%d] bad id", person.getId()));
            assertEquals(expectedResult.get(i).getX(), person.getX(), String.format("[%d] bad x", person.getId()));
            assertEquals(expectedResult.get(i).getY(), person.getY(), String.format("[%d] bad y", person.getId()));
            assertEquals(expectedResult.get(i).getPageIndex(), person.getPageIndex(), String.format("[%d] bad pageIndex", person.getId()));
            PdfDocument pdfDocument = main.getPdfDocument();
            PDPage page = pdfDocument.getPage(person.getPageIndex());
            PDRectangle bBox = page.getBBox();
            IsoPage isoPage = pdfDocument.findBestFittingPageSize(page.getBBox().getWidth(), page.getBBox().getHeight());
            assertThat(String.format("[%d] page too small", person.getId()), isoPage, greaterThanOrEqualTo(minPage));
        }
        assertEquals(0, main.getPageErrors().size(), "Unexpected number of errors");
    }

    protected void writeResult(PersonList personList, TestInfo testInfo) throws Exception {
        String testClassName = testInfo.getTestClass().get().getSimpleName();
        String testMethod = testInfo.getTestMethod().get().getName();
        String fileName = String.format("output/%s/%s.gson", testClassName, testMethod);
        Files.createDirectories(Paths.get(String.format("output/%s", testClassName)));
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            ExpectedResults expectedResults = new ExpectedResults(personList);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(expectedResults, fileWriter);
        }
    }
}
