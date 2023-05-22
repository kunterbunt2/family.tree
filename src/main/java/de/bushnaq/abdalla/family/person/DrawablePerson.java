package de.bushnaq.abdalla.family.person;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.pdf.CloseableGraphicsState;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.pdf.PdfFont;

public abstract class DrawablePerson extends Person {
	protected static final float	FAT_LINE_STROKE_WIDTH		= 3.1f;
	protected static final float	MEDIUM_LINE_STROKE_WIDTH	= 2.1f;
	private Color					backgroundColor;
	private Color					borderColor					= new Color(0, 0, 0, 64);
	private Color					connectorColor				= Color.gray;
	private Color					spouseBorderColor;
	private Color					textColor					= new Color(0, 0, 0);

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
			float	h	= p.getStringHeight(text);
			float	w	= p.getStringWidth(text);
			p.drawRect(x, y - h, w, h);
			p.stroke();
		}
	}

	private void drawBorder(Graphics2D graphics, int x, int y, String string) {
		FontRenderContext	frc				= graphics.getFontRenderContext();
		Font				font			= graphics.getFont();
		Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
		graphics.setColor(spouseBorderColor);
		int	w	= (int) stringBounds.getWidth() - 1;
		int	h	= (int) stringBounds.getHeight() - 1;
		graphics.drawRect(x, y - h, w, h);
	}

	private void drawBox(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		Integer	width	= context.generationToMaxWidthMap.get(generation);
		int		x1		= x * (width + getXSpace(context));
		int		y1		= y * (getHeight(context) + Person.getYSpace(context));
		graphics.setColor(backgroundColor);
		graphics.fillRect(x1, y1, width, getHeight(context));
		if (isSpouse() && !isMember(context)) {
			Stroke stroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(spouseBorderColor);
			graphics.drawRect(x1, y1, width - 1, getHeight(context) - 1);
			graphics.setStroke(stroke);
		} else {
			graphics.setColor(borderColor);
			graphics.drawRect(x1, y1, width - 1, getHeight(context) - 1);
		}
		{
			// first name
			{
				graphics.setColor(textColor);
				graphics.setFont(nameFont);
				String				string	= getFirstNameAsString(context);
				FontRenderContext	ofrc	= graphics.getFontRenderContext();
				Font				ofont	= graphics.getFont();
				LineMetrics			metrics	= ofont.getLineMetrics(string, ofrc);
				setFontSizeToFitBox(graphics, nameFont, width, string);
				FontRenderContext	frc				= graphics.getFontRenderContext();
				Font				font			= graphics.getFont();
				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
				int					w				= (int) stringBounds.getWidth();
				int					x2				= x1 + (width) / 2 - w / 2;
				int					y2				= (int) (y1 + metrics.getHeight());
				if (context.getParameterOptions().isCompact())
					y2 -= 2;
				if (string.contains("?"))
					graphics.setColor(Color.red);
				graphics.drawString(string, x2, y2);
//				drawBorder(graphics, x2, y2, string);
			}
			// last name
			{
				graphics.setColor(textColor);
				graphics.setFont(nameFont);
				String				string	= getLastNameAsString(context);
				FontRenderContext	ofrc	= graphics.getFontRenderContext();
				Font				ofont	= graphics.getFont();
				LineMetrics			metrics	= ofont.getLineMetrics(string, ofrc);
				setFontSizeToFitBox(graphics, nameFont, width, string);
				FontRenderContext	frc				= graphics.getFontRenderContext();
				Font				font			= graphics.getFont();
				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
				int					w				= (int) stringBounds.getWidth();
				int					x2				= x1 + (width) / 2 - w / 2;
				int					y2				= (int) (y1 + metrics.getHeight() * 2);
				if (context.getParameterOptions().isCompact())
					y2 -= 3;
				if (string.contains("?"))
					graphics.setColor(Color.red);
				graphics.drawString(string, x2, y2);
//				drawBorder(graphics, x2, y2, string);
			}
		}
		if (!context.getParameterOptions().isCompact()) {
			// clone
			graphics.setColor(textColor);
			graphics.setFont(nameFont);
			if (this instanceof FemaleClone || this instanceof MaleClone) {
				FontRenderContext	frc				= graphics.getFontRenderContext();
				Font				font			= graphics.getFont();
				String				string			= "*";
				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
				int					x2				= (int) (x1 + width - stringBounds.getWidth());
				int					y2				= (int) (y1 + stringBounds.getHeight());
				graphics.drawString(string, x2, y2);
//				drawBorder(graphics, x2, y2, string);
			}
		}
		if (!context.getParameterOptions().isCompact()) {
			// ID
			graphics.setColor(textColor);
			graphics.setFont(nameFont);
			{
				String	string	= "" + getId();
				int		x2		= x1 + 4;
				int		y2		= y1 + getHeight(context) - 4;
				graphics.drawString(string, x2, y2);
//				drawBorder(graphics, x2, y2, string);
			}
		}
		if (context.getParameterOptions().isCoordinates()) {
			// Coordinates
			graphics.setColor(Color.lightGray);
			graphics.setFont(livedFont);
			{
				String				string			= String.format("%d,%d", x, y);
				Font				font			= graphics.getFont();
				FontRenderContext	frc				= graphics.getFontRenderContext();
				LineMetrics			metrics			= font.getLineMetrics(string, frc);
				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
				int					x2				= (int) (x1 + width - stringBounds.getWidth() - 2);
				int					y2				= y1 + getHeight(context) - 4;
				graphics.drawString(string, x2, y2);
//				drawBorder(graphics, x2, y2, string);
			}
		}
		{
			// born
			if (!context.getParameterOptions().isCompact()) {
				graphics.setColor(textColor);
				graphics.setFont(livedFont);
				FontRenderContext	frc				= graphics.getFontRenderContext();
				Font				font			= graphics.getFont();
				String				string			= getBornString();
				LineMetrics			metrics			= font.getLineMetrics(string, frc);
				float				descent			= metrics.getDescent();
				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
				float				w				= (float) stringBounds.getWidth();
				float				h				= (float) stringBounds.getHeight();
				float				x2				= x1 + (width) / 2 - w / 2;
				float				y2				= y1 + getHeight(context) - 4 - metrics.getHeight();
				graphics.drawString(string, x2, y2);
			}
			// died
			if (!context.getParameterOptions().isCompact()) {
				graphics.setColor(textColor);
				graphics.setFont(livedFont);
				FontRenderContext	frc				= graphics.getFontRenderContext();
				Font				font			= graphics.getFont();
				String				string			= getDiedString();
				LineMetrics			metrics			= font.getLineMetrics(string, frc);
				float				descent			= metrics.getDescent();
				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
				float				w				= (float) stringBounds.getWidth();
				float				h				= (float) stringBounds.getHeight();
				float				x2				= x1 + (width) / 2 - w / 2;
				float				y2				= y1 + getHeight(context) - 4;
				graphics.drawString(string, x2, y2);
			}
		}
		{
			// errors
			graphics.setColor(Color.red);
			graphics.setFont(livedFont);
			{
				if (errors.size() != 0) {
					StringBuffer sb = new StringBuffer();
					for (String s : errors) {
						sb.append(s);
						sb.append(",");
					}
					String				string			= sb.toString();
					FontRenderContext	frc				= graphics.getFontRenderContext();
					Font				font			= graphics.getFont();
					Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
					int					x2				= x1 + 4;
					int					y2				= (int) (y1 + getHeight(context) + stringBounds.getHeight() - 2);
					graphics.drawString(string, x2, y2);
//					drawBorder(graphics, x2, y2, string);
				}
			}
		}
	}

