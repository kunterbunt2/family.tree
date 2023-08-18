package de.bushnaq.abdalla.pdf;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.DrawablePerson;
import de.bushnaq.abdalla.family.person.Rect;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.common.PDPageLabels;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PdfDocument implements Closeable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    final Map<String, PDImageXObject> imageMap = new HashMap<>();
    final IsoPage[] isoPageList = {                    //
            new IsoPage(IsoRectangle.A6, "A6"),                                        //
            new IsoPage(IsoRectangle.A6_LANDSCAPE, "A6 landscape"),                                        //
            new IsoPage(IsoRectangle.A5, "A5"),                                        //
            new IsoPage(IsoRectangle.A5_LANDSCAPE, "A5 landscape"),                                        //
            new IsoPage(IsoRectangle.A4, "A4"),                                        //
            new IsoPage(IsoRectangle.A4_LANDSCAPE, "A4 landscape"),                                        //
            new IsoPage(IsoRectangle.A3, "A3"),                                        //
            new IsoPage(IsoRectangle.A3_LANDSCAPE, "A3 landscape"),                                        //
            new IsoPage(IsoRectangle.A2, "A2"),                                        //
            new IsoPage(IsoRectangle.A2_LANDSCAPE, "A2 landscape"),                                        //
            new IsoPage(IsoRectangle.A1, "A1"),                                        //
            new IsoPage(IsoRectangle.A1_LANDSCAPE, "A1 landscape"),                                        //
            new IsoPage(IsoRectangle.A0, "A0"),
            new IsoPage(IsoRectangle.A0_LANDSCAPE, "A0 landscape")};
    final Map<Integer, PDPageContentStream> pageContentStreamMap = new HashMap<>();
    final Map<Integer, PDPage> pageMap = new HashMap<>();
    private final String documentFileName;
    private final Map<String, PdfFont> fontMap = new HashMap<>();
    public int lastPageIndex = 0;
    PDPageLabels pageLabels;
    private PDDocument document;

    public PdfDocument(String fileName) throws IOException, TransformerException {
        this.documentFileName = fileName;
        startDocument();
    }

    public PdfDocument(String fileName, PDRectangle media) throws IOException, TransformerException {
        this(fileName);
        createPage(0, media.getWidth(), media.getHeight(), "");
    }

    @Override
    public void close() throws IOException {
        endDocument();
    }

    public void createFont(String fontLabel, String fontName, float fontSize) throws IOException {
        PDFont pdFont = loadFont(fontName);
        PdfFont pdfFont = new PdfFont(pdFont, fontSize / getFontSize(pdFont));
        fontMap.put(fontLabel, pdfFont);
    }

