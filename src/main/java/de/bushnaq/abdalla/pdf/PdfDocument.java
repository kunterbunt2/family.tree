package de.bushnaq.abdalla.pdf;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
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
	private PDRectangle			bBox;
	private PDPageContentStream	contentStream;
	private PDDocument			document;
	private String				fileName;
	private PDPage				page;

	public PdfDocument(PDRectangle mediaBox) throws IOException, TransformerException {
		startDocument(mediaBox);
	}

	public PdfDocument(String fileName, int imageWidth, int imageHeight) throws IOException, TransformerException {
		this.fileName = fileName;
		PDRectangle mediaBox = new PDRectangle(imageWidth, imageHeight);
		startDocument(mediaBox);
	}

	@Override
	public void close() throws IOException {
		endDocument();
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
	}

	public void endDocument() throws IOException {
		contentStream.close();

		document.save(fileName);
		document.close();
	}

	public PDPageContentStream getContentStream() {
		return contentStream;
	}

	public PDPage getPage() {
		return page;
	}

	public float getPageHeight() {
		return bBox.getHeight();
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

	private void startDocument(PDRectangle mediaBox) throws IOException, TransformerException {
		document = new PDDocument();
		createMetaDate();
		includeColorProfile();
		page = new PDPage(mediaBox);
		document.addPage(page);
		bBox = page.getBBox();
		contentStream = new PDPageContentStream(document, page);
	}

}
