package de.bushnaq.abdalla.family.tree.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.Main;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.ui.MyCanvas;
import de.bushnaq.abdalla.pdf.IsoPage;
import de.bushnaq.abdalla.pdf.PdfDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    private final String familyName;
    @Autowired
    protected Context context;
    @Autowired
    protected Main main;

    public Base(String familyName) {
        this.familyName = familyName;
    }

    protected String buildFileName() {
        return "reference/" + getFamilyName() + "/" + getFamilyName() + ".xlsx";
    }

    public PersonList generate(String[] args) throws Exception {
//		BufferedImage	image			=
        PersonList personList = main.start(args);
//        String inputName = context.getParameterOptions().getInput();
//        String outputDecorator = context.getParameterOptions().getOutputDecorator();
//        String outputName = inputName + outputDecorator;
//		if (TestUtil.isRunningInEclipse())
//			showImage(image, outputName);
        return personList;
    }

    public String getFamilyName() {
        return familyName;
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

    private void showImage(BufferedImage image, String title) {
        MyCanvas c = new MyCanvas(image);
        c.f.setTitle(title);
        while (c.f.isVisible())
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