//	private void drawHorizontalBox(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
//		int	mapX1	= x;
//		int	mapX2	= x + getWidth(context);
//		int	mapY1	= y;
//		int	mapY2	= y + getHeight(context);
//		graphics.setColor(backgroundColor);
//		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
//		graphics.setColor(borderColor);
//		graphics.drawRect(mapX1, mapY1, mapX2 - mapX1 - 1, mapY2 - mapY1 - 1);
//		graphics.setColor(textColor);
//		{
//			// first name
//			{
//				graphics.setFont(nameFont);
//				FontRenderContext	frc				= graphics.getFontRenderContext();
//				Font				font			= graphics.getFont();
//				String				string			= getFirstNameAsString(context);
//				LineMetrics			metrics			= font.getLineMetrics(string, frc);
//				float				descent			= metrics.getDescent();
//				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
//				float				w				= (float) stringBounds.getWidth();
//				float				h				= metrics.getHeight();
//				graphics.drawString(string, x + (getWidth(context)) / 2 - w / 2, y + getHeight(context) / 2 + metrics.getHeight() / 2 - descent - h);
//			}
//			// last name
//			{
//				graphics.setFont(nameFont);
//				FontRenderContext	frc				= graphics.getFontRenderContext();
//				Font				font			= graphics.getFont();
//				String				string			= getLastNameAsString(context);
//				LineMetrics			metrics			= font.getLineMetrics(string, frc);
//				float				descent			= metrics.getDescent();
//				Rectangle2D			stringBounds	= font.getStringBounds(string, frc);
//				float				w				= (float) stringBounds.getWidth();
////				float		h				= (float) stringBounds.getHeight();
//				graphics.drawString(string, x + (getWidth(context)) / 2 - w / 2, y + getHeight(context) / 2 + metrics.getHeight() / 2 - descent);
//			}
//		}
//		{
//			// ID
//			graphics.setFont(livedFont);
//			FontRenderContext	frc		= graphics.getFontRenderContext();
//			Font				font	= graphics.getFont();
//			{
//				String		string	= "" + getId();
//				LineMetrics	metrics	= font.getLineMetrics(string, frc);
//				graphics.drawString(string, x + 2, y + metrics.getHeight());
//			}
//		}
//		{
//			graphics.setFont(livedFont);
//			FontRenderContext	frc		= graphics.getFontRenderContext();
//			Font				font	= graphics.getFont();
//			// born
//			{
//				String		string			= getBornString();
//				LineMetrics	metrics			= font.getLineMetrics(string, frc);
//				float		descent			= metrics.getDescent();
//				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
//				float		w				= (float) stringBounds.getWidth();
//				float		h				= (float) stringBounds.getHeight();
//				graphics.drawString(string, x + (getWidth(context)) / 2 - w / 2, y + getHeight(context) / 2 + metrics.getHeight() / 2 - descent + h);
//			}
////			{
////				String		string			= "-";
////				LineMetrics	metrics			= font.getLineMetrics(string, frc);
////				float		descent			= metrics.getDescent();
////				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
////				float		w				= (float) stringBounds.getWidth();
////				float		h				= (float) stringBounds.getHeight();
////				graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
////			}
//			// died
//			{
//				String		string			= getDiedString();
//				LineMetrics	metrics			= font.getLineMetrics(string, frc);
//				float		descent			= metrics.getDescent();
//				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
//				float		w				= (float) stringBounds.getWidth();
//				float		h				= (float) stringBounds.getHeight();
//				graphics.drawString(string, x + (getWidth(context)) / 2 - w / 2, y + getHeight(context) / 2 + metrics.getHeight() / 2 - descent + h + h);
//			}
//		}
//	}

