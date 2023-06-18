package de.bushnaq.abdalla.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public abstract class BasicExcelReader {
    private final ColumnHeaderList columnHeaderList = new ColumnHeaderList();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void detectExcelHeaderColumns(Workbook workbook) {
        ExcelErrorHandler grh = new ExcelErrorHandler();
        {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            Iterator<Cell> cellIterator = row.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                getColumnHeaderList().register(grh, row, currentCell.getColumnIndex(), currentCell.getStringCellValue());
            }
            getColumnHeaderList().testForMissingColumns(grh, row);
        }
    }

    protected ColumnHeaderList getColumnHeaderList() {
        return columnHeaderList;
    }

    protected void postPorcess(String fileName, Workbook workbook) throws Exception {

    }

    public void readExcelFile(String fileName) throws Exception {
        File file = new File(fileName);
        if (file.exists()) {
            try (FileInputStream excelFilestream = new FileInputStream(file)) {
                try (Workbook workbook = new XSSFWorkbook(excelFilestream)) {
                    readWokbook(workbook);
                    postPorcess(fileName, workbook);
                }
            }
        } else {
            throw new Exception(String.format("File '%s' not found!", fileName));
        }
    }

    protected abstract void readRow(Workbook workbook, Row row) throws Exception;

    protected void readRows(Workbook workbook) throws Exception {
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (row.getRowNum() != 0) {
                if (row.getCell(0) != null) {
                    readRow(workbook, row);
                }
            }
        }
    }

    protected void readWokbook(Workbook workbook) throws Exception {
        detectExcelHeaderColumns(workbook);
        readRows(workbook);
    }

}
