package de.bushnaq.abdalla.family.tree.tools;

import de.bushnaq.abdalla.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Obfuscate extends BasicExcelReader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    final ObfuscatingBase ObfuscatingBase = new ObfuscatingBase();

    public static void main(String[] args) throws Exception {
        Obfuscate obfuscate = new Obfuscate();
        obfuscate.obfuscate("bushnaq/bushnaq.xlsx");
    }

    private void deleteColumn(Row row, String columnName) {
        Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(columnName));
        if (cell != null) {
            row.removeCell(cell);
        }
    }

    private void obfuscate(String fileName) throws Exception {
        readExcelFile(fileName);
        logger.info("Success");
    }

    String obfuscateCell(Cell cell) throws Exception {
        if (cell != null) {
            return switch (cell.getCellType()) {
                case STRING -> ObfuscatingBase.obfuscateString(cell.getStringCellValue());
                case BLANK -> null;
                default ->
                        throw new Exception(String.format("Expected String cell value at %s instead found %s", ExcelUtil.cellReference(cell), cell.getCellType().name()));
            };
        }
        return null;
    }

    private void obfuscateColumn(Row row, String columnName) throws Exception {
        Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(columnName));
        if (cell != null) {
            String obfuscateString = obfuscateCell(cell);
            cell.setCellValue(obfuscateString);
        }
    }

    @Override
    protected void postPorcess(String fileName, Workbook workbook) throws Exception {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
        ObfuscatingBase.reseed();
        String familyName = ObfuscatingBase.obfuscateString(FileUtil.removeExtension(FileUtil.extractFileNamePart(fileName)));
        write(workbook, "examples/" + familyName + ".xlsx");
    }

    @Override
    protected void readRow(Workbook workbook, Row row) throws Exception {
        obfuscateColumn(row, ColumnHeaderList.FIRST_NAME_COLUMN);
        obfuscateColumn(row, ColumnHeaderList.LAST_NAME_COLUMN);
        obfuscateColumn(row, ColumnHeaderList.COMMENT_COLUMN);
        deleteColumn(row, ColumnHeaderList.FIRST_NAME_COLUMN_ORIGINAL_LANGUAGE);
        deleteColumn(row, ColumnHeaderList.LAST_NAME_COLUMN_ORIGINAL_LANGUAGE);
        deleteColumn(row, ColumnHeaderList.COMMENT_COLUMN_ORIGINAL_LANGUAGE);
    }

    void write(Workbook workbook, String outputFile) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(outputFile));
        workbook.write(out);
        out.close();
    }

}
