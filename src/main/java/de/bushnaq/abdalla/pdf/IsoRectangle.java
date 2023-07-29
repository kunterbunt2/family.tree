package de.bushnaq.abdalla.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class IsoRectangle extends PDRectangle {
    private static final float POINTS_PER_INCH = 72;

    /**
     * user space units per millimeter
     */
    private static final float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
    public static final PDRectangle A0_LANDSCAPE = new PDRectangle(1189 * POINTS_PER_MM, 841 * POINTS_PER_MM);

    /**
     * A rectangle the size of A1 Paper.
     */
    public static final PDRectangle A1_LANDSCAPE = new PDRectangle(841 * POINTS_PER_MM, 594 * POINTS_PER_MM);

    /**
     * A rectangle the size of A2 Paper.
     */
    public static final PDRectangle A2_LANDSCAPE = new PDRectangle(594 * POINTS_PER_MM, 420 * POINTS_PER_MM);

    /**
     * A rectangle the size of A3 Paper.
     */
    public static final PDRectangle A3_LANDSCAPE = new PDRectangle(420 * POINTS_PER_MM, 297 * POINTS_PER_MM);

    /**
     * A rectangle the size of A4 Paper.
     */
    public static final PDRectangle A4_LANDSCAPE = new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM);

    /**
     * A rectangle the size of A5 Paper.
     */
    public static final PDRectangle A5_LANDSCAPE = new PDRectangle(210 * POINTS_PER_MM, 148 * POINTS_PER_MM);

    /**
     * A rectangle the size of A6 Paper.
     */
    public static final PDRectangle A6_LANDSCAPE = new PDRectangle(148 * POINTS_PER_MM, 105 * POINTS_PER_MM);
}
