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
	private Color	backgroundColor;
	private Color	borderColoe	= new Color(0, 0, 0, 64);
	private Color	textColoe	= new Color(0, 0, 0);

	public DrawablePerson(PersonList personList, int id, String firstName, String lastName, Date born, Date died, Male father, Female mother, Color backgroundColor) {
		super(personList, id, firstName, lastName, born, died, father, mother);
		this.backgroundColor = backgroundColor;
	}

	private void drawBox(Graphics2D graphics, Font nameFont, Font livedFont, int mapX1, int mapX2, int mapY1, int mapY2) {
		graphics.setColor(backgroundColor);
		graphics.fillRect(mapX1, mapY1, mapX2 - mapX1, mapY2 - mapY1);
		graphics.setColor(borderColoe);
		graphics.drawRect(mapX1, mapY1, mapX2 - mapX1 - 1, mapY2 - mapY1 - 1);
		graphics.setColor(textColoe);
		{
			graphics.setFont(nameFont);
			FontRenderContext	frc		= graphics.getFontRenderContext();
			Font				font	= graphics.getFont();
			// first name
			{
				String		string			= getFirstName();
				LineMetrics	metrics			= font.getLineMetrics(string, frc);
				float		descent			= metrics.getDescent();
				Rectangle2D	stringBounds	= font.getStringBounds(string, frc);
				float		w				= (float) stringBounds.getWidth();
				float		h				= (float) stringBounds.getHeight();
				graphics.drawString(string, x + idWidth + (width - idWidth) / 2 - w / 2, y + PERSON_HEIGHT / 2 + metrics.getHeight() / 2 - descent - h);
			}
			// last name
			{
				String		string			= getLastName();
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
				String		string	= "" + id;
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

	private void drawConnectors(Context context, Graphics2D graphics, int mapX1, int mapX2, int mapY1, int mapY2) {
		int dy = 0;// childIndex;
		// only child with no children
		if (isFirstChild() && isLastChild() && !hasChildren()) {
			Person	spouseParent	= mother;
			Person	rootParent		= father;
			if (mother.isMember()) {
				spouseParent = father;
				rootParent = mother;
			}
			int	px	= spouseParent.x;
			int	pw	= (int) spouseParent.width;
			if (rootParent.getSpouseList().size() == 1) {
				px = rootParent.x;
				pw = (int) rootParent.width;
			} else {
				px = spouseParent.x;
				pw = (int) spouseParent.width;
			}
			// vertical connector
			graphics.fillRect(px + (pw) / 2, mapY1 - PERSON_Y_SPACE, 1, PERSON_Y_SPACE);
		} else {
			// first child horizontal connector starts at the vertical one
			if (isFirstChild()) {
				Person	spouseParent	= mother;
				Person	rootParent		= father;
				if (mother.isMember()) {
					spouseParent = father;
					rootParent = mother;
				}
				int	px	= spouseParent.x;
				int	pw	= (int) spouseParent.width;
				if (rootParent.getSpouseList().size() == 1) {
					px = rootParent.x;
					pw = (int) rootParent.width;
				} else {
					px = spouseParent.x;
					pw = (int) spouseParent.width;
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
			graphics.setStroke(new BasicStroke(STANDARD_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			graphics.setColor(Color.black);
			graphics.drawLine(mapX2, mapY1 + (mapY2 - mapY1) / 2, mapX2 + PERSON_X_SPACE, mapY1 + (mapY2 - mapY1) / 2);
			graphics.setStroke(stroke);
		}
	}

	public boolean isMember() {
		return attribute.member;
	}

	protected static final float STANDARD_LINE_STROKE_WIDTH = 3.1f;

	@Override
	public void drawHorizontal(Context context, Graphics2D graphics, Font nameFont, Font livedFont) {
		if (attribute.show) {
			int	mapX1	= x;
			int	mapX2	= (int) (x + width);
			int	mapY1	= y;
			int	mapY2	= (int) (y + PERSON_HEIGHT);
			drawBox(graphics, nameFont, livedFont, mapX1, mapX2, mapY1, mapY2);
			drawConnectors(context, graphics, mapX1, mapX2, mapY1, mapY2);
		}
	}

	@Override
	public String toString() {
		return String.format("%d %s %s %d %d", id, getName(), getLivedString(), x, y);
	}
}
