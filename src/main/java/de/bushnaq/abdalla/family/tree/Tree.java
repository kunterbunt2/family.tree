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

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static de.bushnaq.abdalla.family.person.DrawablePerson.*;

public abstract class Tree {
    private static final int COVER_PAGE_INDEX = 0;
    private static final String ERROR_PAGE_NAME = "Errors";
    private static final Color GRID_COLOR = new Color(0x2d, 0xb1, 0xff, 32);
    protected final Context context;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final PersonList personList;
    private final List<PageError> errors = new ArrayList<>();
    Date now = new Date();
    //    PersonList visitedList = new PersonList();// TODO use visited field in person instead
    private int firstTreePageIndex = 0;
    private int errorPageIndex;

    public Tree(Context context, PersonList personList) {
        this.context = context;
        this.personList = personList;
        this.personList.reset();// in case tree was called once before
        errors.clear();
    }

    private static void drawTocRecord(PdfDocument pdfDocument, CloseableGraphicsState p, PDPage page, PDPage targetPage, String bulletText, String nameText, String pageNumberText, float y, boolean indent) throws IOException {
        float w2 = page.getBBox().getWidth() / 2;
        float x1 = page.getBBox().getWidth() / 2 - w2 / 2 - 16;
        float x3 = page.getBBox().getWidth() / 2 + w2 / 2;
        {
            p.beginText();
            p.newLineAtOffset(x1, y);
            p.showText(bulletText);
            p.endText();
        }
        {
            float x2 = page.getBBox().getWidth() / 2 - w2 / 2;
            if (indent)
                x2 += 10;
            p.beginText();
            p.newLineAtOffset(x2, y);
            p.showText(nameText);
            p.endText();
        }
        {
            p.beginText();
            p.newLineAtOffset(x3, y);
            p.showText(pageNumberText);
            p.endText();
        }
        {
            float x = x3 + p.getStringWidth(pageNumberText);
            float width = x - x1;
//                            PDPage sourcePage = pdfDocument.getPage(pageIndex);
            PDRectangle rectangle = new PDRectangle(x1, y - p.getStringHeight(), width, p.getStringHeight());
            pdfDocument.createPageLink(page, targetPage, rectangle);
//                        p.setStrokingColor(Color.red);
//                        p.drawRect(x1,y-p.getStringHeight(), width, p.getStringHeight());
//                        p.stroke();
        }
    }

    private void analyzeTree() {
        List<Person> rootFatherList = personList.findTreeRootList(context);
        logger.info(String.format("Found %d root fathers.", rootFatherList.size()));
        for (Person rootFather : rootFatherList) {
            rootFather.setFirstFather(true);
            rootFather.setGeneration(0);
            rootFather.analyzeTree(context);
            logger.info(String.format("%s", rootFather));
        }
    }

