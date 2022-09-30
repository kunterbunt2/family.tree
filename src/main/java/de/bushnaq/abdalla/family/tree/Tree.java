package de.bushnaq.abdalla.family.tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	private Context	context;
	int				iteration	= 0;
	Font			livedFont	= new Font("Arial", Font.PLAIN, (Person.PERSON_HEIGHT - Person.PERSON_BORDER + 2 - Person.PERSON_MARGINE * 2) / 5);
	final Logger	logger		= LoggerFactory.getLogger(this.getClass());
	Font			nameFont	= new Font("Arial", Font.BOLD, (Person.PERSON_HEIGHT - Person.PERSON_BORDER + 2 - Person.PERSON_MARGINE * 2) / 4);
	PersonList		personList;

	public Tree(Context context) {
		this.context = context;
	}

	abstract int calclateImageWidth();

	private void calculateGenrationMaxWidth() {
		for (Person p : personList) {
			Integer integerMaxWidth = context.generationToMaxWidthMap.get(p.generation);
			if (integerMaxWidth == null) {
				context.generationToMaxWidthMap.put(p.generation, p.width);
			} else {
				if (p.width > integerMaxWidth)
					context.generationToMaxWidthMap.put(p.generation, p.width);
			}
		}
	}

	abstract int calculateImageHeight();

	protected void calculateWidths() {
		BufferedImage	image		= new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= image.createGraphics();
		graphics.setFont(nameFont);
		personList.calculateWidths(graphics, nameFont, livedFont);
	}

	abstract void draw(Context context, Graphics2D graphics);

	private Male findFirstFather() {
		Male firstFather = null;
		for (Person p : personList) {
			if (p.isMale()) {
				if (p.hasChildren()) {
					if (firstFather == null || p.bornBefore(firstFather)) {
						firstFather = (Male) p;
					}
				}
			}
		}
		return firstFather;
	}

	public BufferedImage generate(Context context, String familyName) throws Exception {
		String imageFilenName = familyName + ".png";
		initializePersonList(context);
		int				imageWidth	= calclateImageWidth();
		int				imageHeight	= calculateImageHeight();
		BufferedImage	aImage		= new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D		graphics	= aImage.createGraphics();
		graphics.setFont(nameFont);
		personList.printPersonList(context);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, imageWidth, imageHeight);
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
			if (spouse.isMember()) {
				// both parents are member of the family
				if (!context.getParameterOptions().isFollowFemales() && person.isMale()) {
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
				} else if (context.getParameterOptions().isFollowFemales() && person.isFemale()) {
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
		Male firstFather = findFirstFather();
		firstFather.setFirstFather(true);
		firstFather.generation = 0;
		initAttribute(firstFather);
	}

	private void initializePersonList(Context context) {
		initAttributes();
		calculateWidths();
		calculateGenrationMaxWidth();
		position(context);
	}

	private void position(Context context) {
		Male firstFather = findFirstFather();
		firstFather.x = 0;
		firstFather.y = 0;
		position(context, firstFather);
	}

	abstract int position(Context context, Person person);

	public void readExcel(String fileName) throws Exception {
		TreeExcelReader excelReader = new TreeExcelReader();
		personList = excelReader.readExcel(fileName);
	}

}
