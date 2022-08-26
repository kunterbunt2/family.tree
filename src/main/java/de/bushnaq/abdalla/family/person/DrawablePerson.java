package de.bushnaq.abdalla.family.person;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import de.bushnaq.abdalla.family.Context;

public abstract class DrawablePerson extends Person {
	protected static final float	FAT_LINE_STROKE_WIDTH		= 3.1f;
	protected static final float	MEDIUM_LINE_STROKE_WIDTH	= 2.1f;
	private Color					backgroundColor;
	private Color					borderColor					= new Color(0, 0, 0, 64);
	private Color					connectorColor				= Color.gray;
	private Color					spouseBorderColor;
	private Color					textColor					= new Color(0, 0, 0);

	public DrawablePerson(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother, Color backgroundColor) {
		super(personList, id, firstName, lastName, born, died, father, mother);
		this.backgroundColor = backgroundColor;
		this.spouseBorderColor = new Color(backgroundColor.getRGB());
	}

	public DrawablePerson(PersonList personList, Integer id, Color backgroundColor) {
		super(personList, id);
		this.backgroundColor = backgroundColor;
		this.spouseBorderColor = new Color(backgroundColor.getRGB());
	}

	@Override
	public void drawHorizontal(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		if (isVisible()) {
			drawHorizontalBox(graphics, nameFont, livedFont);
			drawHorizontalConnectors(context, graphics);
		}
	}