//	private void drawHorizontalConnectors(Context context, Graphics2D graphics) {
//		int	mapX1	= x;
//		int	mapX2	= x + getWidth(context);
//		int	mapY1	= y;
//		int	mapY2	= y + getHeight(context);
//		int	dy		= 0;						// childIndex;
//		// only child with no children
//		if (isFirstChild() && isLastChild() && !hasChildren()) {
//			Person	spouseParent	= getMother();
//			Person	rootParent		= getFather();
//			if (getMother().isMember(context)) {
//				spouseParent = getFather();
//				rootParent = getMother();
//			}
//			int	px	= spouseParent.x;
//			int	pw	= Person.getWidth(context);
//			if (rootParent.getSpouseList().size() == 1) {
//				px = rootParent.x;
//				pw = Person.getWidth(context);
//			} else {
//				px = spouseParent.x;
//				pw = Person.getWidth(context);
//			}
//			// vertical connector
//			graphics.fillRect(px + (pw) / 2, mapY1 - getYSpace(context), 1, getYSpace(context));
//		} else {
//			// first child horizontal connector starts at the vertical one
//			if (isFirstChild()) {
//				Person	spouseParent	= getMother();
//				Person	rootParent		= getFather();
//				if (getMother().isMember(context)) {
//					spouseParent = getFather();
//					rootParent = getMother();
//				}
//				int	px	= spouseParent.x;
//				int	pw	= Person.getWidth(context);
//				if (rootParent.getSpouseList().size() == 1) {
//					px = rootParent.x;
//					pw = Person.getWidth(context);
//				} else {
//					px = spouseParent.x;
//					pw = Person.getWidth(context);
//				}
//				graphics.setColor(Color.black);
//				// horizontal connector
//				graphics.fillRect(px + pw / 2, mapY1 - getYSpace(context) / 2 + dy, nextPersonX - (px + pw / 2), 1);
//				// vertical connector
//				graphics.fillRect(px + (pw) / 2, mapY1 - getYSpace(context), 1, getYSpace(context));
//			}
//			// last child horizontal connector without spouse ends at the vertical
//			else if (isLastChild()) {
//				graphics.setColor(Color.black);
//				graphics.fillRect(mapX1, mapY1 - getYSpace(context) / 2, (mapX2 - mapX1) / 2, 1);
//			}
//		}
//		// all the children in between
//		if (!isFirstChild() && !isLastChild() && isMember(context)) {
//			graphics.setColor(Color.black);
//			if (getSpouseList().size() == 1) {
////				graphics.setColor(Color.red);
//				Person spouse = getSpouseList().first();
//				graphics.fillRect(mapX1, mapY1 - getYSpace(context) / 2 + dy, nextPersonX - (mapX1) /*- (int)(spouse.width - Person.PERSON_X_SPACE)*/, 1);
////				graphics.setColor(Color.black);
//			} else {
////				graphics.setColor(Color.green);
//				graphics.fillRect(mapX1, mapY1 - getYSpace(context) / 2 + dy, nextPersonX - (mapX1), 1);
////				graphics.setColor(Color.black);
//			}
//		}
////		if (isSpouseOfLastChild()) {
////			graphics.setColor(Color.black);
////			graphics.fillRect(mapX1 - PERSON_X_SPACE / 2, mapY1 - PERSON_Y_SPACE / 2, (mapX2 - mapX1) / 2 + PERSON_X_SPACE / 2, 1);
////		}
//
//		// vertical connector
//		if (!isFirstChild() && !isSpouse()) {
//			graphics.fillRect(mapX1 + (mapX2 - mapX1) / 2, mapY1 - getYSpace(context) / 2, 1, getYSpace(context) / 2);
//		}
//
//		// sexual relation connector between a person and his/her spouse
//		if (hasChildren() && isMember(context) && !context.getParameterOptions().isExcludeSpouse()) {
//			Stroke stroke = graphics.getStroke();
//			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
//			graphics.setColor(Color.black);
//			graphics.drawLine(mapX2, mapY1 + (mapY2 - mapY1) / 2, mapX2 + getXSpace(context) / 2, mapY1 + (mapY2 - mapY1) / 2);
//			graphics.setStroke(stroke);
//		}
//	}

	private void drawBox(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException {
		Integer	width	= context.generationToMaxWidthMap.get(generation);
		float	x1		= x * (width + getXSpace(context));
		float	y1		= y * (getHeight(context) + Person.getYSpace(context));

		try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
			p.setNonStrokingColor(backgroundColor);
			p.fillRect(x1, y1, width, getHeight(context));
			p.fill();
		}
		if (isSpouse() && !isMember(context)) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(spouseBorderColor);
				p.setLineWidth(FAT_LINE_STROKE_WIDTH);
				p.setLineDashPattern(new float[] { 3 }, 0);
				p.drawRect(x1, y1, width, getHeight(context));
				p.stroke();
			}
		} else {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(borderColor);
				p.drawRect(x1, y1, width, getHeight(context));
				p.stroke();
			}
		}
		float firstNameHeight;
		{
			// first name
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setNonStrokingColor(textColor);
				String text = getFirstNameAsString(context);
				if (isFirstNameOl(context))
					p.setFont(p.getFontFittingWidth(nameOLFont, width, text));
				else
					p.setFont(p.getFontFittingWidth(nameFont, width, text));
				float stringWidth = p.getStringWidth(text);
				firstNameHeight = p.getStringHeight(text);
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
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setNonStrokingColor(textColor);
				String text = getLastNameAsString(context);
				if (isLastNameOl(context))
					p.setFont(p.getFontFittingWidth(nameOLFont, width, text));
				else
					p.setFont(p.getFontFittingWidth(nameFont, width, text));
				float	stringWidth		= p.getStringWidth(text);
				float	lastNameHeight	= p.getStringHeight(text);
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
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setNonStrokingColor(textColor);
				p.setFont(nameFont);
				if (this instanceof FemaleClone || this instanceof MaleClone) {
					String	text	= "*";
					float	x2		= (int) (x1 + width - p.getStringWidth(text));
					float	y2		= (int) (y1 + p.getStringHeight(text));
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
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
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
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setNonStrokingColor(Color.lightGray);
				p.setFont(dateFont);
				{
					String	text	= String.format("%d,%d", x, y);
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
				try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
					p.setNonStrokingColor(textColor);
					p.setFont(dateFont);
					String	text	= getBornString();
					float	w		= p.getStringWidth(text);
					float	h		= p.getStringHeight(text);
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
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setNonStrokingColor(textColor);
				p.setFont(dateFont);
				String	text	= getDiedString();
				float	w		= p.getStringWidth(text);
				float	h		= p.getStringHeight(text);
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
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
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
						float	w		= p.getStringWidth(text);
						float	h		= p.getStringHeight(text);
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
	public void drawHorizontal(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		if (isVisible()) {
			drawBox(context, graphics, nameFont, livedFont);
			drawHorizontalConnectors(context, graphics);
		}
	}

	private void drawHorizontalConnectors(Context context, Graphics2D graphics) {
		int		x1		= xIndexToPixel(context, x);
		int		y1		= yIndexToPixel(context, y);

		Stroke	stroke	= graphics.getStroke();
		// child Connector vertical
		if (isMember(context) && !isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
//			graphics.drawLine(x1 - getXSpace(context) / 2, y1 + getHeight(context) / 2, x1, y1 + getHeight(context) / 2);
			graphics.drawLine(x1 + getWidth(context) / 2, y1 - getYSpace(context) / 2, x1 + getWidth(context) / 2, y1);
		}
		// child Connector horizontal
		if (isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			int	cx1	= x1 + getWidth(context) / 2;
			int	cx2	= xIndexToPixel(context, getChildrenList().last().x) + getWidth(context) / 2;
			graphics.setColor(connectorColor);
//			graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
			graphics.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
		}
		// spouse connector to children
		if (isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
//			graphics.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
			graphics.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context), x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
		}

		// sexual relation connector from person to his/her spouse
		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(connectorColor);
//			graphics.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context), y1 + getHeight(context) / 2);
			graphics.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context), x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context));
			for (Person spouse : getSpouseList()) {
				int	sx	= xIndexToPixel(context, spouse.x);
				int	sy	= yIndexToPixel(context, spouse.y);
//				graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, sy + getHeight(context) / 2, sx, sy + getHeight(context) / 2);
				graphics.drawLine(sx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, sx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context));
			}
			int lsx = xIndexToPixel(context, getSpouseList().last().x);