    float calculatePageHeight(int pageIndex) {
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

    private void createCoverPage(Context context, PdfDocument pdfDocument) throws IOException {
        if (context.getParameterOptions().isCoverPage()) {
            IsoPage page = new IsoPage(PDRectangle.A4, "A4");
            IsoPage minPageSize = context.getParameterOptions().getMinPaperSize();
            if (page.compareTo(minPageSize) < 0) {
                page = minPageSize;
            }
            float pageWidth = page.getRect().getWidth();
            float pageHeight = page.getRect().getHeight();
            pdfDocument.createPage(COVER_PAGE_INDEX, pageWidth, pageHeight, "Cover Page");
        }
    }

    private void createErrorPage(Context context, PdfDocument pdfDocument) throws IOException {
        boolean createErrorPage = errors.size() != 0;
        if (createErrorPage) {
            errorPageIndex = pdfDocument.createPage(PDRectangle.A4, ERROR_PAGE_NAME);
        }
    }

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

    private void createPages(Context context, PdfDocument pdfDocument) throws IOException {
        createCoverPage(context, pdfDocument);
        PersonList treeHeadList = personList.findTreeHeadList(context);
        for (Person person : treeHeadList) {
            Rect rect = person.getTreeRect();
            IsoPage page = pdfDocument.findBestFittingPageSize(context, rect);
            IsoPage minPageSize = context.getParameterOptions().getMinPaperSize();
            if (page.compareTo(minPageSize) < 0) {
                page = minPageSize;
            }
            float pageWidth = page.getRect().getWidth();
            float pageHeight = page.getRect().getHeight();
            pdfDocument.createPage(person.getPageIndex(), pageWidth, pageHeight, person.getFirstName() + " " + person.getLastName());
            drawPageSizeWatermark(pdfDocument, person.getPageIndex());
            drawPageNumber(pdfDocument, person.getPageIndex());
            drawFooter(pdfDocument, person.getPageIndex(), getFootertext());
            drawPageAreaWatermark(pdfDocument, person.getPageIndex(), person.getTreeRect());
        }
        createErrorPage(context, pdfDocument);
        drawCoverPage(pdfDocument, COVER_PAGE_INDEX);
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

    private void cutSubtree(Context context, PdfDocument pdfDocument, Person person, int pageMaxGeneration) throws Exception {
        // TODO can we reuse this code and similar code in createPages?
        IsoPage minPageSize = context.getParameterOptions().getTargetPaperSize();
        IsoPage page = findIsoPage(context, pdfDocument, pageMaxGeneration, person);// run successful treeHead again
        if (page.compareTo(minPageSize) < 0)
            page = minPageSize;
        // we decided the page size and generation
        logger.info(String.format("*-decided parent=%d pageIndex=%d pageSize=%s maxGeneration=G%d", person.getId(), person.getPageIndex(), page.getName(), pageMaxGeneration));
        for (Person p : person.getDescendantList()) {
            // all our children do not need to be distributed anymore
            p.setVisited(true);
        }
        if (person.getTreeHead() != null) {
            cutPerson(person);
            person.findPaginationClone().setVisited(true);
            person.setPageIndex(null);
            person.setVisible(false);
        } else {
            person.setVisited(true);
        }
        pdfDocument.lastPageIndex++;
    }

    private void cutTreeBranches(Context context, PdfDocument pdfDocument, Person person, int includingGeneration) {
        logger.info(String.format("cutting tree for parent=%d genration <= G%d", person.getId(), includingGeneration));
        cutTreeBranches(context, person, includingGeneration);
    }

    private void cutTreeBranches(Context context, Person person, int includingGeneration) {
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
                cutTreeBranches(context, child, includingGeneration);
            }
        }
    }

