package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.util.BasicExcelReader;
import de.bushnaq.abdalla.util.ColumnHeaderList;
import de.bushnaq.abdalla.util.ExcelUtil;
import de.bushnaq.abdalla.util.FlexibleDate;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class TreeExcelReader extends BasicExcelReader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PersonList personList = new PersonList();
    private final Map<Integer, Person> rowIndexToPerson = new HashMap<>();
    private final Pattern pattern = Pattern.compile("\\d+");

    private void createPeronList(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (row.getRowNum() != 0 && row.getFirstCellNum() > -1) {
                Integer id = (int) row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.ID_COLUMN)).getNumericCellValue();
                String familyLetter = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.FAMILY_LETTER_COLUMN)).getStringCellValue();
                String sex = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.SEX_COLUMN)).getStringCellValue();
                if (sex.equalsIgnoreCase("Male")) {
                    rowIndexToPerson.put(row.getRowNum() + 1, Person.createMale(personList, id, familyLetter));
                } else if (sex.equalsIgnoreCase("Female")) {
                    rowIndexToPerson.put(row.getRowNum() + 1, Person.createFemale(personList, id, familyLetter));
                } else
                    throw new Exception(String.format("Unknown sex %s", sex));
            }
        }
    }

    private FlexibleDate getDate(Cell cell) throws Exception {
        if (cell != null) {
            if (Objects.requireNonNull(cell.getCellType()) == CellType.NUMERIC) {
                try {
                    double v = cell.getNumericCellValue();
                    if (v < 3000) {
                        // only year information
                        FlexibleDate date = new FlexibleDate((int) v);
                        return date;
                    } else {
                        FlexibleDate date = new FlexibleDate(cell.getDateCellValue());
                        return date;
                    }
                } catch (IllegalStateException e) {
                    throw new Exception(String.format("Expected Date format at %s", ExcelUtil.cellReference(cell)));
                }
            }
        }
        return null;
    }

    private Person getFemaleRowByReference(Workbook workbook, Row row) throws Exception {
        Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.MOTHER_COLUMN));
        Person p = getPersonRowByReference(workbook, cell);
        if (p == null || p.isFemale()) {
            return p;
        } else {
            throw new Exception(String.format("Expected reference to Female person at %s", ExcelUtil.cellReference(cell)));
        }

    }

    private String getFirstName(Workbook workbook, Cell cell) throws Exception {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case BLANK:
                    return null;
                default:
                    throw new Exception(String.format("Expected String cell value at %s but found %s.", ExcelUtil.cellReference(cell), cell.getCellType().name()));
            }
        }
        return null;
    }

    private String getLastName(Workbook workbook, Cell cell) throws Exception {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case BLANK:
                    return null;
                default:
                    throw new Exception(String.format("Expected String cell value at %s but found %s.", ExcelUtil.cellReference(cell), cell.getCellType().name()));
            }
        }
        return null;
    }

    private Person getMaleRowByReference(Workbook workbook, Row row) throws Exception {
        Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.FATHER_COLUMN));
        Person p = getPersonRowByReference(workbook, cell);
        if (p == null || p.isMale()) {
            return p;
        } else {
            throw new Exception(String.format("Expected reference to male person at %s", ExcelUtil.cellReference(cell)));
        }

    }

    Person getPersonRowByReference(Workbook workbook, Cell cell) throws Exception {
        Integer index = getReference(workbook, cell);
        if (index == null)
            return null;
        Person p = rowIndexToPerson.get(index);
//		if (p == null) {
//			p = getPersonRowByReference(workbook, cell);
//		}
        return p;
    }

    Integer getReference(Workbook workbook, Cell cell) throws Exception {
        if (cell != null) {
            if (cell.getCellType() == CellType.FORMULA) {
                if (!cell.getCellFormula().isEmpty()) {
                    final EvaluationWorkbook evalWorkbook = XSSFEvaluationWorkbook.create((XSSFWorkbook) workbook);
                    final EvaluationSheet evalSheet = evalWorkbook.getSheet(0);
                    final EvaluationCell evelCell = evalSheet.getCell(cell.getRowIndex(), cell.getColumnIndex());
                    final Ptg[] formulaTokens = evalWorkbook.getFormulaTokens(evelCell);
                    String cellFormula = cell.getCellFormula();
                    if (formulaTokens.length == 1 && formulaTokens[0] instanceof RefPtg) {
                        // this is a reference to a cell on the same sheet
                        // String cellFormula = cell.getCellFormula();
                        CellReference cellReference = new CellReference(cellFormula);
                        CellType cachedFormulaResultTypeEnum = cell.getCachedFormulaResultType();
                        switch (cachedFormulaResultTypeEnum) {
                            case STRING: {
                                return cellReference.getRow() + 1;
                            }
                            case NUMERIC: {
                                return cellReference.getRow() + 1;
                            }
                            default:
                                throw new Exception(String.format("Unexpected cell type %s", cachedFormulaResultTypeEnum.name()));
                        }
                    }
                }
            } else {
                throw new Exception(String.format("Expected a reference at cell %s", ExcelUtil.cellReference(cell)));
            }
        }
        return null;
    }

    public PersonList importPersonList(String fileName) throws Exception {
        readExcelFile(fileName);
        return personList;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

//	public PersonList readPersonList(String fileName) throws Exception {
//		readExcel(fileName);
//		return personList;
//	}

    @Override
    protected void readRow(Workbook workbook, Row row) throws Exception {
        Person person = rowIndexToPerson.get(row.getRowNum() + 1);
        {
            Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.FIRST_NAME_COLUMN));
            person.setFirstName(getFirstName(workbook, cell));
            if (person.getFirstName().isEmpty())
                throw new Exception(String.format("firstName cannot be empty at ", ExcelUtil.cellReference(cell)));
        }
        {
            Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.FIRST_NAME_COLUMN_ORIGINAL_LANGUAGE));
            person.setFirstNameOriginalLanguage(getFirstName(workbook, cell));
        }
        {
            Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.LAST_NAME_COLUMN));
            person.setLastName(getLastName(workbook, cell));
            if (person.getLastName() == null || person.getLastName().isEmpty())
                logger.warn(String.format("[%3d] '%s' has no family name.", person.getId(), person.getFirstName()));
        }
        {
            Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.LAST_NAME_COLUMN_ORIGINAL_LANGUAGE));
            person.setLastNameOriginalLanguage(getLastName(workbook, cell));
        }
        person.setBorn(getDate(row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.BORN_COLUMN))));
        person.setDied(getDate(row.getCell(getColumnHeaderList().getExcelColumnIndex(ColumnHeaderList.DIED_COLUMN))));
        person.setFather(getMaleRowByReference(workbook, row));
        person.setMother(getFemaleRowByReference(workbook, row));
    }

    @Override
    protected void readWokbook(Workbook workbook) throws Exception {
        detectExcelHeaderColumns(workbook);
        createPeronList(workbook);
        readRows(workbook);

        for (Person p : rowIndexToPerson.values()) {
            personList.add(p);
        }
    }

}
