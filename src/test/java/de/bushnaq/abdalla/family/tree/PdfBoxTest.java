package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

class PdfBoxTest {
//	PdfFont		font	= new PdfFont(PDType1Font.HELVETICA, 12);
	PdfDocument pdfDocument;

	private void createText(float x, float y, Color color, String string) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {

			PdfFont font = new PdfFont(pdfDocument.loadFont("NotoSans-Regular.ttf"), 12);

			p.setFont(font);

			float	stringWidth		= p.getStringWidth(string);
			float	stringHeight	= p.getStringHeight();
			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
			p.fillRect(x, y, stringWidth, stringHeight);
			p.fill();
		}

		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			PdfFont font = new PdfFont(pdfDocument.loadFont("NotoSans-Regular.ttf"), 12);
			p.setFont(font);
			float stringHeight = p.getStringHeight();
			p.beginText();
			p.setNonStrokingColor(color);
			p.newLineAtOffset(x, y + stringHeight);
			p.showText(string);
			p.endText();
		}
	}

	private void createTextSizes(float x, float y, String fontName, Color color, String string) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			p.setNonStrokingColor(color);
			PdfFont font = new PdfFont(pdfDocument.loadFont(fontName), 12);
			p.setFont(font);

			float				stringWidth		= p.getStringWidth(string);
			float				stringHeight	= p.getStringHeight();

			PDFontDescriptor	fd				= font.getFont().getFontDescriptor();
			float				ascent			= fd.getAscent() * font.getSize() / 1000;
			float				capHeight		= fd.getCapHeight() * font.getSize() / 1000;
			float				descent			= -fd.getDescent() * font.getSize() / 1000;
//			float				baseline		= y - descent;

			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			p.fillRect(x + 1, y - (ascent - capHeight), stringWidth, ascent - capHeight);
			p.fill();
		}
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			p.setNonStrokingColor(color);
			PdfFont font = new PdfFont(pdfDocument.loadFont(fontName), 12);
			p.setFont(font);

			float				stringWidth		= p.getStringWidth(string);
			float				stringHeight	= p.getStringHeight();

			PDFontDescriptor	fd				= font.getFont().getFontDescriptor();
			float				ascent			= fd.getAscent() * font.getSize() / 1000;
			float				capHeight		= fd.getCapHeight() * font.getSize() / 1000;
			float				descent			= -fd.getDescent() * font.getSize() / 1000;
//			float				baseline		= ydescent;

			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
			p.fillRect(x, y, stringWidth, capHeight);
			p.fill();
		}
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			p.setNonStrokingColor(color);
			PdfFont font = new PdfFont(pdfDocument.loadFont(fontName), 12);
			p.setFont(font);

			float				stringWidth		= p.getStringWidth(string);
			float				stringHeight	= p.getStringHeight();

			PDFontDescriptor	fd				= font.getFont().getFontDescriptor();
			float				ascent			= fd.getAscent() * font.getSize() / 1000;
			float				capHeight		= fd.getCapHeight() * font.getSize() / 1000;
			float				descent			= -fd.getDescent() * font.getSize() / 1000;
			float				baseline		= y + capHeight;

			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
			p.fillRect(x + 1, baseline, stringWidth, descent);
			p.fill();
		}

		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			PdfFont font = new PdfFont(pdfDocument.loadFont(fontName), 12);
			p.setFont(font);
			PDFontDescriptor	fd				= font.getFont().getFontDescriptor();
			float				ascent			= fd.getAscent() * font.getSize() / 1000;
			float				capHeight		= fd.getCapHeight() * font.getSize() / 1000;
			float				descent			= -fd.getDescent() * font.getSize() / 1000;
			float				baseline		= y - descent;
			float				stringHeight	= p.getStringHeight();
			p.beginText();
			p.newLineAtOffset(x, y + capHeight);
			p.showText(string);
			p.endText();
		}
	}

	private void drawLine(float x1, float y1, float x2, float y2) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			p.setLineWidth(1f);
//			p.setLineDashPattern(new float[] { 3 }, 0);
			p.setStrokingColor(new Color(0x2d, 0xb1, 0xff, 32));
			p.drawLine(x1, y1, x2, y2);
			p.stroke();
		}
	}

	private void drawRect(float x, float y, float w, float h) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			p.setLineWidth(3.1f);
			p.setLineDashPattern(new float[] { 3 }, 0);
			p.setStrokingColor(new Color(0x2d, 0xb1, 0xff, 32));
			p.drawRect(x, y, w, h);
			p.stroke();
		}
	}

	private void fillRect(float x, float y, float w, float h) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, 0)) {
			p.setNonStrokingColor(new Color(196, 196, 196));
			p.fillRect(x, y, w, h);
			p.fill();
		}
	}

	@Test
	void testBoxWithBorder(TestInfo testInfo) throws IOException, TransformerException {
		pdfDocument = new PdfDocument(testInfo.getDisplayName() + ".pdf", PDRectangle.A4);

		fillRect(100, 200, 100, 100);
		drawRect(100, 200, 100, 100);

		pdfDocument.endDocument();
	}

	@Test
	void testDrawLine(TestInfo testInfo) throws IOException, TransformerException {
		pdfDocument = new PdfDocument(testInfo.getDisplayName() + ".pdf", PDRectangle.A4);

		drawLine(0, 0, 100, 100);
		drawLine(0, 0, 0, 10);
		drawLine(0, 0, 10, 0);

		pdfDocument.endDocument();
	}

	@Test
	void testDrawRect(TestInfo testInfo) throws IOException, TransformerException {
		pdfDocument = new PdfDocument(testInfo.getDisplayName() + ".pdf", PDRectangle.A4);

		drawRect(0, 0, 100, 100);
		drawRect(0, 0, 10, 10);

		pdfDocument.endDocument();
	}

	@Test
	void testFillRect(TestInfo testInfo) throws IOException, TransformerException {
		pdfDocument = new PdfDocument(testInfo.getDisplayName() + ".pdf", PDRectangle.A4);

		fillRect(0, 0, 100, 100);
		fillRect(0, 0, 10, 10);

		pdfDocument.endDocument();
	}

	@Test
	void testText(TestInfo testInfo) throws IOException, TransformerException {
		pdfDocument = new PdfDocument(testInfo.getDisplayName() + ".pdf", PDRectangle.A4);

		createText(20, 20, Color.red, "Hallo 1");
		createText(20, 40, new Color(200, 250, 100, 100), "Hallo 2");
		createText(20, 60, Color.BLUE, "Hallo 3");

		pdfDocument.endDocument();
	}

	@Test
	void testTextSizes(TestInfo testInfo) throws IOException, TransformerException {
		pdfDocument = new PdfDocument(testInfo.getDisplayName() + ".pdf", PDRectangle.A4);

		createTextSizes(20, 20, "NotoSans-Regular.ttf", Color.black, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		createTextSizes(20, 40, "Amiri-Regular.ttf", Color.black, "هجايليش بشناق");

		pdfDocument.endDocument();
	}

}
