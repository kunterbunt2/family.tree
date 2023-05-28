package de.bushnaq.abdalla.family.person;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

public abstract class DrawablePerson extends Person {
	private static final float	FAT_LINE_STROKE_WIDTH		= 2f;
	private static final float	MEDIUM_LINE_STROKE_WIDTH	= 1f;
	private Color				backgroundColor;
	private Color				borderColor					= new Color(0, 0, 0, 64);
	private Color				connectorColor				= Color.gray;
	private Color[]				generationColors			= { Color.red, Color.blue, Color.green, Color.orange, Color.gray };
	private Color				spouseBorderColor;

	private Color				textColor					= new Color(0, 0, 0);

	public DrawablePerson(PersonList personList, DrawablePerson person, Color backgroundColor) {
		super(personList, person);
		this.backgroundColor = backgroundColor;
		this.spouseBorderColor = new Color(backgroundColor.getRGB());
	}

	public DrawablePerson(PersonList personList, Integer id, Color backgroundColor) {
		super(personList, id);
		this.backgroundColor = backgroundColor;
		this.spouseBorderColor = new Color(backgroundColor.getRGB());
	}

	private void drawBox(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException {
		float	x1	= xIndexToCoordinate(context, x);	// x * (width + getXSpace(context));
		float	y1	= yIndexToCoordinate(context, y);	// y * (getHeight(context) + Person.getYSpace(context));

		if (getGeneration() != null && getGeneration() > 0 && !isSpouse() && hasChildren()) {
			// family tree background color
			Rect	treeRect	= getTreeRect();
			float	tx1			= xIndexToCoordinate(context, treeRect.x1);
			float	ty1			= yIndexToCoordinate(context, treeRect.y1);
			float	tx2			= xIndexToCoordinate(context, treeRect.x2);
			float	ty2			= yIndexToCoordinate(context, treeRect.y2);

			if (context.getParameterOptions().isColorTrees()) {
				try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
					p.setNonStrokingColor(getGenrationColor(getGeneration()), 0.05f);
					p.fillRect(tx1, ty1, tx2 - tx1 + getWidth(context), ty2 - ty1 + getHeight(context));
					p.fill();
				}
			}
		}

//		if (!context.getParameterOptions().isCompact())
		{
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				// interior
				p.setNonStrokingColor(backgroundColor);
				p.fillRect(x1, y1, getWidth(context), getHeight(context));
				p.fill();
			}
			if (isSpouse() && !isMember(context)) {
				// clone border
				try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
					p.setStrokingColor(borderColor);
					p.setLineWidth(getBorder(context));
					p.setLineDashPattern(new float[] { 1 }, 0);
					p.drawRect(x1, y1, getWidth(context), getHeight(context));
					p.stroke();
				}
			} else {
				// border
				try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
					p.setStrokingColor(borderColor);
					p.setLineWidth(getBorder(context));
					p.setLineDashPattern(new float[] {}, 0);
					p.drawRect(x1, y1, getWidth(context), getHeight(context));
					p.stroke();
				}
			}
		}
		float firstNameHeight;
		{
			// first name
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				String text = getFirstNameAsString(context);
				if (isFirstNameOl(context))
					p.setFont(p.getFontFittingWidth(nameOLFont, getWidth(context), text));
				else
					p.setFont(p.getFontFittingWidth(nameFont, getWidth(context), text));
				float stringWidth = p.getStringWidth(text);
				firstNameHeight = p.getStringHeight();
				float	w	= stringWidth;
				float	x2	= x1 + (getWidth(context)) / 2 - w / 2;
				float	y2	= y1 + getBorder(context) + firstNameHeight;

				p.setNonStrokingColor(textColor);
				if (text.contains("?"))
					p.setNonStrokingColor(Color.red);
				drawTextMetric(p, x2, y2, text, context);
				p.beginText();
				p.newLineAtOffset(x2, y2);
				p.showText(text);
				p.endText();
			}
		}
		// last name
		{
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				String text = getLastNameAsString(context);
				if (isLastNameOl(context))
					p.setFont(p.getFontFittingWidth(nameOLFont, getWidth(context), text));
				else
					p.setFont(p.getFontFittingWidth(nameFont, getWidth(context), text));
				float	stringWidth		= p.getStringWidth(text);
				float	lastNameHeight	= p.getStringHeight();
				float	w				= stringWidth;
				float	x2				= x1 + (getWidth(context)) / 2 - w / 2;
				float	y2				= y1 + getBorder(context) + firstNameHeight + lastNameHeight;

				p.setNonStrokingColor(textColor);
				if (text.contains("?"))
					p.setNonStrokingColor(Color.red);
				drawTextMetric(p, x2, y2, text, context);
				p.beginText();
				p.newLineAtOffset(x2, y2);
				p.showText(text);
				p.endText();
			}
		}

		if (!context.getParameterOptions().isCompact()) {
			// clone
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(textColor);
				if (context.getParameterOptions().isCompact()) {
					p.setFont(dateFont);
				} else {
					p.setFont(nameFont);
				}
				if (this instanceof FemaleClone || this instanceof MaleClone) {
					String	text	= "*";
					float	x2		= x1 + getWidth(context) - p.getStringWidth(text) - getBorder(context) - getMargine(context);
					float	y2		= y1 + getBorder(context) + p.getStringHeight();
					drawTextMetric(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
//		if (!context.getParameterOptions().isCompact())
		{
			// ID
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(textColor);
				float y2;
				if (context.getParameterOptions().isCompact()) {
					p.setFont(dateFont);
				} else {
					p.setFont(nameFont);
				}
				{
					String	text	= "" + getId();
					float	x2		= x1 + getBorder(context) + getMargine(context);
					y2 = y1 + getHeight(context) - getBorder(context);
					drawTextMetric(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
//		if (!context.getParameterOptions().isCompact())
		{
			// Generation
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				if (getGeneration() != null) {
					p.setNonStrokingColor(textColor);
					if (context.getParameterOptions().isCompact()) {
						p.setFont(dateFont);
//						y2 = y1 + p.getStringHeight() + getBorder(context);
					} else {
						p.setFont(nameFont);
					}
					String	text	= "G" + getGeneration();
					float	x2		= x1 + getBorder(context) + getMargine(context);
					float	y2		= y1 + getBorder(context) + p.getStringHeight();
					drawTextMetric(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
		if (context.getParameterOptions().isCoordinates()) {
			// Coordinates
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(Color.darkGray);
				p.setFont(dateFont);
				{
					String	text	= String.format("%d,%d", (int) x, (int) y);
					float	x2		= x1 + getWidth(context) - p.getStringWidth(text) - getMargine(context) - getBorder(context);
					float	y2		= y1 + getHeight(context) - getBorder(context);
					drawTextMetric(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
		{
			// born
			if (!context.getParameterOptions().isCompact()) {
				try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
					p.setNonStrokingColor(textColor);
					p.setFont(dateFont);
					String	text	= getBornString();
					float	w		= p.getStringWidth(text);
					float	h		= p.getStringHeight();
					float	x2		= x1 + (getWidth(context)) / 2 - w / 2;
					float	y2		= y1 + getHeight(context) - h - getBorder(context);
					drawTextMetric(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
		if (!context.getParameterOptions().isCompact()) {
			// died
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(textColor);
				p.setFont(dateFont);
				String	text	= getDiedString();
				float	w		= p.getStringWidth(text);
				float	x2		= x1 + (getWidth(context)) / 2 - w / 2;
				float	y2		= y1 + getHeight(context) - getBorder(context);
				drawTextMetric(p, x2, y2, text, context);
				p.beginText();
				p.newLineAtOffset(x2, y2);
				p.showText(text);
				p.endText();
			}
		}

		{
			// errors
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(Color.red);
				p.setFont(dateFont);
				{
					if (errors.size() != 0) {
						StringBuffer sb = new StringBuffer();
						for (String s : errors) {
							sb.append(s);
							sb.append(",");
						}
						String	text	= sb.toString();
						float	h		= p.getStringHeight();
						float	x2		= x1;
						float	y2		= y1 + getHeight(context) + h;
						drawTextMetric(p, x2, y2, text, context);
						p.beginText();
						p.newLineAtOffset(x2, y2);
						p.showText(text);
						p.endText();
					}
				}
			}
		}
	}

	@Override
	public void drawHorizontal(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException {
		if (isVisible()) {
			drawBox(context, pdfDocument, nameFont, nameOLFont, dateFont);
			drawHorizontalConnectors(context, pdfDocument);
		}
	}

	private void drawHorizontalConnectors(Context context, PdfDocument pdfDocument) throws IOException {
		float	x1	= xIndexToCoordinate(context, x);
		float	y1	= yIndexToCoordinate(context, y);

		// child Connector vertical to horizontal connector from parent spouse
		if (hasParents()/* isMember(context) && !isSpouse() */) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				Person sp = getSpouseParent();
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(x1 + getWidth(context) / 2, yIndexToCoordinate(context, sp.y) + getHeight(context) + getYSpace(context) / 2, x1 + getWidth(context) / 2, y1);
				p.stroke();
			}
		}
		// child Connector horizontal
		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				float	cx1	= x1 + getWidth(context) / 2;
				float	cx2	= xIndexToCoordinate(context, getChildrenList().last().x) + getWidth(context) / 2;
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}
		// spouse connector to children
		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context), x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}

		// sexual relation connector from person to his/her spouse
		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineDashPattern(new float[] { 1 }, 0);
				p.setLineWidth(getConnectorWidth(context));
				p.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context), x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
				for (Person spouse : getSpouseList()) {
					float sx = xIndexToCoordinate(context, spouse.x);
					p.drawLine(sx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, sx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context));
				}
				float lsx = xIndexToCoordinate(context, getSpouseList().last().x);
				p.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, lsx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}
		// parent connector to children
		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				float	cx1	= y1 + getWidth(context) / 2;
				float	cx2	= xIndexToCoordinate(context, getChildrenList().last().x) + getWidth(context) / 2;
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}
	}

	private void drawTextMetric(CloseableGraphicsState p, float x, float y, String text, Context context) throws IOException {
		if (context.getParameterOptions().isDrawTextMetric()) {
			Color				color			= p.getNonStrokingColor();
			PDFont				font			= p.getFont();
			float				fontSize		= p.getFontSize();
			float				stringWidth		= p.getStringWidth(text);
			float				stringHeight	= p.getStringHeight();
			PDFontDescriptor	fd				= font.getFontDescriptor();
			float				ascent			= fd.getAscent() * fontSize / 1000;
			float				capHeight		= fd.getCapHeight() * fontSize / 1000;
			float				descent			= -fd.getDescent() * fontSize / 1000;

			p.setNonStrokingColor(color, 0.1f);
			p.fillRect(x, y - stringHeight, stringWidth, ascent - capHeight);
			p.fill();

			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
			p.fillRect(x, y - capHeight - descent, stringWidth, capHeight);
			p.fill();

			p.setNonStrokingColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
			p.fillRect(x, y - descent, stringWidth, descent);
			p.fill();
			p.setNonStrokingColor(color);// set color back for text
		}
	}

	@Override
	public void drawVertical(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException {
		if (isVisible()) {
			drawBox(context, pdfDocument, nameFont, nameOLFont, dateFont);
			drawVerticalConnectors(context, pdfDocument);
		}
	}

	private void drawVerticalConnectors(Context context, PdfDocument pdfDocument) throws IOException {
		float	x1	= xIndexToCoordinate(context, x);
		float	y1	= yIndexToCoordinate(context, y);

		// child Connector horizontal to parent direction
		if (isMember(context) && !isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(x1 - getXSpace(context) / 2, y1 + getHeight(context) / 2, x1, y1 + getHeight(context) / 2);
				p.stroke();
			}
		}

		// child Connector vertical
		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				float	cy1	= y1 + getHeight(context) / 2;
				float	cy2	= yIndexToCoordinate(context, getChildrenList().last().y) + getHeight(context) / 2;
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
				p.stroke();
			}
		}

		// spouse connector to children
		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				p.stroke();
			}
		}

		// sexual relation connector from person to his/her spouse
		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineDashPattern(new float[] { 3 }, 0);
				p.setLineWidth(getConnectorWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context), y1 + getHeight(context) / 2);
				for (Person spouse : getSpouseList()) {
					float	sx	= xIndexToCoordinate(context, spouse.x);
					float	sy	= yIndexToCoordinate(context, spouse.y);
					p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, sy + getHeight(context) / 2, sx, sy + getHeight(context) / 2);
				}
				float lsy = yIndexToCoordinate(context, getSpouseList().last().y);
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, lsy + getHeight(context) / 2);
				p.stroke();
			}
		}

		// spouse connector to children
		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getConnectorWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				float	cy1	= y1 + getHeight(context) / 2;
				float	cy2	= yIndexToCoordinate(context, getChildrenList().last().y) + getHeight(context) / 2;
				p.setLineDashPattern(new float[] {}, 0);
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
				p.stroke();
			}
		}

	}

	protected float getConnectorWidth(Context context) {
		return MEDIUM_LINE_STROKE_WIDTH * context.getParameterOptions().getZoom();
	}

	private Color getGenrationColor(int generation) {
		return generationColors[generation % generationColors.length];
	}

	protected float getSpecialBorderWidth(Context context) {
		return FAT_LINE_STROKE_WIDTH * context.getParameterOptions().getZoom();
	}

	@Override
	public String toString(Context context) {
		return String.format("[%d] %s (%s) x=%d y=%d", getId(), getName(context), getLivedString(), x, y);
	}

	private float xIndexToCoordinate(Context context, float x) {
		return x * (getWidth(context) + getXSpace(context));
	}

	private float yIndexToCoordinate(Context context, float y) {
		return y * (getHeight(context) + Person.getYSpace(context));
	}

}
