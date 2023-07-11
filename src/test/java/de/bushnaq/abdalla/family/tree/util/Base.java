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
import de.bushnaq.abdalla.util.ColumnHeader;
import de.bushnaq.abdalla.util.ColumnHeaderList;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
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
    private final ColumnHeaderList columnHeaderList = new ColumnHeaderList();
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

    private static void removeDeadReferences(PersonList personList) {
        for (Person person : personList) {
            if (person.getFather() != null) {
                if (personList.findPersonById(person.getFather().getId()) == null) {
                    person.setFather(null);
                }
            }
            if (person.getMother() != null) {
                if (personList.findPersonById(person.getMother().getId()) == null) {
                    person.setMother(null);
                }
            }
            person.resetSpouseList();
            person.resetChildrenList();
        }
    }

    protected String buildInputFileName(TestInfo testInfo) {
        String testClassName = testInfo.getTestClass().get().getSimpleName();
//        String testMethod = testInfo.getTestMethod().get().getName();
        return String.format("reference/%s/%s.xlsx", testClassName, testClassName);
    }

    protected String buildOutputFileName(TestInfo testInfo) throws IOException {
        String testClassName = testInfo.getTestClass().get().getSimpleName();
        String testMethod = testInfo.getTestMethod().get().getName();
        Files.createDirectories(Paths.get(String.format("output/%s", testClassName)));
        return String.format("output/%s/%s-%s.pdf", testClassName, testMethod, getFootertext());
    }

    void filterSubtree(PersonList personList, int filter) throws IOException {
        personList.clearVisited();
        Person root = personList.findPersonById(filter);
        root.setGeneration(1);
        root.analyzeTree(context);
        PersonList subTreeList = root.getSubTree();
        subTreeList.add(root);
        for (Person person : subTreeList) {
            person.setVisited(true);
        }

        int i = 0;
        do {
            Person current = personList.get(i);
            if (!current.isVisited()) {
                personList.remove(current);
            } else {
                i++;
            }
        }
        while (i < personList.size());
        removeDeadReferences(personList);
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

    void removeSubTree(PersonList personList, int filter) throws IOException {
        personList.clearVisited();
        Person root = personList.findPersonById(filter);
        root.setGeneration(1);
        root.analyzeTree(context);
        PersonList subTreeList = root.getSubTree();
        subTreeList.add(root);
        for (Person person : subTreeList) {
            person.setVisited(true);
        }

        int i = 0;
        do {
            Person current = personList.get(i);
            if (current.isVisited()) {
                personList.remove(current);
            } else {
                i++;
            }
        }
        while (i < personList.size());
        removeDeadReferences(personList);
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

    void writeExcel(String fileName, PersonList personList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            int rowIndex = 0;
            Sheet sheet = workbook.createSheet("tree");
            Row header = sheet.createRow(rowIndex++);
            for (int i = 0; i < columnHeaderList.size(); i++) {
                ColumnHeader columnHeader = columnHeaderList.get(i);
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(columnHeader.name);
            }


            for (Person person : personList) {
                personToIndexMap.put(person, rowIndex);
                Row row = sheet.createRow(rowIndex++);
                int columnIndex = 0;
                {
                    Cell cell = row.createCell(columnIndex++);
                    cell.setCellValue(person.getId());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
                    cell.setCellValue(person.getSex());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
                    cell.setCellValue(person.getFirstName());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
//                    cell.setCellValue(person.getFirstNameOriginalLanguage());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
                    cell.setCellValue(person.getFamilyLetter());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
                    cell.setCellValue(person.getLastName());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
//                    cell.setCellValue(person.getLastNameOriginalLanguage());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
//                    if (person.getBorn() != null)
//                        cell.setCellValue(person.getBorn().getDate());
                }
                {
                    Cell cell = row.createCell(columnIndex++);
//                    if (person.getDied() != null)
//                        cell.setCellValue(person.getDied().getDate());
                }
                if (person.getFather() != null) {
                    Cell cell = row.createCell(columnIndex);
//                    cell.setCellValue(person.getFather());
                }
                columnIndex++;
                if (person.getMother() != null) {
                    Cell cell = row.createCell(columnIndex);
//                    cell.setCellValue(person.getMother());
                }
                columnIndex++;
                {
                    Cell cell = row.createCell(columnIndex++);
                }
                {
                    Cell cell = row.createCell(columnIndex++);
                }
            }
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
//            formulaEvaluator.evaluateAll();
            workbook.setForceFormulaRecalculation(true);
            for (Person person : personList) {
                int r = personToIndexMap.get(person);
                Row row = sheet.getRow(r);
                if (person.getFather() != null) {
                    Cell cell = row.getCell(9);
                    Integer index = personToIndexMap.get(person.getFather());
                    cell.setCellFormula(String.format("$C$%d", index + 1));
                    formulaEvaluator.evaluateFormulaCell(cell);
                }
                if (person.getMother() != null) {
                    Cell cell = row.getCell(10);
                    Integer index = personToIndexMap.get(person.getMother());
                    cell.setCellFormula(String.format("$C$%d", index + 1));
                    formulaEvaluator.evaluateFormulaCell(cell);
                }
            }
            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
        }
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