    private void distributeTreeBottomUpOnPages(Context context, PdfDocument pdfDocument) throws Exception {
        List<Person> rootFatherList = personList.findTreeRootList(context);
        for (Person rootFather : rootFatherList) {
            resetPageIndex(rootFather);
        }
        personList.clearVisited();
//        visitedList.clear();
        boolean changed;
        do {
            changed = false;
            List<Person> lastGenerationList = findLastGeneration(context);
            for (Person lastGeneration : lastGenerationList) {
//                if (!visitedList.contains(lastGeneration))// ignore if already distributed onto a page
                if (!lastGeneration.isVisited())// ignore if already distributed onto a page
                {
                    logger.info(String.format(">-starting with child=%d pageIndex=%d G%d", lastGeneration.getId(), pdfDocument.lastPageIndex, lastGeneration.getGeneration()));
                    boolean pageCreated = distributeTreeBottomUpOnPages(context, pdfDocument, lastGeneration);
                    if (!pageCreated) {
                        //we found no person that we could cut and no page big enough, we are forced to cut where we are
                        logger.warn(String.format("was unable to find correct page size for child=%d G%d, using bigger paper size.", lastGeneration.getId(), pdfDocument.lastPageIndex, lastGeneration.getGeneration()));
                        int pageMaxGeneration = personList.findMaxGeneration();
                        cutSubtree(context, pdfDocument, lastGeneration, pageMaxGeneration);
                    }
                    changed = true;
                    break;
                }
            }
        }
        while (changed);
        orderSubtrees(pdfDocument);
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
//                visitedList.add(person);// person has no tree worth moving onto a new page
                person.setVisited(true);// person has no tree worth moving onto a new page
            } else {
                if (person.getTreeHead() != null)
                    resetPageIndex(person.getTreeHead());// the actual head will stay on the page of its parents, we will only take over its clone
                cutSubtree(context, pdfDocument, person, pageMaxGeneration);// cut subtree and move to new page
            }
        }
        return true;
    }

    private void distributeTreeTopDownOnPages(Context context, PdfDocument pdfDocument) throws Exception {
        List<Person> rootFatherList = personList.findTreeRootList(context);
        if (rootFatherList.size() == 0)
            throw new Exception("Did not find root father");
        for (Person rootFather : rootFatherList) {
            int treeMaxGeneration = findMaxgeneration(rootFather);
            distributeTreeTopDownOnPages(context, pdfDocument, rootFather, treeMaxGeneration);
        }
    }

    private void distributeTreeTopDownOnPages(Context context, PdfDocument pdfDocument, Person person, int treeMaxGeneration) throws Exception {
        IsoPage minPageSize = context.getParameterOptions().getTargetPaperSize();
        IsoPage page;
        int pageMaxGeneration = treeMaxGeneration;

        IsoPage targetPageSize = new IsoPage(new PDRectangle(9999, 9999), ">A0");// no size restriction?
        if (context.getParameterOptions().isDistributeOnPages())
            targetPageSize = context.getParameterOptions().getTargetPaperSize();// target size
        do {
            page = findIsoPage(context, pdfDocument, pageMaxGeneration, person);// run successful treeHead again
            pageMaxGeneration--;
        } while (page.compareTo(targetPageSize) > 0 && pageMaxGeneration != person.getGeneration());
        if (page.compareTo(minPageSize) < 0)
            page = minPageSize;// too small

        // we decided the page size and generation
        pageMaxGeneration++;
        logger.info(String.format("*-decided parent=%d pageIndex=%d pageSize=%s maxGeneration=G%d", person.getId(), person.getPageIndex(), page.getName(), pageMaxGeneration));

        cutTreeBranches(context, pdfDocument, person, pageMaxGeneration);// cut tree at that generation
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
        validate(context);
        createPages(context, pdfDocument);
        if (context.getParameterOptions().isShowGrid())
            drawGrid(pdfDocument);
        for (int pageIndex = firstTreePageIndex; pageIndex <= pdfDocument.lastPageIndex; pageIndex++) {
            draw(context, pdfDocument, pageIndex);
        }
        drawErrorPage(pdfDocument);
    }

    abstract void draw(Context context, PdfDocument pdfDocument, int pageIndex) throws IOException;

    private void drawCoverPage(PdfDocument pdfDocument, int pageIndex) throws IOException {
        if (context.getParameterOptions().isCoverPage()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                PDPage page = pdfDocument.getPage(pageIndex);
                p.setNonStrokingColor(Color.black);
                {
                    p.setFont(pdfDocument.getFont(BIG_WATERMARK_FONT));
                    float h = p.getStringHeight();
                    String text = context.getParameterOptions().getFamilyName();
                    float w = p.getStringWidth(text);
                    float x = page.getBBox().getWidth() / 2 - w / 2;
                    float y = page.getBBox().getHeight() / 4;
                    p.beginText();
                    p.newLineAtOffset(x, y);
                    p.showText(text);
                    p.endText();
                }
                {
                    p.setFont(pdfDocument.getFont(SMALL_WATERMARK_FONT));
                    float h = p.getStringHeight();
                    String text = "family.tree";
                    float w1 = p.getStringWidth(text);
                    float x = page.getBBox().getWidth() / 2 - w1 / 2;
                    float y = page.getBBox().getHeight() / 4 + h;
                    p.beginText();
                    p.newLineAtOffset(x, y);
                    p.showText(text);
                    p.endText();
                }
                {
                    p.setFont(pdfDocument.getFont(SMALL_WATERMARK_FONT));
                    float h0 = p.getStringHeight();
                    p.setFont(pdfDocument.getFont(NAME_FONT));
                    PersonList treeRootList = personList.findTreeHeadList(context);
                    int i = 0;
                    for (Person person : treeRootList) {
                        PDPage targetPage = pdfDocument.getPage(person.getPageIndex());
                        p.setNonStrokingColor(Color.black);
                        String bulletText = String.format("%d", i);
                        String nameText = String.format("%s %s", person.getFirstName(), person.getLastName());
                        String pageNumberText = String.format("%d", person.getPageIndex() + 1);
                        float h1 = p.getStringHeight();
                        float y = page.getBBox().getHeight() / 4 + h0 + 20 + h1 * i;
                        boolean indent = !person.isTreeRoot(context);

                        drawTocRecord(pdfDocument, p, page, targetPage, bulletText, nameText, pageNumberText, y, indent);
                        i++;
                    }
                    if (errors.size() != 0) {
                        float h1 = p.getStringHeight();
                        float y = page.getBBox().getHeight() / 4 + h0 + 20 + h1 * i;
                        drawTocRecord(pdfDocument, p, page, pdfDocument.getPage(errorPageIndex), String.format("%d", i), ERROR_PAGE_NAME, String.format("%d", errorPageIndex + 1), y, false);
                    }
                }
            }
            drawFooter(pdfDocument, pageIndex, getFootertext());
        }
    }

    private List<PageError> drawErrorPage(PdfDocument pdfDocument) throws Exception {
        boolean createErrorPage = errors.size() != 0;

        if (createErrorPage) {
            logger.info("Generating Error page ...");
            createFonts(context, pdfDocument);
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, errorPageIndex)) {
                p.setNonStrokingColor(Color.red);
                p.setFont(pdfDocument.getFont(NAME_FONT));

                float y = p.getStringHeight();
                for (int i = errors.size(); i-- > 0; ) {
                    PageError error = errors.get(i);
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
        return errors;
    }

//    private void drawDebugTree(Context context, Person person, int pageIndex) throws IOException, TransformerException {
//        PdfDocument pdfDocument = new PdfDocument("debug.pdf");
//        createFonts(context, pdfDocument);
//        float pageWidth = calculatePageWidth(pageIndex);
//        float pageHeight = calculatePageHeight(pageIndex);
//        pdfDocument.createPage(pageIndex, pageWidth, pageHeight, person.getFirstName() + context.getParameterOptions().getOutputDecorator());
//        drawPageSizeWatermark(pdfDocument, pageIndex);
//        drawPageAreaWatermark(pdfDocument, pageIndex, person.getTreeRect());
//        draw(context, pdfDocument, pageIndex);
//        pdfDocument.endDocument();
//    }

    private void drawFooter(PdfDocument pdfDocument, Integer pageIndex, String footerText) throws IOException {
        try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
            PDPage page = pdfDocument.getPage(pageIndex);
            p.setNonStrokingColor(Color.gray);
            p.setFont(pdfDocument.getFont(NAME_FONT));
            float h1 = p.getStringHeight();
//            String text = String.format("%d", pageIndex + 1);
            p.beginText();
            p.newLineAtOffset(10, page.getBBox().getHeight() - h1);
            p.showText(footerText);
            p.endText();
        }
    }

    private void drawGrid(PdfDocument pdfDocument) throws IOException {
        for (int pageIndex = 0; pageIndex < pdfDocument.getNumberOfPages(); pageIndex++) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                PDPage page = pdfDocument.getPage(pageIndex);
                p.setLineWidth(0.5f);
                p.setStrokingColor(GRID_COLOR);
                p.setFont(pdfDocument.getFont(NAME_FONT));
                float h = Person.getPersonHeight(context);
                float w = Person.getPersonWidth(context);

                int maxY = (int) ((page.getBBox().getHeight() - getPageMargin(context) * 2) / h) + 1;
                for (int y = 0; y < maxY; y++) {
                    // horizontal line
                    {
                        float x1 = getPageMargin(context) - Person.getXSpace(context) / 2;
                        float x2 = getPageMargin(context) - Person.getXSpace(context) / 2 + ((int) ((page.getBBox().getWidth() - getPageMargin(context) * 2) / w)) * w;
                        float y1 = getPageMargin(context) - Person.getYSpace(context) / 2 + y * h;
                        float y2 = y1;
                        p.drawLine(x1, y1, x2, y2);
                    }
                    int maxX = (int) ((page.getBBox().getWidth() - getPageMargin(context) * 2) / w) + 1;
                    for (int x = 0; x < maxX; x++) {
                        // vertical line
                        {
                            float x1 = getPageMargin(context) - Person.getXSpace(context) / 2 + x * w;
                            float y1 = getPageMargin(context) - Person.getYSpace(context) / 2;
                            float x2 = x1;
                            float y2 = getPageMargin(context) - Person.getYSpace(context) / 2 + ((int) ((page.getBBox().getHeight() - getPageMargin(context) * 2) / h)) * h;
                            p.drawLine(x1, y1, x2, y2);
                        }
//                         draw coordinates
//                        if (x < maxX - 1 && y < maxY - 1) {
//                            p.setFont(pdfDocument.getFont(DATE_FONT));
//                            p.setNonStrokingColor(Color.black);
//                            p.beginText();
//                            p.newLineAtOffset(getPageMargin(context) + x * w, getPageMargin(context) - Person.getYSpace(context) / 2 + y * h + Person.getPersonHeight(context));
//                            p.showText(String.format("%d,%d", x, y));
//                            p.endText();
//                        }
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

    private void drawPageNumber(PdfDocument pdfDocument, Integer pageIndex) throws IOException {
        try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
            PDPage page = pdfDocument.getPage(pageIndex);
            p.setNonStrokingColor(Color.black);
            p.setFont(pdfDocument.getFont(NAME_FONT));
            float h1 = p.getStringHeight();
            String text = String.format("%d", pageIndex + 1);
            p.beginText();
            p.newLineAtOffset(page.getBBox().getWidth() - 10, page.getBBox().getHeight() - h1);
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
//        validate(context, pageMaxGeneration);
        Rect treeRect = treeHead.getTreeRect(pageMaxGeneration);
        //if (person.getId() == 38)
        //drawDebugTree(context, person, pdfDocument.lastPageIndex);
        page = pdfDocument.findBestFittingPageSize(context, treeRect);
        return page;
    }

    /**
     * find the bottom of the tree
     *
     * @param context
     * @return
     */
    private List<Person> findLastGeneration(Context context) {
        List<Person> lastGenerationList = new ArrayList<>();
        for (Person p : personList) {
//            if (!visitedList.contains(p)) {
            if (!p.isVisited()) {
                Person clone = p.findSpouseClone();
                boolean allChildrenVisited = true;
                for (Person c : p.getChildrenList()) {
//                    if (!visitedList.contains(c))
                    if (!c.isVisited())
                        allChildrenVisited = false;
                }
                //a child without children or only children that have been visited that is not a spouse clone or is a spouse clone without children
                if (p.isChild() && (!p.hasChildren() || allChildrenVisited) && (clone == null || !clone.hasChildren())) {
                    lastGenerationList.add(p);
                }
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

    public List<PageError> generate(Context context, PdfDocument pdfDocument, String familyName) throws Exception {
        logger.info(String.format("Generating %s tree ...", context.getParameterOptions().getOutputDecorator()));
        if (context.getParameterOptions().isCoverPage()) {
            firstTreePageIndex++;// reserve first page for cover page
            pdfDocument.lastPageIndex++;
        }
        createFonts(context, pdfDocument);
        analyzeTree();
        draw(context, pdfDocument);
        return errors;
    }

    private String getFootertext() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        //get current date time with Date()
        String date = dateFormat.format(new Date());
        return String.format("Generated %s by family.tree. See https://github.com/kunterbunt2/family.tree", date);
    }

    private void orderSubtrees(PdfDocument pdfDocument) throws Exception {
        // let's order the pages according to the natural order of the tree from top-down and left to right
        personList.clearVisited();
        PersonList treeHeadList = personList.findTreeHeadList(context);
        int newPageIndex = firstTreePageIndex;
        for (Person person : treeHeadList) {
            if (!person.isVisited()) {
                rearrangePage(person.getPageIndex(), newPageIndex);
                newPageIndex++;
            }
        }
    }

//    private void position(Context context, PdfDocument pdfDocument) {
//        List<Person> rootFatherList = personList.findTreeRootList(context);
//        firstPageIndex = pdfDocument.lastPageIndex;
//        for (Person rootFather : rootFatherList) {
//            rootFather.setPageIndex(pdfDocument.lastPageIndex++);
//            rootFather.setX(0);
//            rootFather.setY(0);
//            position(context, rootFather, 1000);
//        }
//        pdfDocument.lastPageIndex--;
//    }

    abstract float position(Context context, Person person, int includingGeneration);

    /**
     * move every person located on the page with sourcePageIndex to the new targetPageIndex
     * moved person will be marked as visited to prevent mixing up new and old pages that have same value
     *
     * @param sourcePageIndex
     * @param targetPageIndex
     */
    private void rearrangePage(Integer sourcePageIndex, int targetPageIndex) {
        for (Person person : personList) {
            if (!person.isVisited() && person.getPageIndex() == sourcePageIndex) {
                person.setPageIndex(targetPageIndex);
                person.setVisited(true);
            }
        }
    }

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

    private void validate(Context context/*, int includingGeneration*/) throws Exception {
        // find overlapping person
        for (Person p1 : personList) {
            for (Person p2 : personList) {
                if (!p1.equals(p2)) {
                    if (p1.isVisible() && p2.isVisible() && Objects.equals(p1.getPageIndex(), p2.getPageIndex()) && p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
                        errors.add(new PageError(p1.getPageIndex(), String.format(ErrorMessages.ERROR_006_OVERLAPPING, p1, p2)));
                    }
                }
            }
        }
        for (Person p : personList) {
            if (p.isVisible() /*&& p.getGeneration() != null && p.getGeneration() <= includingGeneration*/)
                p.validate(context);
        }
        for (Person p : personList) {
            if (!p.isVisible())
                errors.add(new PageError(p.getPageIndex(), String.format(ErrorMessages.ERROR_001_PERSON_IS_NOT_VISIBLE, p.toString())));
        }
    }

}
