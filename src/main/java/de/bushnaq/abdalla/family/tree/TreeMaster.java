package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TreeMaster {
	private static final int	ID_COLUMN			= 0;
	private static final int	MOTHER_COLUMN		= 7;
	private static final int	FATHER_COLUMN		= 6;
	private static final int	DIED_COLUMN			= 5;
	private static final int	BORN_COLUMN			= 4;
	private static final int	LAST_NAME_COLUMN	= 3;
	private static final int	FIRST_NAME_COLUMN	= 2;
	private static final int	SEX_COLUMN			= 1;
//	private static final int	COLUMN_NUMBER		= 8;
	PersonList					personList			= new PersonList();
	Font						nameFont			= new Font("Arial", Font.PLAIN, Person.PERSON_HEIGHT / 3);

	private void draw(String familyName) {
		String imageFilenName = familyName + "-family-tree.png";
		calculateWidths();
		position();
		int				imageWidth	= personList.getWidth();
		int				imageHeight	= personList.getHeight();
		BufferedImage	aImage		= new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= aImage.createGraphics();
		graphics.setFont(nameFont);
		personList.printPersonList();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, imageWidth, imageHeight);
		for (Person p : personList) {
			p.draw(graphics);
		}
		try {
			File outputfile = new File(imageFilenName);
			ImageIO.write(aImage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void calculateWidths() {
		BufferedImage	image		= new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= image.createGraphics();
		graphics.setFont(nameFont);
		personList.calculateWidths(graphics);
	}

	private Male findFirstFather() {
		Male firstFather = null;
		for (Person p : personList) {
			if (p.isMale()) {
				if (p.hasChildren()) {
					if (firstFather == null || p.born.before(firstFather.born)) {
						firstFather = (Male) p;
					}
				}
			}
		}
		return firstFather;
	}

	private void position() {
		Male firstFather = findFirstFather();
		firstFather.x = 0;
		firstFather.y = 0;
		boolean change = false;
		do {
			change = false;
			for (Person p : personList) {
				if (p.isMale())
					if (position((Male) p))
						change = true;
			}
		} while (change);
	}

	private boolean position(Male father) {
		boolean			change		= false;
		List<Female>	wifeList	= father.getWifeList();
		for (Female wife : wifeList) {
			if (wife.y != father.y) {
				wife.y = father.y;
				change = true;
			}
			if (wife.x != father.x + father.width / 2 + wife.width / 2 + Person.PERSON_X_SPACE) {
				wife.x = father.x + father.width / 2 + wife.width / 2 + Person.PERSON_X_SPACE;
				change = true;
			}
			int childX = father.x + father.width/2;
			for (Person p : personList) {
				if ((p.father != null && p.father.equals(father)) && (p.mother != null && p.mother.equals(wife))) {
					if (p.y != father.y + Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE) {
						p.y = father.y + Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE;
						change = true;
					}
					if (p.x != childX - p.width / 2) {
						childX -= p.width / 2;
						p.x = childX;
						childX += p.width + Person.PERSON_X_SPACE / 2;
						change = true;
					} else {
						childX -= p.width / 2;
						childX += p.width + Person.PERSON_X_SPACE / 2;
					}
				}
			}
		}
		return change;
	}

	public void generate(String familyName) throws Exception {
		testAlbumList();
		draw(familyName);
	}

	private void testAlbumList() throws Exception {
		{
			System.out.println("/*-----------------------------------------------------------------");
			System.out.println("* test if songs on HDD exist in the album list");
			System.out.println("-----------------------------------------------------------------*/");

			System.out.println("Success");
		}
	}

	public void readExcel(String fileName) throws Exception {
		File file = new File(fileName);
		if (file.exists()) {
			try (FileInputStream excelFilestream = new FileInputStream(file)) {
				try (Workbook workbook = new XSSFWorkbook(excelFilestream)) {
					read(workbook);
				}
			}
		}
	}

	Map<Integer, Person> rowIndexToPerson = new HashMap<>();

	private void read(Workbook workbook) throws Exception {
		Sheet			sheet		= workbook.getSheetAt(0);
		Iterator<Row>	iterator	= sheet.iterator();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			if (row.getRowNum() != 0) {
//				short lastCellNum = row.getLastCellNum();
//				if (lastCellNum != COLUMN_NUMBER)
//					throw new Exception(String.format("Wrong number of columns, expected %d, but is %d", COLUMN_NUMBER, lastCellNum));
				if (row.getCell(0) != null) {
					readRow(workbook, row);
				}
			}
		}

		for (Person p : rowIndexToPerson.values()) {
			personList.add(p);
		}

	}

	private void readRow(Workbook workbook, Row row) throws Exception {
		Integer	id			= (int) row.getCell(ID_COLUMN).getNumericCellValue();
		String	sex			= row.getCell(SEX_COLUMN).getStringCellValue();
		String	firstName	= row.getCell(FIRST_NAME_COLUMN).getStringCellValue();
		String	lastName	= row.getCell(LAST_NAME_COLUMN).getStringCellValue();
		Date	born		= row.getCell(BORN_COLUMN).getDateCellValue();
		Date	died		= row.getCell(DIED_COLUMN).getDateCellValue();
		Male	father		= (Male) getPersonRow(workbook, row.getCell(FATHER_COLUMN));
		Female	mother		= (Female) getPersonRow(workbook, row.getCell(MOTHER_COLUMN));
		if (sex.equalsIgnoreCase("Male")) {
			rowIndexToPerson.put(row.getRowNum(), new Male(personList, id, firstName, lastName, born, died, father, mother));
		} else if (sex.equalsIgnoreCase("female")) {
			rowIndexToPerson.put(row.getRowNum(), new Female(personList, id, firstName, lastName, born, died, father, mother));
		}
	}

	Person getPersonRow(Workbook workbook, Cell cell) throws Exception {
		Integer index = getReference(workbook, cell);
		if (index == null)
			return null;
		Person p = rowIndexToPerson.get(index);
		if (p == null) {
			p = getPersonRow(workbook, cell);
		}
		return p;
	}

	Integer getReference(Workbook workbook, Cell cell) throws Exception {
		if (cell != null && !cell.getStringCellValue().isEmpty()) {
			final EvaluationWorkbook	evalWorkbook	= XSSFEvaluationWorkbook.create((XSSFWorkbook) workbook);
			final EvaluationSheet		evalSheet		= evalWorkbook.getSheet(0);
			final EvaluationCell		evelCell		= evalSheet.getCell(cell.getRowIndex(), cell.getColumnIndex());
			final Ptg[]					formulaTokens	= evalWorkbook.getFormulaTokens(evelCell);
			String						cellFormula		= cell.getCellFormula();
			if (formulaTokens.length == 1 && formulaTokens[0] instanceof RefPtg) {
				// this is a reference to a cell on the same sheet
				// String cellFormula = cell.getCellFormula();
				CellReference	cellReference				= new CellReference(cellFormula);
				CellType		cachedFormulaResultTypeEnum	= cell.getCachedFormulaResultType();
				switch (cachedFormulaResultTypeEnum) {
				case STRING: {
					return cellReference.getRow();
				}
				default:
					throw new Exception(String.format("Unexpected cell type %s", cachedFormulaResultTypeEnum.name()));
				}
			}
		}
		return null;
	}

}
