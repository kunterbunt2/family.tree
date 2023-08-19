package de.bushnaq.abdalla.family.tree.tool;

import de.bushnaq.abdalla.family.Application;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.util.Base;
import de.bushnaq.abdalla.family.tree.util.Obfuscator;
import de.bushnaq.abdalla.util.ColumnHeader;
import de.bushnaq.abdalla.util.ColumnHeaderList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Generate special test data out of existing data rahjmuw,
 * by removing subtrees
 */
@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class CreateTestData extends Base {

    private final ColumnHeaderList columnHeaderList = new ColumnHeaderList();
    private String testClassName;

    public CreateTestData() {
        super(new Obfuscator().obfuscateString("bushnaq"));
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
        return String.format("reference/%s/%s.xlsx", getFamilyName(), getFamilyName());
    }

    protected String buildOutputFileName(TestInfo testInfo) throws IOException {
        return String.format("reference/%s/%s.xlsx", testClassName, testClassName);
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

    @Test
    @DisplayName("generate date for ETest")
    public void generateETestData(TestInfo testInfo) throws Exception {
        testClassName = "ETest";
        String[] args = new String[]{//
                "-input", buildInputFileName(testInfo)//
                , "-output", buildOutputFileName(testInfo)//
                , "-family_name", getFamilyName()//
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


}