	private void drawHorizontalBox(Graphics2D graphics, Font nameFont, Font livedFont) {
		int	mapX1	= x;
		int	mapX2	= x + width;
		int	mapY1	= y;
		int	mapY2	= y + PERSON_HEIGHT;
		graphics.setColor(backgroundColor);
		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
		graphics.setColor(borderColor);
		graphics.drawRect(mapX1, mapY1, mapX2 - mapX1 - 1, mapY2 - mapY1 - 1);
		graphics.setColor(textColor);
		{
			graphics.setFont(nameFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// first name
			{
				String		string			= getFirstNameAsString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent - h);
			}
			// last name
			{
				String		string			= getLastNameAsString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
//				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent);
			}
		}
		{
			// ID
			graphics.setFont(livedFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			{
				String		string	= "" + getId();
				LineMetrics	metrics	= font.getLineMetrics(string, frc);
				graphics.drawString(string, x + 2, y + metrics.getHeight());
			}
		}
		{
			graphics.setFont(livedFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// born
			{
				String		string			= getBornString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
			}
//			{
//				String		string			= "-";
//				LineMetrics	metrics			= font.getLineMetrics(string, frc);
//				float		descent			= metrics.getDescent();
//				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
//				float		w				= (float) stringBounds.getWidth();
//				float		h				= (float) stringBounds.getHeight();
//				graphics.drawString(string, x + width / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
//			}
			// died
			{
				String		string			= getDiedString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h + h);
			}
		}
	}

	private void drawHorizontalConnectors(Context context, Graphics2D graphics) {
		int	mapX1	= x;
		int	mapX2	= x + width;
		int	mapY1	= y;
		int	mapY2	= y + PERSON_HEIGHT;
		int	dy		= 0;				// childIndex;
		// only child with no children
		if (isFirstChild() && isLastChild() && !hasChildren()) {
			Person	spouseParent	= getMother();
			Person	rootParent		= getFather();
			if (getMother().isMember()) {
				spouseParent = getFather();
				rootParent = getMother();
			}
			int	px	= spouseParent.x;
			int	pw	= spouseParent.width;
			if (rootParent.getSpouseList().size() == 1) {
				px = rootParent.x;
				pw = rootParent.width;
			} else {
				px = spouseParent.x;
				pw = spouseParent.width;
			}
			// vertical connector
			graphics.fillRect(px + (pw) / 2, mapY1 - PERSON_Y_SPACE, 1, PERSON_Y_SPACE);
		} else {
			// first child horizontal connector starts at the vertical one
			if (isFirstChild()) {
				Person	spouseParent	= getMother();
				Person	rootParent		= getFather();
				if (getMother().isMember()) {
					spouseParent = getFather();
					rootParent = getMother();
				}
				int	px	= spouseParent.x;
				int	pw	= spouseParent.width;
				if (rootParent.getSpouseList().size() == 1) {
					px = rootParent.x;
					pw = rootParent.width;
				} else {
					px = spouseParent.x;
					pw = spouseParent.width;
				}
				graphics.setColor(Color.black);
				// horizontal connector
				graphics.fillRect(px + pw / 2, mapY1 - PERSON_Y_SPACE / 2 + dy, nextPersonX - (px + pw / 2), 1);
				// vertical connector
				graphics.fillRect(px + (pw) / 2, mapY1 - PERSON_Y_SPACE, 1, PERSON_Y_SPACE);
			}
			// last child horizontal connector without spouse ends at the vertical
			else if (isLastChild()) {
				graphics.setColor(Color.black);
				graphics.fillRect(mapX1, mapY1 - PERSON_Y_SPACE / 2, (mapX2 - mapX1) / 2, 1);
			}
		}
		// all the children in between
		if (!isFirstChild() && !isLastChild() && isMember()) {
			graphics.setColor(Color.black);
			if (getSpouseList().size() == 1) {
//				graphics.setColor(Color.red);
				Person spouse = getSpouseList().first();
				graphics.fillRect(mapX1, mapY1 - PERSON_Y_SPACE / 2 + dy, nextPersonX - (mapX1) /*- (int)(spouse.width - Person.PERSON_X_SPACE)*/, 1);
//				graphics.setColor(Color.black);
			} else {
//				graphics.setColor(Color.green);
				graphics.fillRect(mapX1, mapY1 - PERSON_Y_SPACE / 2 + dy, nextPersonX - (mapX1), 1);
//				graphics.setColor(Color.black);
			}
		}
//		if (isSpouseOfLastChild()) {
//			graphics.setColor(Color.black);
//			graphics.fillRect(mapX1 - PERSON_X_SPACE / 2, mapY1 - PERSON_Y_SPACE / 2, (mapX2 - mapX1) / 2 + PERSON_X_SPACE / 2, 1);
//		}

		// vertical connector
		if (!isFirstChild() && !isSpouse()) {
			graphics.fillRect(mapX1 + (mapX2 - mapX1) / 2, mapY1 - PERSON_Y_SPACE / 2, 1, PERSON_Y_SPACE / 2);
		}

		// sexual relation connector
		if (hasChildren() && isMember() && context.includeSpouse) {
			Stroke stroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(Color.black);
			graphics.drawLine(mapX2, mapY1 + (mapY2 - mapY1) / 2, mapX2 + PERSON_X_SPACE, mapY1 + (mapY2 - mapY1) / 2);
			graphics.setStroke(stroke);
		}
	}

	@Override
	public void drawVertical(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		if (isVisible()) {
			drawVerticalBox(context, graphics, nameFont, livedFont);
			drawVerticalConnectors(context, graphics);
		}
	}

	private void drawVerticalBox(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		Integer	width	= context.generationToMaxWidthMap.get(generation);
		int		x1		= x * (width + Person.PERSON_X_SPACE);
		int		y1		= y * (Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE);
		graphics.setColor(backgroundColor);
		graphics.fillRect(x1, y1, width, height);
		if (isSpouse() && !isMember()) {
			Stroke stroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(spouseBorderColor);
			graphics.drawRect(x1, y1, width - 1, height - 1);
			graphics.setStroke(stroke);
		} else {
			graphics.setColor(borderColor);
			graphics.drawRect(x1, y1, width - 1, height - 1);
		}
		graphics.setColor(textColor);
		{
			graphics.setFont(nameFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// first name
			{
				String		string			= getFirstNameAsString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x1 + idWidth + (width - idWidth) / 2 - w / 2, y1 + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent - h);
			}
			// last name
			{
				String		string			= getLastNameAsString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				graphics.drawString(string, x1 + idWidth + (width - idWidth) / 2 - w / 2, y1 + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent);
			}
		}
		{
			// ID
			graphics.setFont(livedFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			{
				String		string	= "" + getId();
				LineMetrics	metrics	= font.getLineMetrics(string, frc);
				graphics.drawString(string, x1 + 2, y1 + metrics.getHeight());
			}
		}
		{
			graphics.setFont(livedFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// born
			{
				String		string			= getBornString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x1 + idWidth + (width - idWidth) / 2 - w / 2, y1 + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h);
			}
			// died
			{
				String		string			= getDiedString();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x1 + idWidth + (width - idWidth) / 2 - w / 2, y1 + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent + h + h);
			}
		}
	}

	private void drawVerticalConnectors(Context context, Graphics2D graphics) {
		int		x1		= xIndexToPixel(x);
		int		y1		= yIndexToPixel(y);
//		int	dy	= 0;				// childIndex;

		Stroke	stroke	= graphics.getStroke();
		// child Connector horizontal
		if (isMember()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 - PERSON_X_SPACE / 2, y1 + height / 2, x1, y1 + height / 2);
		}
		// child Connector vertical
		if (isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			int	cy1	= y1 + PERSON_HEIGHT / 2;
//			PersonList	childrenList	= getChildrenList();
			int	cy2	= yIndexToPixel(getChildrenList().last().y) + PERSON_HEIGHT / 2;
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + width + PERSON_X_SPACE / 2, cy1, x1 + width + PERSON_X_SPACE / 2, cy2);
		}
		// spouse connector to children
		if (isSpouse()) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + width, y1 + height / 2, x1 + width + PERSON_X_SPACE / 2, y1 + height / 2);
		}

		// sexual relation connector
		if (hasChildren() && isMember() && context.includeSpouse) {
			graphics.setStroke(new BasicStroke(FAT_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + width, y1 + height / 2, x1 + width + PERSON_X_SPACE, y1 + height / 2);
		}
		// spouse connector to children
		if (hasChildren() && isMember() && !context.includeSpouse) {
			graphics.setStroke(new BasicStroke(MEDIUM_LINE_STROKE_WIDTH));
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + width, y1 + height / 2, x1 + width + PERSON_X_SPACE / 2, y1 + height / 2);
			int	cy1	= y1 + PERSON_HEIGHT / 2;
			int	cy2	= yIndexToPixel(getChildrenList().last().y) + PERSON_HEIGHT / 2;
			graphics.setColor(connectorColor);
			graphics.drawLine(x1 + width + PERSON_X_SPACE / 2, cy1, x1 + width + PERSON_X_SPACE / 2, cy2);
		}
		graphics.setStroke(stroke);
	}

	@Override
	public String toString() {
		return String.format("id=%d name='%s' lived='%s' x=%d y=%d", getId(), getName(), getLivedString(), x, y);
	}

	private int xIndexToPixel(int x) {
		return x * (width + Person.PERSON_X_SPACE);
	}

	private int yIndexToPixel(int y) {
		return y * (Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE);
	}
}
