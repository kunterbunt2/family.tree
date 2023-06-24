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
import java.util.Objects;

import static de.bushnaq.abdalla.family.person.DrawablePerson.*;

public abstract class Tree {

    private static final Color GRID_COLOR = new Color(0x2d, 0xb1, 0xff, 32);
    public final List<PageError> errors = new ArrayList<>();
    protected final Context context;
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final PersonList personList;
    PersonList visitedList = new PersonList();
    private int firstPageIndex;

    public Tree(Context context, PersonList personList) {
        this.context = context;
        this.personList = personList;
        this.personList.reset();// in case tree was called once before
        errors.clear();
    }

    private void analyzeTree() {
        char familyLetter = 'A';
        List<Person> rottFatherList = personList.findRootFatherList(context);
        for (Person rootFather : rottFatherList) {
            rootFather.setFirstFather(true);
            rootFather.setGeneration(0);
            rootFather.setFamilyLetter(String.valueOf(familyLetter));
            rootFather.analyzeTree(context);
            familyLetter += 1;
        }
    }

    float calculatePageHeight(Context context, int pageIndex) {
        float minY = Integer.MAX_VALUE;
        float maxY = Integer.MIN_VALUE;
        for (Person p : personList) {
            if (p.isVisible() && pageIndex == p.getPageIndex()) {
                float y = p.getY();
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }
        return (maxY - minY + 1) * DrawablePerson.getPersonHeight(context) + DrawablePerson.getPageMargin(context) * 2;
    }

    //protected abstract void compact(Context context2, PdfDocument pdfDocument);

    float calculatePageWidth(int pageIndex) {
        float minX = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        for (Person p : personList) {
            if (p.isVisible() && pageIndex == p.getPageIndex()) {
//                logger.info(String.format("[%d] x=%f",p.getId(), p.x));
                float x = p.getX();
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
        }
        return (maxX - minX + 1) * DrawablePerson.getPersonWidth(context) + DrawablePerson.getPageMargin(context) * 2;
    }

    protected abstract void compact(Context context2, PdfDocument pdfDocument, Person rootFather, int includingGeneration);

//    private void createPages(Context context, PdfDocument pdfDocument) throws IOException {
//        position(context, pdfDocument);
//        compact(context, pdfDocument);
//        List<Person> rootFatherList = personList.findRootFatherList(context);
//        for (Person rootFather : rootFatherList) {
//            float pageWidth = calculatePageWidth(rootFather.getPageIndex());
//            float pageHeight = calculatePageHeight(context, rootFather.getPageIndex());
//            pdfDocument.createPage(rootFather.getPageIndex(), pageWidth, pageHeight, rootFather.getFirstName() + context.getParameterOptions().getOutputDecorator());
//            drawPageSizeWatermark(pdfDocument, rootFather.getPageIndex());
//            drawPageAreaWatermark(pdfDocument, rootFather.getPageIndex(), rootFather.getTreeRect());
//        }
//    }

    private void createFonts(Context context, PdfDocument pdfDocument) throws IOException {
        if (context.getParameterOptions().isCompact())
            pdfDocument.createFont(DATE_FONT, "NotoSans-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2) / 5);
        else
            pdfDocument.createFont(DATE_FONT, "NotoSans-Regular.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargin(context) * 2) / 5);
        pdfDocument.createFont(BIG_WATERMARK_FONT, "NotoSans-Bold.ttf", 128);
        pdfDocument.createFont(SMALL_WATERMARK_FONT, "NotoSans-Bold.ttf", 32);
        if (context.getParameterOptions().isCompact()) {
            pdfDocument.createFont(NAME_FONT, "NotoSans-Regular.ttf", (Person.getHeight(context)) / 2);
            pdfDocument.createFont(NAME_OL_FONT, "Amiri-Regular.ttf", (Person.getHeight(context)) / 2);
        } else {
            pdfDocument.createFont(NAME_FONT, "NotoSans-Bold.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargin(context) * 2) / 3);
            pdfDocument.createFont(NAME_OL_FONT, "Amiri-bold.ttf", (Person.getHeight(context) - Person.getBorder(context) * 2 - Person.getMargin(context) * 2) / 3);
        }
    }

    /**
     * cut tree at this person, who will exist as child of his parents but also as root father of his own tree
     *
     * @param person
     */
    private void cutPerson(Person person) {
        if (person.isFemale()) {
            // create a clone of the person and shift all child relations to that clone
            Person clone = Clone.createPaginationClone(person.personList, person);
            PersonList childrenList = person.getChildrenList();
            for (Person child : childrenList) {
                child.setMother(clone);
            }
            person.resetChildrenList();
            personList.add(clone);
        } else if (person.isMale()) {
            // create a clone of the spouse and shift all child relations to that clone
            Person clone = Clone.createPaginationClone(person.personList, person);
            PersonList childrenList = person.getChildrenList();
            for (Person child : childrenList) {
                child.setFather(clone);
            }
            person.resetChildrenList();
            personList.add(clone);
        }
    }

    private void cutTree(Context context, PdfDocument pdfDocument, Person person, int includingGeneration) {
        logger.info(String.format("cutting tree for parent=%d genration <= G%d", person.getId(), includingGeneration));
        cutTree(context, person, includingGeneration);
    }

    private void cutTree(Context context, Person person, int includingGeneration) {
        if (person.getGeneration() != null && person.getGeneration() == includingGeneration) {
            if (person.hasChildren()) {
                // cut the tree at this generation, create a clone for this person
                cutPerson(person);
            }
            return;
        }
        PersonList spouseList = person.getSpouseList();
        for (Person spouse : spouseList) {
            // children
            PersonList childrenList = person.getChildrenList(spouse);
            for (Person child : childrenList) {
                cutTree(context, child, includingGeneration);
            }
        }
    }

    private void distributeTreeBottomUpOnPages(Context context, PdfDocument pdfDocument) throws Exception {
        List<Person> rootFatherList = personList.findRootFatherList(context);
        for (Person rootFather : rootFatherList) {
            resetPageIndex(rootFather);
        }

        visitedList.clear();
//        char familyLetter = 'A';
        boolean changed;
        do {
            changed = false;
            List<Person> lastGenerationList = findLastGeneration(context);
            for (Person lastGeneration : lastGenerationList) {
//                lastGeneration.setFamilyLetter(String.valueOf(familyLetter));
//                familyLetter += 1;
                //int treeMaxGeneration = findMaxGeneration(rootFather);
                if (!visitedList.contains(lastGeneration))// ignore if already distributed onto a page
                {
                    logger.info(String.format(">-starting with child=%d pageIndex=%d G%d", lastGeneration.getId(), pdfDocument.lastPageIndex, lastGeneration.getGeneration()));
                    distributeTreeBottomUpOnPages(context, pdfDocument, lastGeneration);
                    changed = true;
                    break;
                }
            }
        }
        while (changed);
    }

    private boolean distributeTreeBottomUpOnPages(Context context, PdfDocument pdfDocument, Person person) throws Exception {
        // TODO if there is no tree that we can find, the person is added to teh visitedList, that is a mistake
        // Example is 5
        // we should keep retrying to fit it.
        int pageMaxGeneration = personList.findMaxGeneration();

        IsoPage page = findIsoPage(context, pdfDocument, pageMaxGeneration, person);
        if (page.compareTo(context.getParameterOptions().getTargetPaperSize()) > 0)
            return false;// tree is too big for page
        boolean fitsOnPage;
        if (person.getTreeHead() == null) {
            // we are the head of this tree
            fitsOnPage = false;// mark as failed
        } else {
            fitsOnPage = distributeTreeBottomUpOnPages(context, pdfDocument, person.getTreeHead());
        }
        if (!fitsOnPage)// in the last iteration the tree was too big for the page
        {

            if (person.getDescendantList().isEmpty()) {
                logger.info(String.format("x-not worth moving parent=%d pageIndex=%d maxGeneration=G%d", person.getId(), person.getPageIndex(), pageMaxGeneration));
//                if(person.getTreeHead()!=null)
//                    resetPageIndex(person.getTreeHead());// the actual head will stay on the page of its parents, we will only take over its clone
                visitedList.add(person);// person has no tree worth moving onto a new page
            } else {
                if (person.getTreeHead() != null)
                    resetPageIndex(person.getTreeHead());// the actual head will stay on the page of its parents, we will only take over its clone
                page = findIsoPage(context, pdfDocument, pageMaxGeneration, person);// run successful treeHead again
                // we decided the page size and generation
                logger.info(String.format("*-decided parent=%d pageIndex=%d pageSize=%s maxGeneration=G%d", person.getId(), person.getPageIndex(), page.getName(), pageMaxGeneration));
                visitedList.addAll(person.getDescendantList());// all our children do not need to be distributed anymore
                if (person.getTreeHead() != null) {
                    cutPerson(person);
                    visitedList.add(person.findPaginationClone());
                    person.setPageIndex(null);
                    person.setVisible(false);
                } else {
                    visitedList.add(person);
                }
                float pageWidth = calculatePageWidth(pdfDocument.lastPageIndex);
                float pageHeight = calculatePageHeight(context, pdfDocument.lastPageIndex);
                Person person1 = personList.get(personList.size() - 1);
                pdfDocument.createPage(pdfDocument.lastPageIndex, pageWidth, pageHeight, person.getFirstName() + context.getParameterOptions().getOutputDecorator());
                drawPageSizeWatermark(pdfDocument, pdfDocument.lastPageIndex);
                drawPageAreaWatermark(pdfDocument, pdfDocument.lastPageIndex, person.getTreeRect());
                pdfDocument.lastPageIndex++;
            }
        }
        return true;
    }

    private void distributeTreeTopDownOnPages(Context context, PdfDocument pdfDocument) throws Exception {
        List<Person> rootFatherList = personList.findRootFatherList(context);
        if(rootFatherList.size()==0)
            throw new Exception("Did not find root father");
        char familyLetter = 'A';
        for (Person rootFather : rootFatherList) {
            rootFather.setFamilyLetter(String.valueOf(familyLetter));
            familyLetter += 1;
            int treeMaxGeneration = findMaxgeneration(rootFather);
            distributeTreeTopDownOnPages(context, pdfDocument, rootFather, treeMaxGeneration);
        }
    }

    private void distributeTreeTopDownOnPages(Context context, PdfDocument pdfDocument, Person person, int treeMaxGeneration) throws Exception {
        IsoPage page;
        int pageMaxGeneration = treeMaxGeneration;

        IsoPage isoPage = new IsoPage(new PDRectangle(9999, 9999), ">A0");
        if (context.getParameterOptions().isDistributeOnPages())
            isoPage = context.getParameterOptions().getTargetPaperSize();
//        int wPortrait = (int) ((isoPage.getRect().getWidth() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonWidth(context));
//        int hPortrait = (int) ((isoPage.getRect().getHeight() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonHeight(context));
//        int wLandscape = (int) ((isoPage.getRect().getHeight() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonWidth(context));
//        int hLandscape = (int) ((isoPage.getRect().getWidth() - DrawablePerson.getPageMargin(context) * 2) / DrawablePerson.getPersonHeight(context));
//        logger.info(String.format("%s portrait can fit: %d x %d person.", isoPage.getName(), wPortrait, hPortrait));
//        logger.info(String.format("%s landscape can fit: %d x %d person.", isoPage.getName(), wLandscape, hLandscape));

        do {
            //TODO consider using findIsoPage
            page = findIsoPage(context, pdfDocument, pageMaxGeneration, person);// run successful treeHead again
//            resetPageIndex(person);
//            person.setPageIndex(pdfDocument.lastPageIndex);
//            person.setX(0);
//            person.setY(0);
//            position(context, person, pageMaxGeneration);
//            compact(context, pdfDocument, person, pageMaxGeneration);
//            validate(context, pageMaxGeneration);
//            Rect treeRect = person.getTreeRect(pageMaxGeneration);

            //if (person.getId() == 38)
            //drawDebugTree(context, person, pdfDocument.lastPageIndex);

//            page = pdfDocument.findBestFittingPageSize(context, treeRect);
            pageMaxGeneration--;
        } while (page.compareTo(isoPage) > 0 && pageMaxGeneration != person.getGeneration());
        // we decided the page size and generation
        pageMaxGeneration++;
        logger.info(String.format("*-decided parent=%d pageIndex=%d pageSize=%s maxGeneration=G%d", person.getId(), person.getPageIndex(), page.getName(), pageMaxGeneration));

        cutTree(context, pdfDocument, person, pageMaxGeneration);// cut tree at that generation
        for (Person child : person.getDescendantList(pageMaxGeneration)) {
            logger.info(String.format("child%d=%d G%d", child.childIndex, child.getId(), child.getGeneration()));
        }
        float pageWidth = calculatePageWidth(pdfDocument.lastPageIndex);
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
                distributeTreeTopDownOnPages(context, pdfDocument, clone, treeMaxGeneration);
        }
    }

    private void draw(Context context, PdfDocument pdfDocument) throws Exception {
        switch (context.getParameterOptions().getDistributeOnPagesMode()) {
            case BOTTOM_UP -> {
                distributeTreeBottomUpOnPages(context, pdfDocument);
            }
            case TOP_DOWN -> {
                distributeTreeTopDownOnPages(context, pdfDocument);
            }
        }
//        if (context.getParameterOptions().isDistributeOnPages())
//            distributeTreeTopDownOnPages(context, pdfDocument);
//        else
//            createPages(context, pdfDocument);
        if (context.getParameterOptions().isDrawGrid())
            drawGrid(pdfDocument);
        for (int pageIndex = firstPageIndex; pageIndex <= pdfDocument.lastPageIndex; pageIndex++) {
            draw(context, pdfDocument, pageIndex);
            //drawErrors(pdfDocument, pageIndex);
        }
    }

    abstract void draw(Context context, PdfDocument pdfDocument, int pageIndex) throws IOException;

    private void drawDebugTree(Context context, Person person, int pageIndex) throws IOException, TransformerException {
        PdfDocument pdfDocument = new PdfDocument("debug.pdf");
        createFonts(context, pdfDocument);
        float pageWidth = calculatePageWidth(pageIndex);
        float pageHeight = calculatePageHeight(context, pageIndex);
        pdfDocument.createPage(pageIndex, pageWidth, pageHeight, person.getFirstName() + context.getParameterOptions().getOutputDecorator());
        drawPageSizeWatermark(pdfDocument, pageIndex);
        drawPageAreaWatermark(pdfDocument, pageIndex, person.getTreeRect());
        draw(context, pdfDocument, pageIndex);
        pdfDocument.endDocument();
    }

    private void drawGrid(PdfDocument pdfDocument) throws IOException {
        for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                PDPage page = pdfDocument.getPage(pageIndex);
                p.setLineWidth(0.5f);
                p.setStrokingColor(GRID_COLOR);
                p.setNonStrokingColor(GRID_COLOR);
                p.setFont(pdfDocument.getFont(NAME_FONT));
                float h = Person.getPersonHeight(context);
                float w = Person.getPersonWidth(context);

                for (int y = 0; y < page.getBBox().getHeight() / h + 1; y++) {
                    p.drawRect(getPageMargin(context) + 0, getPageMargin(context) - Person.getYSpace(context) / 2 + y * h, page.getBBox().getWidth(), 1);
                    for (int x = 0; x < page.getBBox().getWidth() / w + 1; x++) {
                        p.drawRect(getPageMargin(context) - Person.getXSpace(context) / 2 + x * w, getPageMargin(context) + 0, 1, page.getBBox().getHeight());
                        p.beginText();
                        p.newLineAtOffset(getPageMargin(context) + x * w, getPageMargin(context) - Person.getYSpace(context) / 2 + y * h + Person.getPersonHeight(context));
                        p.showText(String.format("%d,%d", x, y));
                        p.endText();
                    }
                }
                p.stroke();
            }
        }
    }

    //public void drawErrors(PdfDocument pdfDocument, int pageIndex) {
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
    //}

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

    private IsoPage findIsoPage(Context context, PdfDocument pdfDocument, int pageMaxGeneration, Person treeHead) throws Exception {
        IsoPage page;
        resetPageIndex(treeHead);
        treeHead.setPageIndex(pdfDocument.lastPageIndex);
        treeHead.setX(0);
        treeHead.setY(0);
        position(context, treeHead, pageMaxGeneration);
        compact(context, pdfDocument, treeHead, pageMaxGeneration);
        validate(context, pageMaxGeneration);
        Rect treeRect = treeHead.getTreeRect(pageMaxGeneration);
        //if (person.getId() == 38)
        //drawDebugTree(context, person, pdfDocument.lastPageIndex);
        page = pdfDocument.findBestFittingPageSize(context, treeRect);
        return page;
    }

    public List<Person> findLastGeneration(Context context) {
        List<Person> lastGenerationList = new ArrayList<>();
        for (Person p : personList) {
            Person clone = p.findSpouseClone();
            if (p.isChild() && !p.hasChildren() && (clone == null || !clone.hasChildren())) {
                if (!visitedList.contains(p))
                    lastGenerationList.add(p);
            }
        }
        return lastGenerationList;
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


    public void generate(Context context, PdfDocument pdfDocument, String familyName) throws Exception {
        logger.info(String.format("Generating %s tree ...", context.getParameterOptions().getOutputDecorator()));
        createFonts(context, pdfDocument);
        analyzeTree();
        draw(context, pdfDocument);
    }

    public List<PageError> generateErrorPage(PdfDocument pdfDocument) throws Exception {
        boolean createErrorPage = false;
        for (int i = errors.size(); i-- > 0; ) {
            PageError error = errors.get(i);
            if (error.getPageIndex() == null) {
                createErrorPage = true;
            }
        }
        if (createErrorPage) {
            logger.info("Generating Error page ...");
            createFonts(context, pdfDocument);
            analyzeTree();
            position(context, pdfDocument);
            //compact(context, pdfDocument);
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
        return errors;
    }

    private void position(Context context, PdfDocument pdfDocument) {
        List<Person> rootFatherList = personList.findRootFatherList(context);
        firstPageIndex = pdfDocument.lastPageIndex;
        char familyLetter = 'A';
        for (Person rootFather : rootFatherList) {
            rootFather.setPageIndex(pdfDocument.lastPageIndex++);
            rootFather.setFamilyLetter(String.valueOf(familyLetter));
            familyLetter += 1;
            rootFather.setX(0);
            rootFather.setY(0);
            position(context, rootFather, 1000);
        }
        pdfDocument.lastPageIndex--;
    }

    abstract float position(Context context, Person person, int includingGeneration);

    private void resetPageIndex(Person person) {
        person.setPageIndex(null);
        person.setVisible(false);
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

    private void validate(Context context) throws Exception {
        for (Person p1 : personList) {
            for (Person p2 : personList) {
                if (!p1.equals(p2)) {
                    if (p1.isVisible() && p2.isVisible() && p1.getPageIndex() == p2.getPageIndex() && p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
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

    private void validate(Context context, int includingGeneration) throws Exception {
        // find overlapping person
        for (Person p1 : personList) {
            for (Person p2 : personList) {
                if (!p1.equals(p2)) {
                    if (p1.isVisible() && p2.isVisible() && Objects.equals(p1.getPageIndex(), p2.getPageIndex()) && p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
                        if ((p1.getGeneration() == null || p1.getGeneration() <= includingGeneration) && (p2.getGeneration() == null || p2.getGeneration() <= includingGeneration))
                            errors.add(new PageError(p1.getPageIndex(), String.format(ErrorMessages.ERROR_006_OVERLAPPING, p1.getId(), p1.getPageIndex(), p1.getFirstName(), p1.getLastName(), p2.getId(),
                                    p2.getPageIndex(), p2.getFirstName(), p2.getLastName())));
                    }
                }
            }
        }
        for (Person p : personList) {
            if (p.isVisible() && p.getGeneration() != null && p.getGeneration() <= includingGeneration)
                p.validate(context);
        }
        for (Person p : personList) {
            if (p.getGeneration() != null && p.getGeneration() <= includingGeneration)
                if (!p.isVisible())
                    errors.add(new PageError(p.getPageIndex(), String.format(ErrorMessages.ERROR_001_PERSON_IS_NOT_VISIBLE, p.getId(), p.getPageIndex(), p.getFirstName(), p.getLastName())));
        }
    }

}
