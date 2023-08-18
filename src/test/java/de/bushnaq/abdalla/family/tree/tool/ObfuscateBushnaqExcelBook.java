package de.bushnaq.abdalla.family.tree.tool;

import de.bushnaq.abdalla.family.tree.util.Obfuscator;
import de.bushnaq.abdalla.util.BasicExcelReader;
import de.bushnaq.abdalla.util.ColumnHeaderList;
import de.bushnaq.abdalla.util.ExcelUtil;
import de.bushnaq.abdalla.util.FileUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * obfuscates the bushnaq excel book so that it can be used in public tests.
 */
public class ObfuscateBushnaqExcelBook extends BasicExcelReader {
    final Obfuscator obfuscator = new Obfuscator();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) throws Exception {
        ObfuscateBushnaqExcelBook obfuscate = new ObfuscateBushnaqExcelBook();
        obfuscate.obfuscate("reference/bushnaq/bushnaq.xlsx");
    }

    private void deleteColumn(Row row, String columnName) {
        Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(columnName));
        if (cell != null) {
            row.removeCell(cell);
        }
    }

    private void obfuscate(String fileName) throws Exception {
        String a = obfuscator.obfuscateString("bushnaq");
        String b = obfuscator.obfuscateString("Bushnaq");
        readExcelFile(fileName);
        logger.info("Success");
    }

    String obfuscateCell(Cell cell) throws Exception {
        if (cell != null) {
            return switch (cell.getCellType()) {
                case STRING -> obfuscator.obfuscateString(cell.getStringCellValue());
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
        //obfuscator.reseed();
        String familyName = obfuscator.obfuscateString(FileUtil.removeExtension(FileUtil.extractFileNamePart(fileName)));
        write(workbook, "reference/" + familyName + "/" + familyName + ".xlsx");
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
