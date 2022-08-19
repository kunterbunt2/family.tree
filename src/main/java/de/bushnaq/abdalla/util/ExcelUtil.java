package de.bushnaq.abdalla.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {
	static final Logger			logger						= LoggerFactory.getLogger(ExcelUtil.class);
	public static final long	STATUS_OBJECT_NAME_INVALID	= 0xc0000033L;

	public static void addComment(Cell cell, String message) {
		if (cell != null) {
			Comment cellComment = cell.getCellComment();
			if (cellComment == null) {
				CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
				style.cloneStyleFrom(cell.getCellStyle());
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				cell.setCellStyle(style);
				Drawing			drawing	= cell.getSheet().createDrawingPatriarch();
				CreationHelper	factory	= cell.getSheet().getWorkbook().getCreationHelper();
				// When the comment box is visible, have it show in a 1x3 space
				ClientAnchor	anchor	= factory.createClientAnchor();
				anchor.setCol1(cell.getColumnIndex());
				anchor.setCol2(cell.getColumnIndex() + 3);
				anchor.setRow1(cell.getRowIndex());
				anchor.setRow2(cell.getRowIndex() + 3);
				anchor.setDx1(100);
				anchor.setDx2(1000);
				anchor.setDy1(100);
				anchor.setDy2(1000);
				Comment			comment	= drawing.createCellComment(anchor);
				RichTextString	str		= factory.createRichTextString(message);
				comment.setString(str);
				comment.setAuthor("XLSX2MPP");
				cell.setCellComment(comment);
			} else {
				CreationHelper	factory	= cell.getSheet().getWorkbook().getCreationHelper();
				RichTextString	string	= cellComment.getString();
				RichTextString	str		= factory.createRichTextString(string.getString() + "\n" + message);
				cellComment.setString(str);
			}
		}
	}

	public static String cellReference(Cell cell) {
		return columnIndexToExcelColumnName(cell.getColumnIndex()) + rowIndextoExcelRowName(cell.getRowIndex());
	}

	public static String columnIndexToExcelColumnName(int i) {
		char x = 'A';
		x += i;
		return String.valueOf(x);
	}

	public static int rowIndextoExcelRowName(int rowNum) {
		return rowNum + 1;
	}

}
