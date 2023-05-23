package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Female;
import de.bushnaq.abdalla.family.person.FemaleClone;
import de.bushnaq.abdalla.family.person.Male;
import de.bushnaq.abdalla.family.person.MaleClone;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

public abstract class Tree {
	protected Context		context;
	public List<PageError>	errors		= new ArrayList<>();
	int						iteration	= 0;
	Font					livedFont;												// used for date text
	final Logger			logger		= LoggerFactory.getLogger(this.getClass());
	Font					nameFont;												// used for name text
	private int				numberOfRootFathers;
	PdfFont					pdfDateFont;											// used for date text
	PdfFont					pdfNameFont;											// used for name text
	PdfFont					pdfNameOLFont;
	PersonList				personList;

	public Tree(Context context) {
		this.context = context;
	}

	abstract float calclateImageWidth();

	private void calculateGenrationMaxWidth() {
		for (Person p : personList) {
			Float integerMaxWidth = context.generationToMaxWidthMap.get(p.generation);
			if (integerMaxWidth == null) {
				context.generationToMaxWidthMap.put(p.generation, Person.getWidth(context));
			} else {
				if (Person.getWidth(context) > integerMaxWidth)
					context.generationToMaxWidthMap.put(p.generation, Person.getWidth(context));
			}
		}
	}

	abstract float calculateImageHeight(Context context);

	protected void calculateWidths() {
		BufferedImage	image		= new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= image.createGraphics();
		graphics.setFont(nameFont);
		personList.calculateWidths(graphics, nameFont, livedFont);
	}

//	abstract void draw(Context context, Graphics2D graphics);

	abstract void draw(Context context, PdfDocument pdfDocument, int numberOfRootFathers) throws IOException;

	private void drawErrors(Graphics2D graphics, int imageWidth, int imageHeight) {
		graphics.setFont(nameFont);
		graphics.setColor(Color.red);
		int y = imageHeight - 2;
		for (PageError s : errors) {
			Font				font			= graphics.getFont();
			FontRenderContext	frc				= graphics.getFontRenderContext();
			Rectangle2D			stringBounds	= font.getStringBounds(s.getError(), frc);
			graphics.drawString(s.getError(), 2, y);
			y -= stringBounds.getHeight();
		}
	}

