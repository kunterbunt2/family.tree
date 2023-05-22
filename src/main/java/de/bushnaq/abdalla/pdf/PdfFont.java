package de.bushnaq.abdalla.pdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

public class PdfFont {
	PDFont	font;

	float	size;

	public PdfFont(PDFont pdFont, float size) {
		this.font = pdFont;
		this.size = size;
	}

	public PDFont getFont() {
		return font;
	}

	public float getSize() {
		return size;
	}
}
