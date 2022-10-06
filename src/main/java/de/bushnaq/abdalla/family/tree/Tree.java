package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.person.Female;
import de.bushnaq.abdalla.family.person.FemaleClone;
import de.bushnaq.abdalla.family.person.Male;
import de.bushnaq.abdalla.family.person.MaleClone;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;

public abstract class Tree {
	protected Context	context;
	public List<String>	errors		= new ArrayList<>();
	int					iteration	= 0;
	Font				livedFont;
	final Logger		logger		= LoggerFactory.getLogger(this.getClass());
	Font				nameFont;
	PersonList			personList;

	public Tree(Context context) {
		this.context = context;
		livedFont = new Font("Arial", Font.PLAIN, (Person.PERSON_HEIGHT - Person.PERSON_BORDER + 2 - Person.PERSON_MARGINE * 2) / 5);
		nameFont = new Font("Arial", Font.BOLD, (Person.PERSON_HEIGHT - Person.PERSON_BORDER + 2 - Person.PERSON_MARGINE * 2) / 4);
	}

	abstract int calclateImageWidth();

	private void calculateGenrationMaxWidth() {
		for (Person p : personList) {
			Integer integerMaxWidth = context.generationToMaxWidthMap.get(p.generation);
			if (integerMaxWidth == null) {
				context.generationToMaxWidthMap.put(p.generation, Person.getWidth(context));
			} else {
				if (Person.getWidth(context) > integerMaxWidth)
					context.generationToMaxWidthMap.put(p.generation, Person.getWidth(context));
			}
		}
	}

	abstract int calculateImageHeight(Context context);

	protected void calculateWidths() {
		BufferedImage	image		= new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= image.createGraphics();
		graphics.setFont(nameFont);
		personList.calculateWidths(graphics, nameFont, livedFont);
	}

	abstract void draw(Context context, Graphics2D graphics);

	private void drawErrors(Graphics2D graphics, int imageWidth, int imageHeight) {
		graphics.setFont(nameFont);
		graphics.setColor(Color.red);
		int y = imageHeight - 2;
		for (String s : errors) {
			Font				font			= graphics.getFont();
			FontRenderContext	frc				= graphics.getFontRenderContext();
			Rectangle2D			stringBounds	= font.getStringBounds(s, frc);
			graphics.drawString(s, 2, y);
			y -= stringBounds.getHeight();
		}
	}

	private void drawGrid(Graphics2D graphics, int imageWidth, int imageHeight) {
//		graphics.setFont(nameFont);
//		graphics.setColor(Color.lightGray);
//		int	h	= Person.PERSON_HEIGHT + Person.PERSON_Y_SPACE;
//		int	w	= Person.PERSON_WIDTH + Person.PERSON_X_SPACE;
//		for (int y = 0; y < imageHeight / h + 1; y++) {
//			graphics.fillRect(0, -Person.PERSON_Y_SPACE / 2 + y * h, imageWidth, 1);
//			for (int x = 0; x < imageWidth / w + 1; x++) {
//				graphics.fillRect(-Person.PERSON_X_SPACE / 2 + x * w, 0, 1, imageHeight);
//				graphics.drawString(String.format("%d,%d", x, y), x * w + 2, -Person.PERSON_Y_SPACE / 2 + y * h + Person.PERSON_HEIGHT - Person.PERSON_Y_SPACE - 2);
//			}
//		}
	}

	private List<Male> findFirstFathers() {
		List<Male> fathers = new ArrayList<>();
		for (Person p : personList) {
			if (p.isRootFather(context)) {
				fathers.add((Male) p);
			}
		}
		return fathers;
	}

	public BufferedImage generate(Context context, String familyName) throws Exception {
		String imageFilenName = familyName + ".png";
		initializePersonList(context);
		int				imageWidth	= calclateImageWidth();
		int				imageHeight	= calculateImageHeight(context);
		BufferedImage	aImage		= new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= aImage.createGraphics();
		graphics.setFont(nameFont);
		personList.printPersonList(context);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, imageWidth, imageHeight);
		drawGrid(graphics, imageWidth, imageHeight);
		drawErrors(graphics, imageWidth, imageHeight);
		draw(context, graphics);
		try {
			File outputfile = new File(imageFilenName);
			ImageIO.write(aImage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return aImage;
	}

	private void initAttribute(Person person) {
		PersonList	spouseList	= person.getSpouseList();
		int			spouseIndex	= 0;
		for (Person spouse : spouseList) {
			if (spouse.isMember(context)) {
				// both parents are member of the family
				// ignore any clone that we already have converted
				if (!context.getParameterOptions().isFollowFemales() && person.isMale() && !(spouse instanceof FemaleClone)) {
					// create a clone of the spouse and shift all child relations to that clone
					FemaleClone	clone			= new FemaleClone(spouse.personList, (Female) spouse);
					PersonList	childrenList	= person.getChildrenList(spouse);
					for (Person child : childrenList) {
						child.setMother(clone);
					}
					personList.add(clone);
					spouse = clone;
					spouse.spouseIndex = spouseIndex++;
					spouse.setSpouse(true);
				} else if (context.getParameterOptions().isFollowFemales() && person.isFemale() && !(spouse instanceof MaleClone)) {
					// create a clone of the spouse and shift all child relations to that clone
					MaleClone	clone			= new MaleClone(spouse.personList, (Male) spouse);
					PersonList	childrenList	= person.getChildrenList(spouse);
					for (Person child : childrenList) {
						child.setFather(clone);
					}
					personList.add(clone);
					spouse = clone;
					spouse.spouseIndex = spouseIndex++;
					spouse.setSpouse(true);
				}
			} else {
				spouse.spouseIndex = spouseIndex++;
				spouse.setSpouse(true);
			}
			if (person.isLastChild()) {
				spouse.setSpouseOfLastChild(true);
			}
			// children
			int			childIndex		= 0;
			boolean		firstChild		= true;
			PersonList	childrenList	= person.getChildrenList(spouse);
			for (Person child : childrenList) {
				child.generation = person.generation + 1;
				child.childIndex = childIndex++;
				child.setIsChild(true);
				if (firstChild) {
					child.setFirstChild(true);
					firstChild = false;
				}
				if (child.equals(childrenList.last())) {
					child.setLastChild(true);
				}
				initAttribute(child);
			}
		}
	}

	private void initAttributes() {
		List<Male> firstFathers = findFirstFathers();
		for (Person firstFather : firstFathers) {
			firstFather.setFirstFather(true);
			firstFather.generation = 0;
			initAttribute(firstFather);
		}
		for (Person p : personList) {
			p.validate(context);
		}
	}

	private void initializePersonList(Context context) {
		initAttributes();
		calculateWidths();
		calculateGenrationMaxWidth();
		position(context);
		for (Person p : personList) {
			if (!p.isVisible())
				errors.add(String.format("Error #001: [%d]%s %s is not visible. A person is visible if he is member of the family or has children with someone from the family.", p.getId(), p.getFirstName(),
						p.getLastName()));
		}
	}

	private void position(Context context) {
		List<Male>	firstFathers	= findFirstFathers();
		int			rootFatherIndex	= 0;
		for (Person firstFather : firstFathers) {
			firstFather.x = rootFatherIndex++ * 10;
			firstFather.y = 0;
			position(context, firstFather);
		}
	}

	abstract int position(Context context, Person person);

	public void readExcel(String fileName) throws Exception {
		TreeExcelReader excelReader = new TreeExcelReader();
		personList = excelReader.readExcel(fileName);
	}

}
