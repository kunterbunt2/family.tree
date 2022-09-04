package de.bushnaq.abdalla.family.tree.tools;

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
import de.bushnaq.abdalla.util.DirectoryUtil;
import de.bushnaq.abdalla.util.ExcelUtil;
import de.bushnaq.abdalla.util.ObfuscatingBase;

public class Obfuscate extends BasicExcelReader {
	public static void main(String[] args) throws Exception {
		Obfuscate obfuscate = new Obfuscate();
		obfuscate.obfuscate("bushnaq/bushnaq.xlsx");
	}

	private final Logger	logger			= LoggerFactory.getLogger(this.getClass());
	ObfuscatingBase			ObfuscatingBase	= new ObfuscatingBase();

	private void deleteColumn(Row row, String columnName) throws Exception {
		Cell cell = row.getCell(getColumnHeaderList().getExcelColumnIndex(columnName));
		if (cell != null) {
			row.removeCell(cell);
		}
	}

	private String extractFileNamePart(String originalName) {
		int slash = originalName.lastIndexOf('/');
		if (slash != -1) {
			return originalName.substring(slash + 1, originalName.length());
		} else {
			return originalName;
		}
	}

	private void obfuscate(String fileName) throws Exception {
		readExcelFile(fileName);
		logger.info("Success");
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
		String familyName = ObfuscatingBase.obfuscateString(removeExtension(extractFileNamePart(fileName)));
		DirectoryUtil.createDirectory(familyName);
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

	public String removeExtension(String originalName) {
		int lastDot = originalName.lastIndexOf(".");
		if (lastDot != -1) {
			return originalName.substring(0, lastDot);
		} else {
			return originalName;
		}
	}

	void write(Workbook workbook, String outputFile) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(outputFile));
		workbook.write(out);
		out.close();
	}

}
