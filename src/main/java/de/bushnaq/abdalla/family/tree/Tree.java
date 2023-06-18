package de.bushnaq.abdalla.family.tree;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.*;
import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.IsoPage;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.util.ErrorMessages;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.bushnaq.abdalla.family.person.DrawablePerson.*;

public abstract class Tree {

    private static final Color GRID_COLOR = new Color(0x2d, 0xb1, 0xff, 32);
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    public List<PageError> errors = new ArrayList<>();
    protected Context context;
    PersonList personList;
    private int firstPageIndex;

    public Tree(Context context, PersonList personList) {
        this.context = context;
        this.personList = personList;
        this.personList.reset();// in case tree was called once before
        errors.clear();
    }

    private void analyzeTree() {
        List<Male> firstFathers = findRootFatherList();
        for (Person firstFather : firstFathers) {
            firstFather.setFirstFather(true);
            firstFather.setGeneration(0);
            firstFather.analyzeTree(context);
        }
    }

    float calclatePageWidth(int pageIndex) {
        float minX = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        for (Person p : personList) {
            if (p.isVisible() && pageIndex == p.getPageIndex()) {
                float x = p.x;
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
        }
        return (maxX - minX + 1) * DrawablePerson.getPersonWidth(context) + DrawablePerson.getPageMargin(context) * 2;
    }

    float calculatePageHeight(Context context, int pageIndex) {
        float minY = Integer.MAX_VALUE;
        float maxY = Integer.MIN_VALUE;
        for (Person p : personList) {
            if (p.isVisible() && pageIndex == p.getPageIndex()) {
                float y = p.y;
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }
        return (maxY - minY + 1) * DrawablePerson.getPersonHeight(context) + DrawablePerson.getPageMargin(context) * 2;
    }

    protected abstract void compact(Context context2, PdfDocument pdfDocument);

    protected abstract void compact(Context context2, PdfDocument pdfDocument, Person rootFather, int clipGeneration);

    private void createPages(Context context, PdfDocument pdfDocument) throws IOException {
        position(context, pdfDocument);
        compact(context, pdfDocument);
        //validate(context);
        List<Male> rootFatherList = findRootFatherList();
        for (Person rootFather : rootFatherList) {
            float pageWidth = calclatePageWidth(rootFather.getPageIndex());
            float pageHeight = calculatePageHeight(context, rootFather.getPageIndex());
//			Rect	treeRect	= rootFather.getTreeRect();
            pdfDocument.createPage(rootFather.getPageIndex(), pageWidth, pageHeight, rootFather.getFirstName() + context.getParameterOptions().getOutputDecorator());
            drawPageSizeWatermark(pdfDocument, rootFather.getPageIndex());
            drawPageAreaWatermark(pdfDocument, rootFather.getPageIndex(), rootFather.getTreeRect());
            //pdfDocument.lastPageIndex++;
        }
    }

    private void cutTree(Context context, PdfDocument pdfDocument, Person person, int treeMaxGeneration) {
        logger.info(String.format("cutting tree for parent=%d genration <= G%d", person.getId(), treeMaxGeneration));
        cutTree(context, person, treeMaxGeneration);
    }

    private void cutTree(Context context, Person person, int treeMaxGeneration) {
        if (person.getGeneration() != null && person.getGeneration() == treeMaxGeneration) {
            if (person.hasChildren()) {
                // cut the tree at this generation, create a clone for this person
                if (person.isFemale()) {
                    // create a clone of the person and shift all child relations to that clone
                    FemalePaginationClone clone = new FemalePaginationClone(person.personList, (Female) person);
                    PersonList childrenList = person.getChildrenList();
                    for (Person child : childrenList) {
                        child.setMother(clone);
                    }
                    person.resetChildrenList();
                    //PersonList childrenList2 = person.getChildrenList();
                    personList.add(clone);
                } else if (person.isMale()) {
                    // create a clone of the spouse and shift all child relations to that clone
                    MalePaginationClone clone = new MalePaginationClone(person.personList, (Male) person);
                    PersonList childrenList = person.getChildrenList();
                    for (Person child : childrenList) {
                        child.setFather(clone);
                    }
                    person.resetChildrenList();
                    personList.add(clone);
                }
            }
            return;
        }
        PersonList spouseList = person.getSpouseList();
        for (Person spouse : spouseList) {
            // children
            PersonList childrenList = person.getChildrenList(spouse);
            for (Person child : childrenList) {
                cutTree(context, child, treeMaxGeneration);
            }
        }
    }

    private void distributeTreeOnPages(Context context, PdfDocument pdfDocument) throws IOException, TransformerException {
        List<Male> rootFatherList = findRootFatherList();
        for (Person rootFather : rootFatherList) {
            int treeMaxgeneration = findMaxgeneration(rootFather);
            distributeTreeOnPages(context, pdfDocument, rootFather, treeMaxgeneration);
        }
    }

    private void distributeTreeOnPages(Context context, PdfDocument pdfDocument, Person person, int treeMaxGeneration) throws IOException, TransformerException {
        IsoPage page;
        int pageMaxGeneration = treeMaxGeneration;
        IsoPage isoPage = context.getParameterOptions().getTargetPaperSize();
        int wPortrait = (int) ((isoPage.getRect().getWidth() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonWidth(context));
        int hPortrait = (int) ((isoPage.getRect().getHeight() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonHeight(context));
        int wLandscap = (int) ((isoPage.getRect().getHeight() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonWidth(context));
        int hLandscape = (int) ((isoPage.getRect().getWidth() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonHeight(context));
        logger.info(String.format("%s portrait can fit: %d x %d person.", isoPage.getName(), wPortrait, hPortrait));
        logger.info(String.format("%s landscape can fit: %d x %d person.", isoPage.getName(), wLandscap, hLandscape));

        do {
            person.setPageIndex(pdfDocument.lastPageIndex);
            person.x = 0;
            person.y = 0;
            resetPageIndex(person);
            position(context, person, pageMaxGeneration);
            compact(context, pdfDocument, person, pageMaxGeneration + 1);
            validate(context, pageMaxGeneration + 1);
            Rect treeRect = person.getTreeRect(pageMaxGeneration);

            //if (person.getId() == 38)
            //drawDebugTree(context, person, pdfDocument.lastPageIndex);

            page = pdfDocument.findBestFittingPageSize(context, treeRect);
            pageMaxGeneration--;
        } while (page.compareTo(context.getParameterOptions().getTargetPaperSize()) > 0 && pageMaxGeneration != person.getGeneration());
        // we decided the page size and generation
        pageMaxGeneration++;
        logger.info(String.format("*-decided parent=%d pageIndex=%d pageSize=%s maxGeneration=G%d", person.getId(), person.getPageIndex(), page.getName(), pageMaxGeneration));

        cutTree(context, pdfDocument, person, pageMaxGeneration);// cut tree at that generation
        for (Person child : person.getDescendantList(pageMaxGeneration)) {
            logger.info(String.format("child%d=%d G%d", child.childIndex, child.getId(), child.getGeneration()));
        }
        float pageWidth = calclatePageWidth(pdfDocument.lastPageIndex);
        float pageHeight = calculatePageHeight(context, pdfDocument.lastPageIndex);
        pdfDocument.createPage(pdfDocument.lastPageIndex, pageWidth, pageHeight, person.getFirstName() + context.getParameterOptions().getOutputDecorator());
        drawPageSizeWatermark(pdfDocument, pdfDocument.lastPageIndex);
        drawPageAreaWatermark(pdfDocument, pdfDocument.lastPageIndex, person.getTreeRect());
        pdfDocument.lastPageIndex++;
        for (Person child : person.getDescendantList(pageMaxGeneration)) {
            // we only are interested in descendants that have children, otherwise there is no need to create a new page
            // there children have however been moved to their clone in the cutTree method above
            // we need to find the pagination clone
            Person clone = child.findPaginationClone();
            if (clone != null)
                distributeTreeOnPages(context, pdfDocument, clone, treeMaxGeneration);
        }
    }

    private void draw(Context context, PdfDocument pdfDocument) throws IOException, TransformerException {
        if (context.getParameterOptions().isDistributeOnPages())
            distributeTreeOnPages(context, pdfDocument);
        else
            createPages(context, pdfDocument);
        if (context.getParameterOptions().isDrawGrid())
            drawGrid(pdfDocument);
        for (int pageIndex = firstPageIndex; pageIndex <= pdfDocument.lastPageIndex; pageIndex++) {
            draw(context, pdfDocument, pageIndex);
            drawErrors(pdfDocument, pageIndex);
        }
    }

    abstract void draw(Context context, PdfDocument pdfDocument, int pageIndex) throws IOException;

    private void drawDebugTree(Context context, Person person, int pageIndex) throws IOException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument("debug.pdf");
        init(context, pdfDocument);
        float pageWidth = calclatePageWidth(pageIndex);
        float pageHeight = calculatePageHeight(context, pageIndex);
        pdfDocument.createPage(pageIndex, pageWidth, pageHeight, person.getFirstName() + context.getParameterOptions().getOutputDecorator());
        drawPageSizeWatermark(pdfDocument, pageIndex);
        drawPageAreaWatermark(pdfDocument, pageIndex, person.getTreeRect());
        draw(context, pdfDocument, pageIndex);
        pdfDocument.endDocument();
    }

    public void drawErrors(PdfDocument pdfDocument, int pageIndex) throws IOException {
//		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
//			PDPage page = pdfDocument.getPage(pageIndex);
//			p.setNonStrokingColor(Color.red);
//			p.setFont(pdfNameFont);
//
//			float y = page.getMediaBox().getHeight();
//			for (PageError pageError : errors) {
//				if (pageError.getPageIndex() != null && (pageError.getPageIndex() == pageIndex)) {
//					p.beginText();
//					p.newLineAtOffset(2, y);
//					String errorMessage;
//					errorMessage = String.format("%s", pageError.getError());
//					p.showText(errorMessage);
//					logger.error(errorMessage);
//					p.endText();
//					y -= p.getStringHeight();
//				}
//			}
//		}
    }

    private void drawGrid(PdfDocument pdfDocument) throws IOException {
        for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                PDPage page = pdfDocument.getPage(pageIndex);
                p.setLineWidth(0.5f);
                p.setStrokingColor(GRID_COLOR);
                p.setNonStrokingColor(GRID_COLOR);
                p.setFont(pdfDocument.getFont(NAME_FONT));
                float h = Person.getHeight(context) + Person.getYSpace(context);
                float w = Person.getWidth(context) + Person.getXSpace(context);

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

    private void drawPageAreaWatermark(PdfDocument pdfDocument, int pageIndex, Rect rect) throws IOException {
        try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
            PDPage page = pdfDocument.getPage(pageIndex);
            p.setNonStrokingColor(GRID_COLOR);
            p.setFont(pdfDocument.getFont(BIG_WATERMARK_FONT));
            float h1 = p.getStringHeight();
            p.setFont(pdfDocument.getFont(SMALL_WATERMARK_FONT));
            float h2 = p.getStringHeight();
            int w = (int) (rect.getX2() - rect.getX1() + 1);
            int h = (int) (rect.getY2() - rect.getY1() + 1);
            int area = w * h;
            String text = String.format("%d X %d = %d", w, h, area);
            p.beginText();
            p.newLineAtOffset(page.getBBox().getWidth() - p.getStringWidth(text), h1 + h2);
            p.showText(text);
            p.endText();
        }
    }

    private void drawPageSizeWatermark(PdfDocument pdfDocument, int pageIndex) throws IOException {
        try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
            PDPage page = pdfDocument.getPage(pageIndex);
            p.setNonStrokingColor(GRID_COLOR);
            p.setFont(pdfDocument.getFont(BIG_WATERMARK_FONT));
            String text = pdfDocument.getPageSizeName(pageIndex);
            p.beginText();
            p.newLineAtOffset(page.getBBox().getWidth() - p.getStringWidth(text), p.getStringHeight());
            p.showText(text);
            p.endText();
        }
    }

    int findMaxgeneration() {
        int maxGenration = -1;

        for (Person p : personList) {
            if (p.getGeneration() != null)
                maxGenration = Math.max(maxGenration, p.getGeneration());
        }
        return maxGenration;
    }

    int findMaxgeneration(Person father) {
        return findMaxgeneration(father, -1);
    }

    int findMaxgeneration(Person father, int maxGenration) {
        if (father.getGeneration() != null)
            maxGenration = Math.max(maxGenration, father.getGeneration());
        for (Person c : father.getChildrenList()) {
            maxGenration = Math.max(maxGenration, findMaxgeneration(c));
        }
        return maxGenration;
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
        analyzeTree();
        draw(context, pdfDocument);
    }

    public void generateErrorPage(PdfDocument pdfDocument) throws IOException {
        logger.info("Generating Error page ...");
        init(context, pdfDocument);
        analyzeTree();
        position(context, pdfDocument);
        compact(context, pdfDocument);
        validate(context);
        pdfDocument.createPage(PDRectangle.A4, "Errors");
        try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pdfDocument.lastPageIndex)) {
            p.setNonStrokingColor(Color.red);
            p.setFont(pdfDocument.getFont(NAME_FONT));

            float y = p.getStringHeight();
            for (int i = errors.size(); i-- > 0; ) {
                PageError error = errors.get(i);
                if (error.getPageIndex() == null) {
                    p.beginText();
                    p.newLineAtOffset(2, y);
                    String errorMessage;
                    errorMessage = String.format("%s", error.getError());
                    p.showText(errorMessage);
                    logger.error(errorMessage);
                    p.endText();
                    y += p.getStringHeight();
                }
            }
        }
    }

    private void init(Context context, PdfDocument pdfDocument) throws IOException {
        if (context.getParameterOptions().isCompact())
            pdfDocument.createFont(DATE_FONT, "NotoSans-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2) / 5);
        else
            pdfDocument.createFont(DATE_FONT, "NotoSans-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 5);
        pdfDocument.createFont(BIG_WATERMARK_FONT, "NotoSans-Bold.ttf", 128);
        pdfDocument.createFont(SMALL_WATERMARK_FONT, "NotoSans-Bold.ttf", 32);
        if (context.getParameterOptions().isCompact()) {
            pdfDocument.createFont(NAME_FONT, "NotoSans-Regular.ttf", (Person.getHeight(context)) / 2);
            pdfDocument.createFont(NAME_OL_FONT, "Amiri-Regular.ttf", (Person.getHeight(context)) / 2);
        } else {
            pdfDocument.createFont(NAME_FONT, "NotoSans-Bold.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 3);
            pdfDocument.createFont(NAME_OL_FONT, "Amiri-bold.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargine(context) * 2) / 3);
        }
    }

    private void position(Context context, PdfDocument pdfDocument) {
        List<Male> firstFathers = findRootFatherList();
        firstPageIndex = pdfDocument.lastPageIndex;
        for (Person firstFather : firstFathers) {
            firstFather.setPageIndex(pdfDocument.lastPageIndex++);
            firstFather.x = 0;
            firstFather.y = 0;
            position(context, firstFather, 1000);
        }
        pdfDocument.lastPageIndex--;
    }

    abstract float position(Context context, Person person, int treeMaxGeneration);

    private void resetPageIndex(Person person) {
        PersonList spouseList = person.getSpouseList();
        for (Person spouse : spouseList) {
            spouse.setPageIndex(null);
            spouse.setVisible(false);
            // children
            PersonList childrenList = person.getChildrenList(spouse);
            for (Person child : childrenList) {
                child.setPageIndex(null);
                child.setVisible(false);
                resetPageIndex(child);
            }
        }
    }

    private void validate(Context context) {
        for (Person p1 : personList) {
            for (Person p2 : personList) {
                if (!p1.equals(p2)) {
                    if (p1.isVisible() && p2.isVisible() && p1.getPageIndex() == p2.getPageIndex() && p1.x == p2.x && p1.y == p2.y) {
                        errors.add(new PageError(p1.getPageIndex(), String.format(ErrorMessages.ERROR_006_OVERLAPPING, p1.getId(), p1.getPageIndex(), p1.getFirstName(), p1.getLastName(), p2.getId(), p2.getPageIndex(),
                                p2.getFirstName(), p2.getLastName())));
                    }
                }
            }
        }
        for (Person p : personList) {
            p.validate(context);
        }
        for (Person p : personList) {
            if (!p.isVisible())
                errors.add(new PageError(p.getPageIndex(), String.format(ErrorMessages.ERROR_001_PERSON_IS_NOT_VISIBLE, p.getId(), p.getPageIndex(), p.getFirstName(), p.getLastName())));
        }
    }

    private void validate(Context context, int clipGeneration) {
        for (Person p1 : personList) {
            for (Person p2 : personList) {
                if (!p1.equals(p2)) {
                    if (p1.isVisible() && p2.isVisible() && p1.getPageIndex() == p2.getPageIndex() && p1.x == p2.x && p1.y == p2.y) {
                        if ((p1.getGeneration() == null || p1.getGeneration() < clipGeneration) && (p2.getGeneration() == null || p2.getGeneration() < clipGeneration))
                            errors.add(new PageError(p1.getPageIndex(), String.format(ErrorMessages.ERROR_006_OVERLAPPING, p1.getId(), p1.getPageIndex(), p1.getFirstName(), p1.getLastName(), p2.getId(),
                                    p2.getPageIndex(), p2.getFirstName(), p2.getLastName())));
                    }
                }
            }
        }
        for (Person p : personList) {
            if (p.isVisible() && p.getGeneration() != null && p.getGeneration() < clipGeneration)
                p.validate(context);
        }
        for (Person p : personList) {
            if (p.getGeneration() != null && p.getGeneration() < clipGeneration)
                if (!p.isVisible())
                    errors.add(new PageError(p.getPageIndex(), String.format(ErrorMessages.ERROR_001_PERSON_IS_NOT_VISIBLE, p.getId(), p.getPageIndex(), p.getFirstName(), p.getLastName())));
        }
    }

}
