package de.bushnaq.abdalla.family.person;

import java.awt.Color;
import java.io.IOException;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

public abstract class DrawablePerson extends Person {
	private static final float	FAT_LINE_STROKE_WIDTH		= 3.1f;
	private static final float	MEDIUM_LINE_STROKE_WIDTH	= 2.1f;
	private Color				backgroundColor;
	private Color				borderColor					= new Color(0, 0, 0, 64);
	private Color				connectorColor				= Color.gray;
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

	private void drawBorder(CloseableGraphicsState p, float x, float y, String text, Context context) throws IOException {
		if (context.getParameterOptions().isDrawTextBorders()) {
			p.setStrokingColor(new Color(p.getNonStrokingColor().getRed(), p.getNonStrokingColor().getGreen(), p.getNonStrokingColor().getBlue(), 100));
			float	h	= p.getStringHeight();
			float	w	= p.getStringWidth(text);
			p.drawRect(x, y - h, w, h);
			p.stroke();
		}
	}

	private void drawBox(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException {
		float	width	= context.generationToMaxWidthMap.get(generation);
		float	x1		= x * (width + getXSpace(context));
		float	y1		= y * (getHeight(context) + Person.getYSpace(context));

		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
			p.setNonStrokingColor(backgroundColor);
			p.fillRect(x1, y1, width, getHeight(context));
			p.fill();
		}
		if (isSpouse() && !isMember(context)) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(spouseBorderColor);
				p.setLineWidth(getFatLineWidth(context));
				p.setLineDashPattern(new float[] { 3 }, 0);
				p.drawRect(x1, y1, width, getHeight(context));
				p.stroke();
			}
		} else {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(borderColor);
				p.drawRect(x1, y1, width, getHeight(context));
				p.stroke();
			}
		}
		float firstNameHeight;
		{
			// first name
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(textColor);
				String text = getFirstNameAsString(context);
				if (isFirstNameOl(context))
					p.setFont(p.getFontFittingWidth(nameOLFont, width, text));
				else
					p.setFont(p.getFontFittingWidth(nameFont, width, text));
				float stringWidth = p.getStringWidth(text);
				firstNameHeight = p.getStringHeight();
				float	w	= (int) stringWidth;
				float	x2	= x1 + (width) / 2 - w / 2;
				float	y2	= (int) (y1 + firstNameHeight);
				if (context.getParameterOptions().isCompact())
					y2 -= 2;
				if (text.contains("?"))
					p.setNonStrokingColor(Color.red);
				drawBorder(p, x2, y2, text, context);
				p.beginText();
				p.newLineAtOffset(x2, y2);
				p.showText(text);
				p.endText();
			}
		}
		// last name
		{
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(textColor);
				String text = getLastNameAsString(context);
				if (isLastNameOl(context))
					p.setFont(p.getFontFittingWidth(nameOLFont, width, text));
				else
					p.setFont(p.getFontFittingWidth(nameFont, width, text));
				float	stringWidth		= p.getStringWidth(text);
				float	lastNameHeight	= p.getStringHeight();
				float	w				= (int) stringWidth;
				float	x2				= x1 + (width) / 2 - w / 2;
				float	y2				= (int) (y1 + firstNameHeight + lastNameHeight);
				if (context.getParameterOptions().isCompact())
					y2 -= 3;
				if (text.contains("?"))
					p.setNonStrokingColor(Color.red);
				drawBorder(p, x2, y2, text, context);
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
				p.setFont(nameFont);
				if (this instanceof FemaleClone || this instanceof MaleClone) {
					String	text	= "*";
					float	x2		= (int) (x1 + width - p.getStringWidth(text));
					float	y2		= (int) (y1 + p.getStringHeight());
					drawBorder(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
		if (!context.getParameterOptions().isCompact()) {
			// ID
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(textColor);
				p.setFont(nameFont);
				{
					String	text	= "" + getId();
					float	x2		= x1 + 4;
					float	y2		= y1 + getHeight(context) /*- p.getStringHeight(text)*/ - 4;
					drawBorder(p, x2, y2, text, context);
					p.beginText();
					p.newLineAtOffset(x2, y2);
					p.showText(text);
					p.endText();
				}
			}
		}
		if (context.getParameterOptions().isCoordinates()) {
//			 Coordinates
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setNonStrokingColor(Color.lightGray);
				p.setFont(dateFont);
				{
					String	text	= String.format("%d,%d", (int) x, (int) y);
					float	x2		= (int) (x1 + width - p.getStringWidth(text) - 2);
					float	y2		= y1 + getHeight(context)/* - p.getStringHeight(text) */ - 4;
					drawBorder(p, x2, y2, text, context);
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
					float	x2		= x1 + (width) / 2 - w / 2;
					float	y2		= y1 + getHeight(context) - h - 4;
					drawBorder(p, x2, y2, text, context);
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
//				float	h		= p.getStringHeight(text);
				float	x2		= x1 + (width) / 2 - w / 2;
				float	y2		= y1 + getHeight(context) /*- h*/ - 4;
				drawBorder(p, x2, y2, text, context);
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
//						float	w		= p.getStringWidth(text);
						float	h		= p.getStringHeight();
						float	x2		= x1 + 4;
						float	y2		= (int) (y1 + getHeight(context) + h - 2);
						drawBorder(p, x2, y2, text, context);
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
		float	x1	= xIndexToPixel(context, x);
		float	y1	= yIndexToPixel(context, y);

		// child Connector vertical
		if (isMember(context) && !isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context) / 2, y1 - getYSpace(context) / 2, x1 + getWidth(context) / 2, y1);
				p.stroke();
			}
		}
		// child Connector horizontal
		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				float	cx1	= x1 + getWidth(context) / 2;
				float	cx2	= xIndexToPixel(context, getChildrenList().last().x) + getWidth(context) / 2;
				p.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}
		// spouse connector to children
		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context), x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}

		// sexual relation connector from person to his/her spouse
		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineDashPattern(new float[] { 1 }, 0);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context), x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
				for (Person spouse : getSpouseList()) {
					float sx = xIndexToPixel(context, spouse.x);
//					float	sy	= yIndexToPixel(context, spouse.y);
					p.drawLine(sx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, sx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context));
				}
				float lsx = xIndexToPixel(context, getSpouseList().last().x);
				p.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, lsx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
		}
		// parent connector to children
		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				float	cx1	= y1 + getWidth(context) / 2;
				float	cx2	= xIndexToPixel(context, getChildrenList().last().x) + getWidth(context) / 2;
				p.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
				p.stroke();
			}
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
		float	x1	= xIndexToPixel(context, x);
		float	y1	= yIndexToPixel(context, y);

		// child Connector horizontal
		if (isMember(context) && !isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 - getXSpace(context) / 2, y1 + getHeight(context) / 2, x1, y1 + getHeight(context) / 2);
				p.stroke();
			}
		}
		// child Connector vertical

		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				float	cy1	= y1 + getHeight(context) / 2;
				float	cy2	= yIndexToPixel(context, getChildrenList().last().y) + getHeight(context) / 2;
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
				p.stroke();
			}
		}

		// spouse connector to children

		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				p.stroke();
			}
		}

		// sexual relation connector from person to his/her spouse
		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineDashPattern(new float[] { 3 }, 0);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context), y1 + getHeight(context) / 2);
				for (Person spouse : getSpouseList()) {
					float	sx	= xIndexToPixel(context, spouse.x);
					float	sy	= yIndexToPixel(context, spouse.y);
					p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, sy + getHeight(context) / 2, sx, sy + getHeight(context) / 2);
				}
				float lsy = yIndexToPixel(context, getSpouseList().last().y);
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, lsy + getHeight(context) / 2);
				p.stroke();
			}
		}

		// spouse connector to children
		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument, pageIndex)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(getMediumLineWidth(context));
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				float	cy1	= y1 + getHeight(context) / 2;
				float	cy2	= yIndexToPixel(context, getChildrenList().last().y) + getHeight(context) / 2;
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
				p.stroke();
			}
		}

	}

	protected float getFatLineWidth(Context context) {
		return FAT_LINE_STROKE_WIDTH * context.getParameterOptions().getZoom();
	}

	protected float getMediumLineWidth(Context context) {
		return MEDIUM_LINE_STROKE_WIDTH * context.getParameterOptions().getZoom();
	}

	@Override
	public String toString(Context context) {
		return String.format("[%d] %s (%s) x=%d y=%d", getId(), getName(context), getLivedString(), x, y);
	}

	private float xIndexToPixel(Context context, float x) {
		return x * (getWidth(context) + getXSpace(context));
	}

	private float yIndexToPixel(Context context, float y) {
		return y * (getHeight(context) + Person.getYSpace(context));
	}
}
