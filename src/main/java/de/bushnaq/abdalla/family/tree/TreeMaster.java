package de.bushnaq.abdalla.family.tree;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

public class TreeMaster {
	PersonList personList = new PersonList();

	public Female addFemale(String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		return personList.addFemale(firstName, lastName, born, died, father, mother);
	}

	public Male addMale(String firstName, String lastName, Calendar born, Calendar died, Male father, Female mother) {
		return personList.addMale(firstName, lastName, born, died, father, mother);
	}

	private void draw(String familyName) {
		Font			nameFont		= new Font("Arial", Font.PLAIN, Person.PERSON_HEIGHT / 3);
		BufferedImage	aImage			= new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);

		String			imageFilenName	= familyName + "-family-tree.png";
		Graphics2D		graphics		= aImage.createGraphics();
		graphics.setFont(nameFont);
		Person.personWidth = personList.calculateMaxNameWidth(graphics) + Person.PERSON_MARGINE * 4;
		position();
		int	imageWidth	= personList.getWidth();
		int	imageHeight	= personList.getHeight();
		aImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		graphics = aImage.createGraphics();
		graphics.setFont(nameFont);
//		System.out.println(String.format("first father is %s %s", firstFather.firstName, firstFather.lastName));
		personList.printPersonList();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		for (Person p : personList) {
			p.draw(graphics);
		}
		try {
			File outputfile = new File(imageFilenName);
			ImageIO.write(aImage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Male findFirstFather() {
		Male firstFather = null;
		for (Person p : personList) {
			if (p.isMale()) {
				if (p.hasChildren()) {
					if (firstFather == null || p.born.before(firstFather.born)) {
						firstFather = (Male) p;
					}
				}
			}
		}
		return firstFather;
	}

	private void position() {
		Male firstFather = findFirstFather();
		firstFather.x = 0;
		firstFather.y = 0;
		boolean change = false;
		do {
			change = false;
			for (Person p : personList) {
				if (p.isMale())
					if (position((Male) p))
						change = true;
			}
		} while (change);
	}

	private boolean position(Male father) {
		boolean			change		= false;
		List<Female>	wifeList	= father.getWifeList();
		for (Female female : wifeList) {
			if (female.y != father.y) {
				female.y = father.y;
				change = true;
			}
			if (female.x != father.x + Person.personWidth) {
				female.x = father.x + Person.personWidth;
				change = true;
			}
			int childIndex = 0;
			for (Person p : personList) {
				if ((p.father != null && p.father.equals(father)) && (p.mother != null && p.mother.equals(female))) {
					if (p.y != father.y + Person.PERSON_HEIGHT) {
						p.y = father.y + Person.PERSON_HEIGHT;
						change = true;
					}
					if (p.x != father.x + childIndex * Person.personWidth) {
						p.x = father.x + childIndex * Person.personWidth;
						change = true;
					}
					childIndex++;
				}
			}
		}
		return change;
	}

	public void generate(String familyName) throws Exception {
		testAlbumList();
		draw(familyName);
	}

	private void testAlbumList() throws Exception {
		{
			System.out.println("/*-----------------------------------------------------------------");
			System.out.println("* test if songs on HDD exist in the album list");
			System.out.println("-----------------------------------------------------------------*/");

			System.out.println("Success");
		}
	}

}