	private void drawErrors(PdfDocument pdfDocument, float imageWidth, float imageHeight) throws IOException {
		for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(Color.red);
				p.setFont(pdfNameFont);

				float y = imageHeight - 2;
				for (PageError s : errors) {
					p.beginText();
					p.newLineAtOffset(2, y);
					p.showText(s.getError());
					p.endText();
					y -= p.getStringHeight(s.getError());
				}
			}
		}
	}

	private void drawGrid(Graphics2D graphics, int imageWidth, int imageHeight) {
//		graphics.setFont(nameFont);
//		graphics.setColor(Color.lightGray);
//		int	h	= Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE;
//		int	w	= Person.PERSON_WIDTH + Person.PERSON_X_SPACE;
//		for (int y = 0; y < imageHeight / h + 1; y++) {
//			graphics.fillRect(0, -Person.PERSON_Y_SPACE / 2 + y * h, imageWidth, 1);
//			for (int x = 0; x < imageWidth / w + 1; x++) {
//				graphics.fillRect(-Person.PERSON_X_SPACE / 2 + x * w, 0, 1, imageHeight);
//				graphics.drawString(String.format("%d,%d", x, y), x * w + 2, -Person.PERSON_Y_SPACE / 2 + y * h + Person.PERSON_HEIGHT - Person.PERSON_Y_SPACE - 2);
//			}
//		}
	}

	protected List<Male> findRootFatherList() {
		List<Male> fathers = new ArrayList<>();
		for (Person p : personList) {
			if (p.isRootFather(context)) {
				fathers.add((Male) p);
			}
		}
		return fathers;
	}

	public void generate(Context context, String familyName) throws Exception {
		initializePersonList(context);
		String		outputFilenName	= familyName + ".pdf";
		float		pageWidth		= calclateImageWidth();
		float		pageHeight		= calculateImageHeight(context);
		PdfDocument	pdfDocument		= new PdfDocument(outputFilenName, pageWidth, pageHeight);

		float		zoom			= context.getParameterOptions().getZoom();
		pdfDateFont = new PdfFont(pdfDocument.loadFont("NotoSans-Regular.ttf"), (Person.PERSON_HEIGHT * zoom - Person.PERSON_BORDER * zoom + 2 - Person.PERSON_MARGINE * zoom * 2) / 5);
		if (context.getParameterOptions().isCompact()) {
			pdfNameFont = new PdfFont(pdfDocument.loadFont("NotoSans-Regular.ttf"), (Person.PERSON_HEIGHT * zoom - Person.PERSON_BORDER * zoom + 2 - Person.PERSON_MARGINE * zoom * 2) / 4);
			pdfNameOLFont = new PdfFont(pdfDocument.loadFont("Amiri-Regular.ttf"), (Person.PERSON_HEIGHT * zoom - Person.PERSON_BORDER * zoom + 2 - Person.PERSON_MARGINE * zoom * 2) / 4);
		} else {
			pdfNameFont = new PdfFont(pdfDocument.loadFont("NotoSans-Bold.ttf"), (Person.PERSON_HEIGHT * zoom - Person.PERSON_BORDER * zoom + 2 - Person.PERSON_MARGINE * zoom * 2) / 4);
			pdfNameOLFont = new PdfFont(pdfDocument.loadFont("Amiri-bold.ttf"), (Person.PERSON_HEIGHT * zoom - Person.PERSON_BORDER * zoom + 2 - Person.PERSON_MARGINE * zoom * 2) / 4);
		}

		drawErrors(pdfDocument, pageWidth, pageHeight);
		draw(context, pdfDocument, numberOfRootFathers);
		pdfDocument.endDocument();
	}

	private void initAttribute(Person person) {
		PersonList	spouseList	= person.getSpouseList();
		int			spouseIndex	= 0;
		for (Person spouse : spouseList) {
			if (spouse.isMember(context)) {
				// both parents are member of the family
				// ignore any clone that we already have converted
				if (!context.getParameterOptions().isFollowFemales() && person.isMale() && !(spouse instanceof FemaleClone)) {
					// create a clone of the spouse and shift all child relations to that clone
					FemaleClone	clone			= new FemaleClone(spouse.personList, (Female) spouse);
					PersonList	childrenList	= person.getChildrenList(spouse);
					for (Person child : childrenList) {
						child.setMother(clone);
					}
					personList.add(clone);
					spouse = clone;
					spouse.spouseIndex = spouseIndex++;
					spouse.setSpouse(true);
				} else if (context.getParameterOptions().isFollowFemales() && person.isFemale() && !(spouse instanceof MaleClone)) {
					// create a clone of the spouse and shift all child relations to that clone
					MaleClone	clone			= new MaleClone(spouse.personList, (Male) spouse);
					PersonList	childrenList	= person.getChildrenList(spouse);
					for (Person child : childrenList) {
						child.setFather(clone);
					}
					personList.add(clone);
					spouse = clone;
					spouse.spouseIndex = spouseIndex++;
					spouse.setSpouse(true);
				}
			} else {
				spouse.spouseIndex = spouseIndex++;
				spouse.setSpouse(true);
			}
			if (person.isLastChild()) {
				spouse.setSpouseOfLastChild(true);
			}
			// children
			int			childIndex		= 0;
			boolean		firstChild		= true;
			PersonList	childrenList	= person.getChildrenList(spouse);
			for (Person child : childrenList) {
				child.generation = person.generation + 1;
				child.childIndex = childIndex++;
				child.setIsChild(true);
				if (firstChild) {
					child.setFirstChild(true);
					firstChild = false;
				}
				if (child.equals(childrenList.last())) {
					child.setLastChild(true);
				}
				initAttribute(child);
			}
		}
	}

	private void initAttributes() {
		List<Male> firstFathers = findRootFatherList();
		for (Person firstFather : firstFathers) {
			firstFather.setFirstFather(true);
			firstFather.generation = 0;
			initAttribute(firstFather);
		}
	}

	private void initializePersonList(Context context) {
		initAttributes();
		calculateWidths();
		calculateGenrationMaxWidth();
		position(context);
		for (Person p : personList) {
			p.validate(context);
		}
		for (Person p : personList) {
			if (!p.isVisible())
				errors.add(new PageError(p.getPageIndex(), String.format("Error #001: [%d]%s %s is not visible. A person is visible if he is member of the family or has children with someone from the family.", p.getId(),
						p.getFirstName(), p.getLastName())));
		}
	}

	private void position(Context context) {
		List<Male> firstFathers = findRootFatherList();
		numberOfRootFathers = 0;
		for (Person firstFather : firstFathers) {
			firstFather.setPageIndex(numberOfRootFathers++);
			if (context.getParameterOptions().isV()) {
				firstFather.x = 0/* rootFatherIndex++ * 10 */;
				firstFather.y = 0;
			} else {
				firstFather.x = 0;
				firstFather.y = 0/* rootFatherIndex++ * 10 */;
			}
			position(context, firstFather);
		}
	}

	abstract float position(Context context, Person person);

	public void readExcel(String fileName) throws Exception {
		TreeExcelReader excelReader = new TreeExcelReader();
		personList = excelReader.readExcel(fileName);
	}

}