//    public void closeOperation(int pageIndex) throws IOException {
//        pageContentStreamMap.get(pageIndex).close();
//        PDPage page = pageMap.get(pageIndex);
//        PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true);
//        pageContentStreamMap.put(pageIndex, contentStream);
//    }

    private void createMetaData() throws TransformerException, IOException {
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        try {
            DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle("family.tree");
            PDFAIdentificationSchema id = xmp.createAndAddPFAIdentificationSchema();
            id.setPart(1);
            id.setConformance("B");
            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);
            PDMetadata metadata = new PDMetadata(document);
            metadata.importXMPMetadata(baos.toByteArray());
            document.getDocumentCatalog().setMetadata(metadata);
        } catch (BadFieldValueException e) {
            // won't happen here, as the provided value is valid
            throw new IllegalArgumentException(e);
        }
        PDDocumentInformation pdd = document.getDocumentInformation();
        pdd.setAuthor("family.tree");
    }

    public void createPage(int pageIndex, float pageWidth, float pageHeight, String label) throws IOException {
        PDRectangle bestFitting = findBestFittingPageSize(pageWidth, pageHeight).getRect();
        PDPage page = new PDPage(bestFitting);
        pageMap.put(pageIndex, page);
        document.addPage(page);
//        createPageLabel(pageIndex, (pageIndex + 1) + " - " + label);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        pageContentStreamMap.put(pageIndex, contentStream);
    }


    public int createPage(PDRectangle mediaBox, String label) throws IOException {
        PDPage page = new PDPage(mediaBox);
        lastPageIndex = pageMap.keySet().size();
        pageMap.put(lastPageIndex, page);
        document.addPage(page);
//        createPageLabel(lastPageIndex, (lastPageIndex + 1) + " - " + label);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        pageContentStreamMap.put(lastPageIndex, contentStream);
        return lastPageIndex;
    }

    private void createPageLabel(int pageIndex, String label) {
        PDPageLabelRange pageLabelRange1 = new PDPageLabelRange();
        pageLabelRange1.setPrefix(label);
        pageLabels.setLabelItem(pageIndex, pageLabelRange1);
        document.getDocumentCatalog().setPageLabels(pageLabels);

    }

    public void createPageLink(PDPage sourcePage, PDPage targetPage, PDRectangle rectangle) throws IOException {
        PDAnnotationLink link = new PDAnnotationLink();
        link.setBorderStyle(null);
        link.setBorder(new COSArray());
        PDPageDestination destination = new PDPageFitWidthDestination();
        PDActionGoTo action = new PDActionGoTo();
        destination.setPage(targetPage);
        action.setDestination(destination);
        link.setAction(action);
        link.setPage(sourcePage);
        rectangle.setLowerLeftY(sourcePage.getBBox().getHeight() - rectangle.getLowerLeftY());
        rectangle.setUpperRightY(sourcePage.getBBox().getHeight() - rectangle.getUpperRightY());
        link.setRectangle(rectangle);
        sourcePage.getAnnotations().add(link);
    }

    public void endDocument() throws IOException {
        for (Integer pageIndex : pageContentStreamMap.keySet()) {
            PDPageContentStream contentStream = pageContentStreamMap.get(pageIndex);
            contentStream.close();
        }
        document.save(documentFileName);
        document.close();
    }

    public IsoPage findBestFittingPageSize(Context context, Rect treeRect) {
        float w = (treeRect.getX2() - treeRect.getX1() + 1);
        float h = (treeRect.getY2() - treeRect.getY1() + 1);
        return findBestFittingPageSize(w * DrawablePerson.getPersonWidth(context) + DrawablePerson.getPageMargin(context) * 2, h * DrawablePerson.getPersonHeight(context) + DrawablePerson.getPageMargin(context) * 2);
    }

    public IsoPage findBestFittingPageSize(float w, float h) {
        for (int i = 0; i < isoPageList.length; i++) {
            IsoPage pageSize = isoPageList[i];
            if (pageSize.getRect().getWidth() >= w && pageSize.getRect().getHeight() >= h)
                return pageSize;
        }
        return new IsoPage(w, h, ">A0");
    }

    public PDPageContentStream getContentStream(int pageIndex) {
        return pageContentStreamMap.get(pageIndex);
    }

    public PDDocument getDocument() {
        return document;
    }

    public PdfFont getFont(String fontName) {
        return fontMap.get(fontName);
    }

    private float getFontSize(PDFont font) {
        return (-font.getFontDescriptor().getDescent() + font.getFontDescriptor().getCapHeight() + (font.getFontDescriptor().getAscent() - font.getFontDescriptor().getCapHeight())) / 1000;
    }

    public PDImageXObject getImage(String imageFileName) throws IOException {
        PDImageXObject pdImage = imageMap.get(imageFileName);
        if (pdImage == null) {
            if (imageFileName.endsWith(DrawablePerson.DEFAULT_IMAGE)) {
                byte[] bytes = IOUtils.resourceToByteArray(imageFileName);
                pdImage = PDImageXObject.createFromByteArray(getDocument(), bytes, DrawablePerson.DEFAULT_IMAGE);

            } else {
                pdImage = PDImageXObject.createFromFile(imageFileName, getDocument());
            }
            imageMap.put(imageFileName, pdImage);
        }
        return pdImage;
    }

    public int getNumberOfPages() {
        return pageMap.keySet().size();
    }

    public PDPage getPage(int pageIndex) {
        PDPage pdPage = pageMap.get(pageIndex);
        return pdPage;
    }

    public float getPageHeight(int pageIndex) throws IOException {
        return getPage(pageIndex).getBBox().getHeight();
    }

    public IsoPage getPageSize(int pageIndex) {
        PDPage page = getPage(pageIndex);
        float w = page.getBBox().getWidth();
        float h = page.getBBox().getHeight();

        if (w >= h) {
            for (IsoPage pageSize : isoPageList) {
                if (pageSize.getRect().getWidth() >= h && pageSize.getRect().getHeight() >= w)
                    return pageSize;
            }
        } else {
            for (IsoPage pageSize : isoPageList) {
                if (pageSize.getRect().getWidth() >= w && pageSize.getRect().getHeight() >= h)
                    return pageSize;
            }
        }
        return null;
    }

    public String getPageSizeName(int pageIndex) throws IOException {
        IsoPage isoPage = getPageSize(pageIndex);
        if (isoPage != null)
            return isoPage.getName();
        return ">A0";
    }

    private void includeColorProfile() throws IOException {
        // sRGB output intent
        InputStream colorProfile = PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/pdfa/sRGB.icc");
        PDOutputIntent intent = new PDOutputIntent(document, colorProfile);
        intent.setInfo("sRGB IEC61966-2.1");
        intent.setOutputCondition("sRGB IEC61966-2.1");
        intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
        intent.setRegistryName("http://www.color.org");
        document.getDocumentCatalog().addOutputIntent(intent);
    }

    public PDFont loadFont(String fontName) throws IOException {
        InputStream fontStream = PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/" + fontName);
        PDFont font = PDType0Font.load(document, fontStream);
        return font;
    }

    private void startDocument() throws IOException, TransformerException {
        document = new PDDocument();
        createMetaData();
        includeColorProfile();
        pageLabels = new PDPageLabels(document);
    }
}
