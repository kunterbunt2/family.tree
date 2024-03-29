package de.bushnaq.abdalla.family.person;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DrawablePerson extends Person {
    public static final String BIG_WATERMARK_FONT = "BIG_WATERMARK_FONT";
    public static final String CHAPTER_FONT = "CHAPTER_FONT";
    public static final String DATE_FONT = "DATE_FONT";
    public static final String DEFAULT_IMAGE = "default.jpg";
    public static final String NAME_FONT = "NAME_FONT";
    public static final String NAME_OL_FONT = "NAME_OL_FONT";
    public static final String SMALL_WATERMARK_FONT = "SMALL_WATERMARK_FONT";
    private static final float FAT_LINE_STROKE_WIDTH = 2f;
    private static final float MEDIUM_LINE_STROKE_WIDTH = 2f;
    private final Color backgroundColor;
    private final Color borderColor = new Color(0, 0, 0, 64);
    private final Color connectorColor = Color.black;
    private final Color[] generationColors = {Color.red, Color.blue, Color.green, Color.orange, Color.gray};
    private final Color spouseBorderColor;
    private final Color textColor = new Color(0, 0, 0);

    public DrawablePerson(PersonList personList, DrawablePerson person, Color backgroundColor) {
        super(personList, person);
        this.backgroundColor = backgroundColor;
        this.spouseBorderColor = new Color(backgroundColor.getRGB());
    }

    public DrawablePerson(PersonList personList, Integer id, String familyLetter, Color backgroundColor) {
        super(personList, id, familyLetter);
        this.backgroundColor = backgroundColor;
        this.spouseBorderColor = new Color(backgroundColor.getRGB());
    }

    private void drawBox(Context context, PdfDocument pdfDocument) throws IOException {
        float x1 = xIndexToCoordinate(context, getX());    // x * (width + getXSpace(context));
        float y1 = yIndexToCoordinate(context, getY());    // y * (getHeight(context) + Person.getYSpace(context));

        if (getGeneration() != null && getGeneration() > 0 && !isSpouse() && hasChildren()) {
            // family tree background color

            if (context.getParameterOptions().isColorTrees()) {
                Rect treeRect = getTreeRect();
                float tx1 = xIndexToCoordinate(context, treeRect.x1);
                float ty1 = yIndexToCoordinate(context, treeRect.y1);
                float tx2 = xIndexToCoordinate(context, treeRect.x2);
                float ty2 = yIndexToCoordinate(context, treeRect.y2);
                try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                    p.setNonStrokingColor(getGenrationColor(getGeneration()), 0.05f);
                    p.fillRect(tx1, ty1, tx2 - tx1 + getWidth(context), ty2 - ty1 + getHeight(context));
                    p.fill();
                }
            }
        }

//        if (childIndex != null) {
//            float maxGeneration = personList.findMaxgeneration() * 2;
//            // family color border
//            Color color = Color.white;
//            if (!getFather().isSpouse() && getFather().getChildIndex() != null) {
//                if (getFather().getChildIndex() % 2 == 0) {
//                    color = Color.lightGray;
//                } else {
//                    color = Color.white;
//                }
//            } else if (!getMother().isSpouseClone() && getMother().getChildIndex() != null) {
//                if (getMother().getChildIndex() % 2 == 0) {
//                    color = Color.lightGray;
//                } else {
//                    color = Color.white;
//                }
//            }
//
////            Color color = new Color(.5f + getGeneration() / maxGeneration, .5f + getGeneration() / maxGeneration, .5f + getGeneration() / maxGeneration);
//
//            {
//                try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
//                    p.setNonStrokingColor(color);
//                    {
//                        //top
//                        float tx = x1 - getXSpace(context) / 2;
//                        float ty = y1 - getYSpace(context) / 2;
//                        float tw = getPersonWidth(context);
//                        float th = getYSpace(context) / 2;
//                        p.fillRect(tx, ty, tw, th);
//                    }
//                    {
//                        //bottom
//                        float tx = x1 - getXSpace(context) / 2;
//                        float ty = y1 + getHeight(context);
//                        float tw = getPersonWidth(context);
//                        float th = getYSpace(context) / 2;
//                        p.fillRect(tx, ty, tw, th);
//                    }
//                    {
//                        //left
//                        float tx = x1 - getXSpace(context) / 2;
//                        float ty = y1;
//                        float tw = getXSpace(context) / 2;
//                        float th = getHeight(context);
//                        p.fillRect(tx, ty, tw, th);
//                    }
//                    {
//                        //right
//                        float tx = x1 + getPersonWidth(context) - getXSpace(context);
//                        float ty = y1;
//                        float tw = getXSpace(context) / 2;
//                        float th = getHeight(context);
//                        p.fillRect(tx, ty, tw, th);
//                    }
//                    p.fill();
//                }
//            }
//        }
        if (!context.getParameterOptions().isCompact()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                // interior
                p.setNonStrokingColor(backgroundColor);
                p.fillRect(x1 + getImageWidth(context), y1, getWidth(context), getHeight(context));
                p.fill();
            }
            if (isSpouse() && !isFamilyMember(context)) {
                // clone border
                try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                    p.setStrokingColor(borderColor);
                    p.setLineWidth(getBorder(context));
                    p.setLineDashPattern(new float[]{1}, 0);
                    p.drawRect(x1 + getImageWidth(context), y1, getWidth(context), getHeight(context));
                    p.stroke();
                }
            } else {
                // border
                try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                    p.setStrokingColor(borderColor);
                    p.setLineWidth(getBorder(context));
                    p.setLineDashPattern(new float[]{}, 0);
                    p.drawRect(x1 + getImageWidth(context), y1, getWidth(context), getHeight(context));
                    p.stroke();
                }
            }
        }
        if (!context.getParameterOptions().isCompact()) {
            // image
            String imageFileName;
            Person father = getFather();
            if (this.isSpouseClone()) {
                father = this.getOriginalFather();
            }

            if (father != null)
                imageFileName = String.format("%s/images/%s.%s.%s.jpg", context.getParameterOptions().getInputFolder(), father.getFirstName().toLowerCase(), getFirstName().toLowerCase(), getLastName().toLowerCase());
            else
                imageFileName = String.format("%s/images/%s.%s.jpg", context.getParameterOptions().getInputFolder(), getFirstName().toLowerCase(), getLastName().toLowerCase());
            if (!new File(imageFileName).exists()) {
                imageFileName = "/images/" + DEFAULT_IMAGE;
            } else {
                logger.info(String.format("Found image %s.", imageFileName));
            }
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                float x2 = x1;
                float y2 = y1;
                float h = getImageHeight(context);
                float w = getImageWidth(context);
                p.setNonStrokingColor(Color.white);
                p.setStrokingColor(Color.white);
                p.drawImage(imageFileName, x2, y2, w, h);

            }
        }
        float firstNameHeight;
        {
            // first name
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                String text = getFirstNameAsString(context);
                if (isFirstNameOl(context))
                    p.setFont(p.getFontFittingWidth(pdfDocument.getFont(NAME_OL_FONT), getWidth(context), text));
                else
                    p.setFont(p.getFontFittingWidth(pdfDocument.getFont(NAME_FONT), getWidth(context), text));
                float stringWidth = p.getStringWidth(text);
                firstNameHeight = p.getStringHeight();
                float w = stringWidth;
                float x2;
                if (context.getParameterOptions().isShowImage())
                    x2 = x1 + +getImageWidth(context) + (getWidth(context)) / 2 - w / 2;
                else
                    x2 = x1 + (getWidth(context)) / 2 - w / 2;
                float y2;
                //if (context.getParameterOptions().isCompact())
                y2 = y1 + getBorder(context) + firstNameHeight;
                //else
                //y2 = y1 + getBorder(context) + firstNameHeight;
                p.setNonStrokingColor(textColor);
                if (text.contains("?"))
                    p.setNonStrokingColor(Color.red);
                drawTextMetric(p, x2, y2, text, context);
//                p.beginText();
//                p.newLineAtOffset(x2, y2);
//                p.showText(text);
//                p.endText();
                p.drawText(x2, y2, text);
            }
        }
        // last name
        {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                String text = getLastNameAsString(context);
                if (isLastNameOl(context))
                    p.setFont(p.getFontFittingWidth(pdfDocument.getFont(NAME_OL_FONT), getWidth(context), text));
                else
                    p.setFont(p.getFontFittingWidth(pdfDocument.getFont(NAME_FONT), getWidth(context), text));
                float stringWidth = p.getStringWidth(text);
                float lastNameHeight = p.getStringHeight();
                float w = stringWidth;

                float x2;
                if (context.getParameterOptions().isShowImage())
                    x2 = x1 + getImageWidth(context) + (getWidth(context)) / 2 - w / 2;
                else
                    x2 = x1 + (getWidth(context)) / 2 - w / 2;
                float y2;
                if (context.getParameterOptions().isCompact())
                    y2 = y1 + firstNameHeight + lastNameHeight;
                else
                    y2 = y1 + getBorder(context) + firstNameHeight + lastNameHeight;

                p.setNonStrokingColor(textColor);
                if (text.contains("?"))
                    p.setNonStrokingColor(Color.red);
                drawTextMetric(p, x2, y2, text, context);
//                p.beginText();
//                p.newLineAtOffset(x2, y2);
//                p.showText(text);
//                p.endText();
                p.drawText(x2, y2, text);
            }
        }

        {
            Person spouseClone = this.findSpouseClone();
            if (!context.getParameterOptions().isCompact()) {
                if (this.isSpouseClone() || spouseClone != null) {
                    // clone
                    try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                        p.setNonStrokingColor(textColor);
                        if (context.getParameterOptions().isCompact()) {
                            p.setFont(pdfDocument.getFont(DATE_FONT));
                        } else {
                            p.setFont(pdfDocument.getFont(NAME_FONT));
                        }
                        String text = "*";
                        if (spouseClone != null)
                            text = "O";
                        float x2 = x1 + getImageWidth(context) + getWidth(context) - p.getStringWidth(text) - getBorder(context) - getMargin(context);
                        float y2 = y1 + getBorder(context) + p.getStringHeight();
                        drawTextMetric(p, x2, y2, text, context);
//                        p.beginText();
//                        p.newLineAtOffset(x2, y2);
//                        p.showText(text);
//                        p.endText();
                        p.drawText(x2, y2, text);
                    }
                }
            }
            if (spouseClone != null || isSpouseClone()) {
                //there exists a clone of us somewhere
                //add link
                float x2;
                float y2;
                float w;
                float h;
                try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                    String text = "O";
                    w = p.getStringWidth(text);
                    h = p.getStringHeight();
                    x2 = x1 + getImageWidth(context) + getWidth(context) - w - getBorder(context) - getMargin(context);
                    y2 = y1 + getBorder(context);
//                    p.drawRect(x2,y2,w,h);
                }
                PDRectangle rectangle = new PDRectangle(x2, y2, w, h);
                PDPage sourcePage;
                PDPage targetPage;
                if (spouseClone != null) {
                    // original link to spouse clone
                    if (spouseClone.getPageIndex() != null) {
                        sourcePage = pdfDocument.getPage(getPageIndex());
                        targetPage = pdfDocument.getPage(spouseClone.getPageIndex());
                        pdfDocument.createPageLink(sourcePage, targetPage, rectangle);
                    }
                } else {
                    // spouse clone link to original
                    if (getOriginal().getPageIndex() != null) {
                        sourcePage = pdfDocument.getPage(getPageIndex());
                        targetPage = pdfDocument.getPage(getOriginal().getPageIndex());
                        pdfDocument.createPageLink(sourcePage, targetPage, rectangle);
                    }
                }
            }
        }
        {
            // ID
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setNonStrokingColor(textColor);
                if (context.getParameterOptions().isCompact()) {
                    p.setFont(pdfDocument.getFont(DATE_FONT));
                } else {
                    p.setFont(pdfDocument.getFont(NAME_FONT));
                }
                {
                    String text = getFamilyLetter() + getId();
                    float x2 = x1 + getImageWidth(context) + getBorder(context) + getMargin(context);
                    float y2 = y1 + getHeight(context) - getBorder(context);
                    drawTextMetric(p, x2, y2, text, context);
//                    p.beginText();
//                    p.newLineAtOffset(x2, y2);
//                    p.showText(text);
//                    p.endText();
                    p.drawText(x2, y2, text);
                }
            }
        }
        {
            // Generation
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                if (getGeneration() != null) {
                    p.setNonStrokingColor(textColor);
                    if (context.getParameterOptions().isCompact()) {
                        p.setFont(pdfDocument.getFont(DATE_FONT));
                    } else {
                        p.setFont(pdfDocument.getFont(NAME_FONT));
                    }
                    String text = "G" + getGeneration();
//                    float x2 = x1 + getImageWidth(context) + getBorder(context) + getMargin(context);
//                    float y2 = y1 + getBorder(context) + p.getStringHeight();
                    float x2 = x1 + getImageWidth(context) + getWidth(context) - p.getStringWidth(text) - getMargin(context) - getBorder(context);
                    float y2 = y1 + getHeight(context) - getBorder(context);
                    drawTextMetric(p, x2, y2, text, context);
//                    p.beginText();
//                    p.newLineAtOffset(x2, y2);
//                    p.showText(text);
//                    p.endText();
                    p.drawText(x2, y2, text);
                }
            }
        }
        if (context.getParameterOptions().isCoordinates()) {
            // Coordinates
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setNonStrokingColor(Color.blue);
                p.setFont(pdfDocument.getFont(NAME_FONT));
                {
                    String text = String.format("%d,%d", (int) getX(), (int) getY());
//                    float x2 = x1 + getImageWidth(context) + getWidth(context) - p.getStringWidth(text) - getMargin(context) - getBorder(context);
//                    float y2 = y1 + getHeight(context) - getBorder(context) - p.getStringHeight();
                    float x2 = x1 + getImageWidth(context) + getWidth(context) - p.getStringWidth("GX") - p.getStringWidth(text) - getMargin(context) - getBorder(context);
                    float y2 = y1 + getHeight(context) - getBorder(context);
                    drawTextMetric(p, x2, y2, text, context);
//                    p.beginText();
//                    p.newLineAtOffset(x2, y2);
//                    p.showText(text);
//                    p.endText();
                    p.drawText(x2, y2, text);
                }
            }
        }
        {
            // born
            if (!context.getParameterOptions().isCompact()) {
                try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                    p.setNonStrokingColor(textColor);
                    p.setFont(pdfDocument.getFont(DATE_FONT));
                    String text = getBornString();
                    float w = p.getStringWidth(text);
                    float h = p.getStringHeight();
                    float x2 = x1 + getImageWidth(context) + (getWidth(context)) / 2 - w / 2;
                    float y2 = y1 + getHeight(context) - h - getBorder(context);
                    drawTextMetric(p, x2, y2, text, context);
//                    p.beginText();
//                    p.newLineAtOffset(x2, y2);
//                    p.showText(text);
//                    p.endText();
                    p.drawText(x2, y2, text);
                }
            }
        }
        if (!context.getParameterOptions().isCompact()) {
            // died
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setNonStrokingColor(textColor);
                p.setFont(pdfDocument.getFont(DATE_FONT));
                String text = getDiedString();
                float w = p.getStringWidth(text);
                float x2 = x1 + getImageWidth(context) + (getWidth(context)) / 2 - w / 2;
                float y2 = y1 + getHeight(context) - getBorder(context);
                drawTextMetric(p, x2, y2, text, context);
//                p.beginText();
//                p.newLineAtOffset(x2, y2);
//                p.showText(text);
//                p.endText();
                p.drawText(x2, y2, text);
            }
        }

        {
            // errors
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setNonStrokingColor(Color.red);
                p.setFont(pdfDocument.getFont(DATE_FONT));
                {
                    if (errors.size() != 0) {
                        StringBuffer sb = new StringBuffer();
                        for (String s : errors) {
                            sb.append(s);
                            sb.append(",");
                        }
                        String text = sb.toString();
                        float h = p.getStringHeight();
                        float x2 = x1;
                        float y2 = y1 + getHeight(context) + h;
                        drawTextMetric(p, x2, y2, text, context);
//                        p.beginText();
//                        p.newLineAtOffset(x2, y2);
//                        p.showText(text);
//                        p.endText();
                        p.drawText(x2, y2, text);
                    }
                }
            }
        }
    }

    @Override
    public void drawHorizontal(Context context, PdfDocument pdfDocument) throws IOException {
        if (isVisible()) {
            drawBox(context, pdfDocument);
            drawHorizontalConnectors(context, pdfDocument);
        }
    }

    private void drawHorizontalConnectors(Context context, PdfDocument pdfDocument) throws IOException {
        float x1 = xIndexToCoordinate(context, getX());
        float y1 = yIndexToCoordinate(context, getY());

        Person clone = findPaginationClone();
        if (clone != null && clone.isPaginationClone()) {
            drawLabelBelow(context, pdfDocument, x1, y1, this, clone);
        }
        if (this.isPaginationClone()) {
            drawLabelAbove(context, pdfDocument, x1, y1, this, this.getOriginal());
        }

        // vertical child Connector to horizontal connector from parent spouse
        if (hasParents()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                Person sp = getSpouseParent();
                p.setLineDashPattern(new float[]{}, 0);
                //if we are the first child, connector is strait
                if (childIndex == 0)
                    p.drawLine(x1 + getPersonWidth(context) / 2, yIndexToCoordinate(context, sp.getY()) + getHeight(context) + getYSpace(context) / 2, x1 + getPersonWidth(context) / 2, y1);
                else {
                    //draw curve from left to bottom with the radius of spaceX and spaceY
                    p.drawCurveLeftToBottom(x1 + getPersonWidth(context) / 2 - getYSpace(context) / 2, yIndexToCoordinate(context, sp.getY()) + getHeight(context) + getYSpace(context) / 2, getYSpace(context) / 2);
                    p.drawLine(x1 + getPersonWidth(context) / 2, yIndexToCoordinate(context, sp.getY()) + getHeight(context) + getYSpace(context) / 2 + getYSpace(context) / 2, x1 + getPersonWidth(context) / 2, y1);
                }
                p.stroke();
            }
        }
        // horizontal child Connector
        if (isSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                if (getChildrenList().size() > 1) {// TODO why do we need to check for this?
                    p.setStrokingColor(connectorColor);
                    p.setLineWidth(getConnectorWidth(context));
                    float cx1 = x1 + getPersonWidth(context) / 2;
                    float cx2 = xIndexToCoordinate(context, getChildrenList().getLast().getX()) + getPersonWidth(context) / 2 - getYSpace(context) / 2;
                    p.setLineDashPattern(new float[]{}, 0);
                    p.drawLine(cx1 + getYSpace(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
                    p.stroke();
                }
            }
        }
        // spouse connector to children
        if (isSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                p.setLineDashPattern(new float[]{}, 0);
                p.drawLine(x1 + getPersonWidth(context) / 2, y1 + getHeight(context), x1 + getPersonWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
                if (getChildrenList().size() > 1)
                    p.drawCurveTopToRight(x1 + getPersonWidth(context) / 2, y1 + getHeight(context), getYSpace(context) / 2);
                p.stroke();
            }
        }

        // vertical sexual relation connector from person to his/her spouse
        if (hasChildren() && isFamilyMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineDashPattern(new float[]{1}, 0);
                p.setLineWidth(getConnectorWidth(context));
                float y2 = yIndexToCoordinate(context, getSpouseList().getFirst().getY());
                p.drawLine(x1 + getPersonWidth(context) / 2, y1 + getHeight(context), x1 + getPersonWidth(context) / 2, y2/*y1 + getHeight(context) + getYSpace(context) / 2*/);
                if (getSpouseList().size() > 1)
                    p.drawCurveTopToRight(x1 + getPersonWidth(context) / 2, y1 + getHeight(context), getYSpace(context) / 2);
                for (Person spouse : getSpouseList()) {
                    float sx = xIndexToCoordinate(context, spouse.getX());
                    if (spouse.spouseIndex == 0)
                        p.drawLine(sx + getPersonWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, sx + getPersonWidth(context) / 2, y1 + getHeight(context) + getYSpace(context));
                    else {
                        //draw curve from left to bottom with the radius of spaceX and spaceY
                        p.drawCurveLeftToBottom(sx + getPersonWidth(context) / 2 - getYSpace(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, getYSpace(context) / 2);
                        //p.drawLine(sx + getPersonWidth(context) / 2+getYSpace(context)/2, y1 + getHeight(context) + getYSpace(context) / 2, sx + getPersonWidth(context) / 2, y1 + getHeight(context) + getYSpace(context));
                    }
                }
                // horizontal
                float lsx = xIndexToCoordinate(context, getSpouseList().getLast().getX());
                if (getSpouseList().size() > 1)
                    p.drawLine(x1 + getPersonWidth(context) / 2 + getYSpace(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, lsx + getPersonWidth(context) / 2 - getYSpace(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
                p.stroke();
            }
        }
        // parent connector to children
        if (hasChildren() && isFamilyMember(context) && context.getParameterOptions().isExcludeSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getPersonWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
                float cx1 = y1 + getWidth(context) / 2;
                float cx2 = xIndexToCoordinate(context, getChildrenList().getLast().getX()) + getWidth(context) / 2;
                p.setLineDashPattern(new float[]{}, 0);
                p.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
                p.stroke();
            }
        }
    }

    private void drawLabel(Context context, PdfDocument pdfDocument, float x1, float y1, float labelSize, Person person, Person clone) throws IOException {
        try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, person.getPageIndex())) {
            int targetPageNumber = clone.getPageIndex() + 1;
            //float annotationSize = 24f;
            String text = getFamilyLetter() + targetPageNumber;
            p.setFont(pdfDocument.getFont(NAME_FONT));
            float stringWidth = p.getStringWidth(text);
            float stringHeight = p.getStringHeight();
            float w = stringWidth;
            float annotationX = x1;
            float annotationY = y1;
            float textX = annotationX - w / 2;
            float textY = y1 + stringHeight / 2;

            {
                //add background
                p.setNonStrokingColor(Color.red);
                p.drawCircle(annotationX, annotationY, labelSize / 2);
                p.fill();
            }
            {
                //add text
                p.setNonStrokingColor(Color.white);
                drawTextMetric(p, textX, textY, text, context);
//                p.beginText();
//                p.newLineAtOffset(textX, textY);
//                p.showText(text);
//                p.endText();
                p.drawText(textX, textY, text);
            }
            {
                //add link
                PDPage sourcePage = pdfDocument.getPage(getPageIndex());
                PDPage targetPage = pdfDocument.getPage(clone.getPageIndex());
                PDRectangle rectangle = new PDRectangle(annotationX - labelSize / 2, annotationY + labelSize / 2 - labelSize, labelSize, labelSize);
                pdfDocument.createPageLink(sourcePage, targetPage, rectangle);
            }
        }
    }


    private void drawLabelAbove(Context context, PdfDocument pdfDocument, float x1, float y1, DrawablePerson person, Person clone) throws IOException {
        float annotationSize = 16f;
        float annotationX;
        if (context.getParameterOptions().isShowImage()) {
            annotationX = x1 + +getImageWidth(context) + (getWidth(context)) / 2;
        } else {
            annotationX = x1 + (getWidth(context)) / 2;
        }
        drawLabel(context, pdfDocument, annotationX, y1 - annotationSize / 2, annotationSize, person, clone);
    }

    private void drawLabelBelow(Context context, PdfDocument pdfDocument, float x1, float y1, DrawablePerson person, Person clone) throws IOException {
        float annotationSize = 16f;
        float annotationX;
        if (context.getParameterOptions().isShowImage()) {
            annotationX = x1 + +getImageWidth(context) + (getWidth(context)) / 2;
        } else {
            annotationX = x1 + (getWidth(context)) / 2;
        }
        drawLabel(context, pdfDocument, annotationX, y1 + Person.getHeight(context) + annotationSize / 2, annotationSize, person, clone);
    }

    private void drawTextMetric(CloseableGraphicsState p, float x, float y, String text, Context context) throws IOException {
        if (context.getParameterOptions().isDrawTextMetric()) {
            Color color = p.getNonStrokingColor();
            PDFont font = p.getFont();
            float fontSize = p.getFontSize();
            float stringWidth = p.getStringWidth(text);
            float stringHeight = p.getStringHeight();
            PDFontDescriptor fd = font.getFontDescriptor();
            float ascent = fd.getAscent() * fontSize / 1000;
            float capHeight = fd.getCapHeight() * fontSize / 1000;
            float descent = -fd.getDescent() * fontSize / 1000;

            p.setNonStrokingColor(color, 0.1f);
            p.fillRect(x, y - stringHeight, stringWidth, ascent - capHeight);
            p.fill();

            p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            p.fillRect(x, y - capHeight - descent, stringWidth, capHeight);
            p.fill();

            p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            p.fillRect(x, y - descent, stringWidth, descent);
            p.fill();
            p.setNonStrokingColor(color);// set color back for text
        }
    }

    @Override
    public void drawVertical(Context context, PdfDocument pdfDocument) throws IOException {
        if (isVisible()) {
            drawBox(context, pdfDocument);
            drawVerticalConnectors(context, pdfDocument);
        }
    }

    private void drawVerticalConnectors(Context context, PdfDocument pdfDocument) throws IOException {
        float x1 = xIndexToCoordinate(context, getX());
        float y1 = yIndexToCoordinate(context, getY());

        // child Connector horizontal to parent direction
        if (hasParents()/* isMember(context) && !isSpouse() */) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                Person sp = getSpouseParent();
                p.setLineDashPattern(new float[]{}, 0);
                p.drawLine(xIndexToCoordinate(context, sp.getX()) + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1, y1 + getHeight(context) / 2);
                p.stroke();
            }
        }

        // child Connector vertical
        if (isSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                float cy1 = y1 + getHeight(context) / 2;
                float cy2 = yIndexToCoordinate(context, getChildrenList().getLast().getY()) + getHeight(context) / 2;
                p.setLineDashPattern(new float[]{}, 0);
                p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
                p.stroke();
            }
        }

        // spouse connector to children
        if (isSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                p.setLineDashPattern(new float[]{}, 0);
                p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
                p.stroke();
            }
        }

        // sexual relation connector from person to his/her spouse
        if (hasChildren() && isFamilyMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineDashPattern(new float[]{3}, 0);
                p.setLineWidth(getConnectorWidth(context));
                p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context), y1 + getHeight(context) / 2);
                for (Person spouse : getSpouseList()) {
                    float sx = xIndexToCoordinate(context, spouse.getX());
                    float sy = yIndexToCoordinate(context, spouse.getY());
                    p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, sy + getHeight(context) / 2, sx, sy + getHeight(context) / 2);
                }
                float lsy = yIndexToCoordinate(context, getSpouseList().getLast().getY());
                p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, lsy + getHeight(context) / 2);
                p.stroke();
            }
        }

        // spouse connector to children
        if (hasChildren() && isFamilyMember(context) && context.getParameterOptions().isExcludeSpouse()) {
            try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
                p.setStrokingColor(connectorColor);
                p.setLineWidth(getConnectorWidth(context));
                p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
                float cy1 = y1 + getHeight(context) / 2;
                float cy2 = yIndexToCoordinate(context, getChildrenList().getLast().getY()) + getHeight(context) / 2;
                p.setLineDashPattern(new float[]{}, 0);
                p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
                p.stroke();
            }
        }

    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    protected float getConnectorWidth(Context context) {
        return MEDIUM_LINE_STROKE_WIDTH * context.getParameterOptions().getZoom();
    }

    private Color getGenrationColor(int generation) {
        return generationColors[generation % generationColors.length];
    }

    protected float getSpecialBorderWidth(Context context) {
        return FAT_LINE_STROKE_WIDTH * context.getParameterOptions().getZoom();
    }

    private float xIndexToCoordinate(Context context, float x) {
        return getPageMargin(context) + x * getPersonWidth(context);
    }

    private float yIndexToCoordinate(Context context, float y) {
        return getPageMargin(context) + y * getPersonHeight(context);
    }

}
