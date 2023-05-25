package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
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
	private static final Color	GRID_COLOR	= new Color(0x2d, 0xb1, 0xff, 32);
	protected Context			context;
	public List<PageError>		errors		= new ArrayList<>();
	private int					firstPageIndex;
	int							iteration	= 0;
	private int					lastPageIndex;
	Font						livedFont;												// used for date text
	final Logger				logger		= LoggerFactory.getLogger(this.getClass());
	Font						nameFont;												// used for name text
	PdfFont						pdfDateFont;											// used for date text
	PdfFont						pdfNameFont;											// used for name text
	PdfFont						pdfNameOLFont;
	PersonList					personList;

	public Tree(Context context, PersonList personList) {
		this.context = context;
		this.personList = personList;
		this.personList.reset();// in case tree was called once before
		errors.clear();
	}

	float calclatePageWidth(int pageIndex) {
		float	minX	= Integer.MAX_VALUE;
		float	maxX	= Integer.MIN_VALUE;
		for (Person p : personList) {
			if (p.isVisible() && pageIndex == p.getPageIndex()) {
				float x = p.x * (Person.getWidth(context) + Person.getXSpace(context));
				minX = Math.min(minX, (x));
				maxX = Math.max(maxX, x + Person.getWidth(context));
			}
		}
		return maxX;
	}

	float calculatePageHeight(Context context, int pageIndex) {
		float	minY	= Integer.MAX_VALUE;
		float	maxY	= Integer.MIN_VALUE;
		for (Person p : personList) {
			if (p.isVisible() && pageIndex == p.getPageIndex()) {
				float y = p.y * (Person.getHeight(context) + Person.getYSpace(context));
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
			}
		}
		return maxY + Person.getHeight(context);
	}

	private PdfFont createFont(PdfDocument pdfDocument, String fontName, float fontSize) throws IOException {
		PDFont pdFont = pdfDocument.loadFont(fontName);
		return new PdfFont(pdFont, fontSize / getFontSize(pdFont));
	}

	private void createPages(Context context, PdfDocument pdfDocument) throws IOException {
		List<Male> rootFatherList = findRootFatherList();
		for (Person rootFather : rootFatherList) {
			float	pageWidth	= calclatePageWidth(pdfDocument.lastPageIndex);
			float	pageHeight	= calculatePageHeight(context, pdfDocument.lastPageIndex);
			pdfDocument.createPage(pdfDocument.lastPageIndex++, pageWidth, pageHeight, rootFather.getFirstName() + context.getParameterOptions().getOutputDecorator());
		}
	}

	private void draw(Context context, PdfDocument pdfDocument) throws IOException {
		createPages(context, pdfDocument);
//		drawGrid(pdfDocument);
		draw(context, pdfDocument, firstPageIndex, lastPageIndex);
	}

	abstract void draw(Context context, PdfDocument pdfDocument, int firstPage, int lastPage) throws IOException;

	private void drawGrid(PdfDocument pdfDocument) throws IOException {
		for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				PDPage page = pdfDocument.getPage(pageIndex);
				p.setLineWidth(0.5f);
				p.setStrokingColor(GRID_COLOR);
				p.setNonStrokingColor(GRID_COLOR);
				p.setFont(pdfNameFont);
				float	h	= Person.getHeight(context) + Person.getYSpace(context);
				float	w	= Person.getWidth(context) + Person.getXSpace(context);

				for (int y = 0; y < page.getBBox().getHeight() / h + 1; y++) {
					p.drawRect(0, -Person.getYSpace(context) / 2 + y * h, page.getBBox().getWidth(), 1);
					for (int x = 0; x < page.getBBox().getWidth() / w + 1; x++) {
						p.drawRect(-Person.getXSpace(context) / 2 + x * w, 0, 1, page.getBBox().getHeight());
						p.beginText();
						p.newLineAtOffset(x * w, y * h + Person.getHeight(context));
						p.showText(String.format("%d,%d", x, y));
						p.endText();
					}
				}
				p.stroke();
			}
		}
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

	public void generate(Context context, PdfDocument pdfDocument, String familyName) throws Exception {
		logger.info(String.format("Generating %s tree ...", context.getParameterOptions().getOutputDecorator()));
		init(context, pdfDocument);
		draw(context, pdfDocument);
	}

	public void generateErrorPage(PdfDocument pdfDocument) throws IOException {
		logger.info(String.format("Generating Error page ..."));
		init(context, pdfDocument);
		pdfDocument.createPage(PDRectangle.A4, "Errors");
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pdfDocument.lastPageIndex)) {
			PDPage page = pdfDocument.getPage(pdfDocument.lastPageIndex);
			p.setNonStrokingColor(Color.red);
			p.setFont(pdfNameFont);

			float y = p.getStringHeight();
			for (PageError s : errors) {
				p.beginText();
				p.newLineAtOffset(2, y);
				p.showText(s.getError());
				p.endText();
				y += p.getStringHeight();
			}
		}
	}

	private float getFontSize(PDFont font) {
		return (-font.getFontDescriptor().getDescent() + font.getFontDescriptor().getCapHeight() + (font.getFontDescriptor().getAscent() - font.getFontDescriptor().getCapHeight())) / 1000;
	}

	private void init(Context context, PdfDocument pdfDocument) throws IOException {
		pdfDateFont = createFont(pdfDocument, "NotoSans-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 5);
//		pdfDateFont = new PdfFont(pdfDocument.loadFont("NotoSans-Regular.ttf"), (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 5);
		if (context.getParameterOptions().isCompact()) {
			pdfNameFont = createFont(pdfDocument, "NotoSans-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 2);
			pdfNameOLFont = createFont(pdfDocument, "Amiri-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 2);
		} else {
			pdfNameFont = createFont(pdfDocument, "NotoSans-Bold.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 3);
			pdfNameOLFont = createFont(pdfDocument, "Amiri-bold.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 3);
		}
		initializePersonList(context, pdfDocument);
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

	private void initializePersonList(Context context, PdfDocument pdfDocument) {
		initAttributes();
		position(context, pdfDocument);
		for (Person p : personList) {
			p.validate(context);
		}
		for (Person p : personList) {
			if (!p.isVisible())
				errors.add(new PageError(firstPageIndex, String.format("Error #001: [%d]%s %s is not visible. A person is visible if he is member of the family or has children with someone from the family.", p.getId(),
						p.getFirstName(), p.getLastName())));
		}
	}

	private void position(Context context, PdfDocument pdfDocument) {
		List<Male> firstFathers = findRootFatherList();
		firstPageIndex = pdfDocument.lastPageIndex;
		lastPageIndex = pdfDocument.lastPageIndex;
		for (Person firstFather : firstFathers) {
			firstFather.setPageIndex(lastPageIndex++);
			firstFather.x = 0;
			firstFather.y = 0;
			position(context, firstFather);
		}
		lastPageIndex--;
	}

	abstract float position(Context context, Person person);

}
