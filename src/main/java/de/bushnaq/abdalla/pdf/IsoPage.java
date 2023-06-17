package de.bushnaq.abdalla.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class IsoPage implements Comparable<IsoPage> {

    private final String name;
    private final PDRectangle rect;

    IsoPage(float width, float height, String name) {
        this.rect = new PDRectangle(width, height);
        this.name = name;

    }

    public IsoPage(PDRectangle rect, String name) {
        this.rect = new PDRectangle(rect.getWidth(), rect.getHeight());
        this.name = name;
    }

    @Override
    public int compareTo(IsoPage o) {
        if (this.rect.getWidth() * this.rect.getHeight() < o.rect.getWidth() * o.rect.getHeight()) {
            return -1;
        } else if (this.rect.getWidth() * this.rect.getHeight() == o.rect.getWidth() * o.rect.getHeight()) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getName() {
        return name;
    }

    public PDRectangle getRect() {
        return rect;
    }
}
