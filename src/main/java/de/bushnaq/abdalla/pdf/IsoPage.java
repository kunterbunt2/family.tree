package de.bushnaq.abdalla.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class IsoPage {

	private final String		name;
	private final PDRectangle	rect;

	IsoPage(float width, float height, String name) {
		this.rect = new PDRectangle(width, height);
		this.name = name;

	}

	IsoPage(PDRectangle rect, String name) {
		this.rect = new PDRectangle(rect.getWidth(), rect.getHeight());
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public PDRectangle getRect() {
		return rect;
	}

}
