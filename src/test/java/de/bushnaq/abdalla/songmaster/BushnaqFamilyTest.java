package de.bushnaq.abdalla.songmaster;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.tree.TreeMaster;

public class BushnaqFamilyTest {
	TreeMaster treeMaster = new TreeMaster();

	@Test
	public void generateFraomExcel() throws Exception {
		treeMaster.readExcel("bushnaq-family.xlsx");
		BufferedImage	image	= treeMaster.generate("bushnaq");
		MyCanvas		c		= new MyCanvas(image);

		while (c.f.isVisible())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
