package de.bushnaq.abdalla.pdf;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.common.PDPageLabels;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

public class PdfDocument implements Closeable {
	private PDDocument					document;
	private String						documentFileName;
	IsoPage[]							isoPageList				= {					//
			new IsoPage(PDRectangle.A6, "A6"),										//
			new IsoPage(PDRectangle.A5, "A5"),										//
			new IsoPage(PDRectangle.A4, "A4"),										//
			new IsoPage(PDRectangle.A3, "A3"),										//
			new IsoPage(PDRectangle.A2, "A2"),										//
			new IsoPage(PDRectangle.A1, "A1"),										//
			new IsoPage(PDRectangle.A0, "A0") };
	public int							lastPageIndex			= 0;
	Map<Integer, PDPageContentStream>	pageContentStreamMap	= new HashMap<>();
	PDPageLabels						pageLabels;

	Map<Integer, PDPage>				pageMap					= new HashMap<>();

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

	public void closeOperation(int pageIndex) throws IOException {
		pageContentStreamMap.get(pageIndex).close();
		PDPage				page			= pageMap.get(pageIndex);
		PDPageContentStream	contentStream	= new PDPageContentStream(document, page, AppendMode.APPEND, true, true);
		pageContentStreamMap.put(pageIndex, contentStream);
	}

	private void createMetaDate() throws TransformerException, IOException {
		XMPMetadata xmp = XMPMetadata.createXMPMetadata();
		try {
			DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
			dc.setTitle("family.tree");
			PDFAIdentificationSchema id = xmp.createAndAddPFAIdentificationSchema();
			id.setPart(1);
			id.setConformance("B");
			XmpSerializer			serializer	= new XmpSerializer();
			ByteArrayOutputStream	baos		= new ByteArrayOutputStream();
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

	public PDPage createPage(int pageIndex, float pageWidth, float pageHeight, String label) throws IOException {
//		PDRectangle	mediaBox	= new PDRectangle(pageWidth, pageHeight);
		PDRectangle	bestFitting	= findBestFittingPageSize(pageWidth, pageHeight).getRect();
		PDPage		page		= new PDPage(bestFitting);
		pageMap.put(pageIndex, page);
		document.addPage(page);
		createPageLabel(pageIndex, label);
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		pageContentStreamMap.put(pageIndex, contentStream);
		return page;
	}

	public PDPage createPage(PDRectangle mediaBox, String label) throws IOException {
		PDPage page = new PDPage(mediaBox);
		lastPageIndex = pageMap.keySet().size();
		pageMap.put(lastPageIndex, page);
		document.addPage(page);
		createPageLabel(lastPageIndex, label);
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		pageContentStreamMap.put(lastPageIndex, contentStream);
		return page;
	}

	private void createPageLabel(int pageIndex, String label) {
		PDPageLabelRange pageLabelRange1 = new PDPageLabelRange();
		pageLabelRange1.setPrefix(label);
		pageLabels.setLabelItem(pageIndex, pageLabelRange1);
		document.getDocumentCatalog().setPageLabels(pageLabels);

	}

	public void endDocument() throws IOException {
		for (Integer pageIndex : pageContentStreamMap.keySet()) {
			PDPageContentStream contentStream = pageContentStreamMap.get(pageIndex);
			contentStream.close();
		}
		document.save(documentFileName);
		document.close();
	}

	private IsoPage findBestFittingPageSize(float w, float h) {
		if (w >= h) {
			for (int i = 0; i < isoPageList.length; i++) {
				IsoPage pageSize = isoPageList[i];
				if (pageSize.getRect().getWidth() >= h && pageSize.getRect().getHeight() >= w)
					return new IsoPage(pageSize.getRect().getHeight(), pageSize.getRect().getWidth(), pageSize.getName());
			}
		} else {
			for (int i = 0; i < isoPageList.length; i++) {
				IsoPage pageSize = isoPageList[i];
				if (pageSize.getRect().getWidth() >= w && pageSize.getRect().getHeight() >= h)
					return pageSize;
			}
		}

		return new IsoPage(w, h, ">A0");
	}

	public PDPageContentStream getContentStream(int pageIndex) {
		return pageContentStreamMap.get(pageIndex);
	}

	public int getNumberOfPages() {
		return pageMap.keySet().size();
	}

	public PDPage getPage(int pageIndex) throws IOException {
		PDPage pdPage = pageMap.get(pageIndex);
		return pdPage;
	}

	public float getPageHeight(int pageIndex) throws IOException {
		return getPage(pageIndex).getBBox().getHeight();
	}

	public String getPageSizeName(int pageIndex) throws IOException {
		PDPage	page	= getPage(pageIndex);
		float	w		= page.getBBox().getWidth();
		float	h		= page.getBBox().getHeight();

		if (w >= h) {
			for (int i = 0; i < isoPageList.length; i++) {
				IsoPage pageSize = isoPageList[i];
				if (pageSize.getRect().getWidth() >= h && pageSize.getRect().getHeight() >= w)
					return pageSize.getName();
			}
		} else {
			for (int i = 0; i < isoPageList.length; i++) {
				IsoPage pageSize = isoPageList[i];
				if (pageSize.getRect().getWidth() >= w && pageSize.getRect().getHeight() >= h)
					return pageSize.getName();
			}
		}

		return ">A0";
	}

	private void includeColorProfile() throws IOException {
		// sRGB output intent
		InputStream		colorProfile	= PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/pdfa/sRGB.icc");
		PDOutputIntent	intent			= new PDOutputIntent(document, colorProfile);
		intent.setInfo("sRGB IEC61966-2.1");
		intent.setOutputCondition("sRGB IEC61966-2.1");
		intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
		intent.setRegistryName("http://www.color.org");
		document.getDocumentCatalog().addOutputIntent(intent);
	}

	public PDFont loadFont(String fontName) throws IOException {
		InputStream	fontStream	= PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/" + fontName);
		PDFont		font		= PDType0Font.load(document, fontStream);
		return font;
	}

	private void startDocument() throws IOException, TransformerException {
		document = new PDDocument();
		createMetaDate();
		includeColorProfile();
		pageLabels = new PDPageLabels(document);
	}

}