//			graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, lsy + getHeight(context) / 2);
			graphics.drawLine(x1 + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2, lsx + getWidth(context) / 2, y1 + getHeight(context) + getYSpace(context) / 2);
		}
		// parent connector to children
		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
			int	cx1	= y1 + getWidth(context) / 2;
			int	cx2	= xIndexToPixel(context, getChildrenList().last().x) + getWidth(context) / 2;
			graphics.setColor(connectorColor);
//			graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
			graphics.drawLine(cx1, y1 + getHeight(context) + getYSpace(context) / 2, cx2, y1 + getHeight(context) + getYSpace(context) / 2);
		}
		graphics.setStroke(stroke);
	}

	@Override
	public void drawVertical(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		if (isVisible()) {
			drawBox(context, graphics, nameFont, livedFont);
			drawVerticalConnectors(context, graphics);
		}
	}

	@Override
	public void drawVertical(Context context, PdfDocument pdfDocument, PdfFont nameFont, PdfFont nameOLFont, PdfFont dateFont) throws IOException {
		if (isVisible()) {
			drawBox(context, pdfDocument, nameFont, nameOLFont, dateFont);
			drawVerticalConnectors(context, pdfDocument);
		}
	}

	private void drawVerticalConnectors(Context context, Graphics2D graphics) {
		int		x1		= xIndexToPixel(context, x);
		int		y1		= yIndexToPixel(context, y);

		Stroke	stroke	= graphics.getStroke();
		// child Connector horizontal
		if (isMember(context) && !isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 - getXSpace(context) / 2, y1 + getHeight(context) / 2, x1, y1 + getHeight(context) / 2);
		}
		// child Connector vertical

		if (isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			int	cy1	= y1 + getHeight(context) / 2;
			int	cy2	= yIndexToPixel(context, getChildrenList().last().y) + getHeight(context) / 2;
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
		}

		// spouse connector to children

		if (isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
		}

		// sexual relation connector from person to his/her spouse

		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context), y1 + getHeight(context) / 2);
			for (Person spouse : getSpouseList()) {
				int	sx	= xIndexToPixel(context, spouse.x);
				int	sy	= yIndexToPixel(context, spouse.y);
				graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, sy + getHeight(context) / 2, sx, sy + getHeight(context) / 2);
			}
			int lsy = yIndexToPixel(context, getSpouseList().last().y);
			graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, lsy + getHeight(context) / 2);
		}

		// spouse connector to children

		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
			int	cy1	= y1 + getHeight(context) / 2;
			int	cy2	= yIndexToPixel(context, getChildrenList().last().y) + getHeight(context) / 2;
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
		}

		graphics.setStroke(stroke);
	}

	private void drawVerticalConnectors(Context context, PdfDocument pdfDocument) throws IOException {
		int	x1	= xIndexToPixel(context, x);
		int	y1	= yIndexToPixel(context, y);

		// child Connector horizontal
		if (isMember(context) && !isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(MEDIUM_LINE_STROKE_WIDTH);
				p.drawLine(x1 - getXSpace(context) / 2, y1 + getHeight(context) / 2, x1, y1 + getHeight(context) / 2);
				p.stroke();
			}
		}
		// child Connector vertical

		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(MEDIUM_LINE_STROKE_WIDTH);
				int	cy1	= y1 + getHeight(context) / 2;
				int	cy2	= yIndexToPixel(context, getChildrenList().last().y) + getHeight(context) / 2;
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
				p.stroke();
			}
		}

		// spouse connector to children

		if (isSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(MEDIUM_LINE_STROKE_WIDTH);
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				p.stroke();
			}
		}

		// sexual relation connector from person to his/her spouse
		if (hasChildren() && isMember(context) && !isSpouse() && !context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(connectorColor);
				p.setLineDashPattern(new float[] { 3 }, 0);
				p.setLineWidth(MEDIUM_LINE_STROKE_WIDTH);
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context), y1 + getHeight(context) / 2);
				for (Person spouse : getSpouseList()) {
					int	sx	= xIndexToPixel(context, spouse.x);
					int	sy	= yIndexToPixel(context, spouse.y);
					p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, sy + getHeight(context) / 2, sx, sy + getHeight(context) / 2);
				}
				int lsy = yIndexToPixel(context, getSpouseList().last().y);
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, lsy + getHeight(context) / 2);
				p.stroke();
			}
		}

		// spouse connector to children
		if (hasChildren() && isMember(context) && context.getParameterOptions().isExcludeSpouse()) {
			try (CloseableGraphicsState p = new CloseableGraphicsState(pdfDocument)) {
				p.setStrokingColor(connectorColor);
				p.setLineWidth(MEDIUM_LINE_STROKE_WIDTH);
				p.drawLine(x1 + getWidth(context), y1 + getHeight(context) / 2, x1 + getWidth(context) + getXSpace(context) / 2, y1 + getHeight(context) / 2);
				int	cy1	= y1 + getHeight(context) / 2;
				int	cy2	= yIndexToPixel(context, getChildrenList().last().y) + getHeight(context) / 2;
				p.drawLine(x1 + getWidth(context) + getXSpace(context) / 2, cy1, x1 + getWidth(context) + getXSpace(context) / 2, cy2);
				p.stroke();
			}
		}

	}

	private void setFontSizeToFitBox(Graphics2D graphics, Font nameFont, Integer boxWidth, String text) {
		graphics.setFont(nameFont);
		FontRenderContext	frc				= graphics.getFontRenderContext();
		Font				font			= graphics.getFont();
		Rectangle2D			stringBounds	= font.getStringBounds(text, frc);
		int					size			= nameFont.getSize();
		if (stringBounds.getWidth() > boxWidth - 4) {
			// reduce font size
			do {
				size--;
				Font deriveFont = nameFont.deriveFont((float) size);
				graphics.setFont(deriveFont);
				frc = graphics.getFontRenderContext();
				stringBounds = deriveFont.getStringBounds(text, frc);
			} while (stringBounds.getWidth() > boxWidth - 4);
		}
	}

	@Override
	public String toString(Context context) {
		return String.format("[%d] %s (%s) x=%d y=%d", getId(), getName(context), getLivedString(), x, y);
	}

	private int xIndexToPixel(Context context, int x) {
		return x * (getWidth(context) + getXSpace(context));
	}

	private int yIndexToPixel(Context context, int y) {
		return y * (getHeight(context) + Person.getYSpace(context));
	}
}
