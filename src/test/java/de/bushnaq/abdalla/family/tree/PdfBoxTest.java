package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

class PdfBoxTest {
	PdfFont		font	= new PdfFont(PDType1Font.HELVETICA, 12);
	PdfDocument	pdfDocument;

	private void createText(float x, float y, Color color, String string) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
			p.setNonStrokingColor(color);
			p.setFont(font);

			float	stringWidth		= p.getStringWidth(string);
			float	stringHeight	= p.getStringHeight(string);
			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
			p.fillRect(x, y, stringWidth, stringHeight);
			p.fill();

			p.beginText();
			p.newLineAtOffset(x, y);
			p.showText(string);
			p.endText();
		}
	}

	private void drawLine(float x1, float y1, float x2, float y2) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
			p.setLineWidth(1f);
//			p.setLineDashPattern(new float[] { 3 }, 0);
			p.setStrokingColor(new Color(0x2d, 0xb1, 0xff, 32));
			p.drawLine(x1, y1, x2, y2);
			p.stroke();
		}
	}

	private void drawRect(float x, float y, float w, float h) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
			p.setLineWidth(3.1f);
			p.setLineDashPattern(new float[] { 3 }, 0);
			p.setStrokingColor(new Color(0x2d, 0xb1, 0xff, 32));
			p.drawRect(x, y, w, h);
			p.stroke();
		}
	}

	private void fillRect(float x, float y, float w, float h) throws IOException {
		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
			p.setNonStrokingColor(new Color(196, 196, 196));
			p.fillRect(x, y, w, h);
			p.fill();
		}
	}

	@Test
	void testBoxWithBorder() throws IOException, TransformerException {
		pdfDocument = new PdfDocument(PDRectangle.A4);

		fillRect(100, 200, 100, 100);
		drawRect(100, 200, 100, 100);

		pdfDocument.endDocument();
	}

	@Test
	void testDrawLine() throws IOException, TransformerException {
		pdfDocument = new PdfDocument(PDRectangle.A4);

		drawLine(0, 0, 100, 100);
		drawLine(0, 0, 0, 10);
		drawLine(0, 0, 10, 0);

		pdfDocument.endDocument();
	}

	@Test
	void testDrawRect() throws IOException, TransformerException {
		pdfDocument = new PdfDocument(PDRectangle.A4);

		drawRect(0, 0, 100, 100);
		drawRect(0, 0, 10, 10);

		pdfDocument.endDocument();
	}

	@Test
	void testFillRect() throws IOException, TransformerException {
		pdfDocument = new PdfDocument(PDRectangle.A4);

		fillRect(0, 0, 100, 100);
		fillRect(0, 0, 10, 10);

		pdfDocument.endDocument();
	}

	@Test
	void testText() throws IOException, TransformerException {
		pdfDocument = new PdfDocument(PDRectangle.A4);

		createText(0, 0, Color.red, "Hallo 1");
		createText(0, 10, new Color(200, 250, 100, 100), "Hallo 2");
		createText(0, 20, Color.BLUE, "Hallo 3");

		pdfDocument.endDocument();
	}

}
