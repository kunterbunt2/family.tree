package de.bushnaq.abdalla.pdf;

import java.awt.Color;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class CloseableGraphicsState implements Closeable {

	private PDPageContentStream		contentStream;
	private PDFont					font			= PDType1Font.HELVETICA;
	Set<PdfFont>					fontSet			= new HashSet<>();
	private float					fontSize		= 12;
	private PDExtendedGraphicsState	graphicsState	= new PDExtendedGraphicsState();
	private float					lineWidth		= 1f;
	private Color					nonStrokingColor;
	private PDPage					page;
	private PDRectangle				pageBBox;
	private float					pageHeight;
	private float					pageWidth;
	private PdfDocument				pdfDocument;

	private Color					strokingColor;

	public CloseableGraphicsState(PdfDocument pdfDocument) throws IOException {
		this.pdfDocument = pdfDocument;
		this.page = pdfDocument.getPage();
		this.contentStream = pdfDocument.getContentStream();
		pageBBox = page.getBBox();
		pageWidth = pageBBox.getWidth();
		pageHeight = pageBBox.getHeight();
		contentStream.saveGraphicsState();
	}

	public void beginText() throws IOException {
		contentStream.beginText();
	}

	@Override
	public void close() throws IOException {
		contentStream.restoreGraphicsState();
	}

	public void drawLine(float x1, float y1, float x2, float y2) throws IOException {
		contentStream.moveTo(x1, pageHeight - y1);
		contentStream.lineTo(x2, pageHeight - y2);
//		contentStream.addRect(x1, pageHeight - y1 - (y2 - y1 + 1), x2 - x1 + 1, y2 - y1 + 1);
	}

	public void drawRect(float x, float y, float width, float height) throws IOException {
		contentStream.addRect(x + lineWidth / 2, pageHeight - y - height + lineWidth / 2, width - lineWidth, (height - lineWidth));
	}

	public void endText() throws IOException {
		contentStream.endText();
	}

	public void fill() throws IOException {
		contentStream.setGraphicsStateParameters(graphicsState);
		contentStream.fill();
	}

	public void fillRect(float x, float y, float width, float height) throws IOException {
		contentStream.addRect(x, pageHeight - y - height, width, height);
	}

	public PDFont getFont() {
		return font;
	}

	public PdfFont getFontFittingWidth(PdfFont font, Integer boxWidth, String text) throws IOException {

//			graphics.setFont(nameFont);
//			FontRenderContext	frc				= graphics.getFontRenderContext();
//			Font				font			= graphics.getFont();
//			Rectangle2D			stringBounds	= font.getStringBounds(text, frc);
		float	size	= font.getSize();
		float	width	= getStringWidth(font, text);
		if (width > boxWidth - 4) {
			// reduce font size
			do {
				size -= 1f;
				font = new PdfFont(font.getFont(), size);
//				graphics.setFont(deriveFont);
//				frc = graphics.getFontRenderContext();
				width = getStringWidth(font, text);
			} while (width > boxWidth - 4);
		}
		return font;
	}

	public float getFontSize() {
		return fontSize;
	}

	public Color getNonStrokingColor() {
		return nonStrokingColor;
	}

	public float getStringHeight(String string) {
		return (-getFont().getFontDescriptor().getDescent() + getFont().getFontDescriptor().getCapHeight()) / 1000 * getFontSize();
	}

	public float getStringWidth(PdfFont font, String string) throws IOException {
		try {
			return font.getFont().getStringWidth(string) / 1000 * font.getSize();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public float getStringWidth(String string) throws IOException {
		return getFont().getStringWidth(string) / 1000 * getFontSize();
	}

	public Color getStrokingColor() {
		return strokingColor;
	}

	public void newLineAtOffset(float tx, float ty) throws IOException {
		float stringHeight = getFont().getFontDescriptor().getCapHeight() / 1000 * getFontSize();
		contentStream.newLineAtOffset(tx, pageHeight - ty);
	}

	public void setFont(PdfFont font) throws IOException {
		setFont(font.getFont(), font.getSize());
	}

	public void setFont(PDFont font, float fontSize) throws IOException {
		this.font = font;
		this.fontSize = fontSize;
		contentStream.setFont(font, fontSize);
	}

	public void setLineDashPattern(float[] pattern, float phase) throws IOException {
		contentStream.setLineDashPattern(pattern, phase);

	}

	public void setLineWidth(float lineWidth) throws IOException {
		this.lineWidth = lineWidth;
		contentStream.setLineWidth(lineWidth);
	}

	public void setNonStrokingColor(Color nonStrokingColor) throws IOException {
		this.nonStrokingColor = nonStrokingColor;
		contentStream.setNonStrokingColor(nonStrokingColor);
		if (nonStrokingColor.getAlpha() != 0) {
			graphicsState.setNonStrokingAlphaConstant(nonStrokingColor.getAlpha() / 255.0f);
		}
	}

	public void setStrokingColor(Color strokingColor) throws IOException {
		this.strokingColor = strokingColor;
		contentStream.setStrokingColor(strokingColor);
		if (strokingColor.getAlpha() != 0) {
			graphicsState.setStrokingAlphaConstant(strokingColor.getAlpha() / 255.0f);
		}
	}

	public void showText(String text) throws IOException {
		contentStream.showText(text);
	}

	public void stroke() throws IOException {
		contentStream.setGraphicsStateParameters(graphicsState);
		contentStream.stroke();
	}

}