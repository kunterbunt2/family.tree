package de.bushnaq.abdalla.family;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bushnaq.abdalla.util.BasicExcelReader;
import de.bushnaq.abdalla.util.ColumnHeaderList;
import de.bushnaq.abdalla.util.ExcelUtil;
import de.bushnaq.abdalla.util.ObfuscatingBase;

public class Obfuscate extends BasicExcelReader {
	private final Logger	logger			= LoggerFactory.getLogger(this.getClass());
	ObfuscatingBase			ObfuscatingBase	= new ObfuscatingBase();

	public static void main(String[] args) throws Exception {
		Obfuscate obfuscate = new Obfuscate();
		obfuscate.obfuscate("bushnaq.xlsx");
	}

	private void obfuscate(String fileName) throws Exception {
		readExcelFile(fileName);
	}

	protected void postPorcess(String fileName, Workbook workbook) throws Exception {
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		evaluator.evaluateAll();
		ObfuscatingBase.reseed();
		write(workbook, ObfuscatingBase.obfuscateString(removeExtension(fileName)) + ".xlsx");
	}

	void write(Workbook workbook, String outputFile) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(outputFile));
		workbook.write(out);
		out.close();
	}

	protected void readRow(Workbook workbook, Row row) throws Exception {
		obfuscateColumn(row, ColumnHeaderList.FIRST_NAME_COLUMN);
		obfuscateColumn(row, ColumnHeaderList.LAST_NAME_COLUMN);
		obfuscateColumn(row, ColumnHeaderList.COMMENT_COLUMN);
	}

	private void obfuscateColumn(Row row, String columnName) throws Exception {
		Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(columnName));
		if (cell != null) {
			String obfuscateString = obfuscateCell(cell);
			cell.setCellValue(obfuscateString);
		}
	}

	public static String removeExtension(String originalName) {
		int lastDot = originalName.lastIndexOf(".");
		if (lastDot != -1) {
			return originalName.substring(0, lastDot);
		} else {
			return originalName;
		}
	}

	String obfuscateCell(Cell cell) throws Exception {
		if (cell != null) {
			switch (cell.getCellType()) {
			case STRING:
				return ObfuscatingBase.obfuscateString(cell.getStringCellValue());
			default:
				throw new Exception(String.format("Expected String cell value at %s", ExcelUtil.cellReference(cell)));
			}
		}
		return null;
	}

}
