package de.bushnaq.abdalla.songmaster;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.tree.TreeMaster;

public class TestCase01 {
	TreeMaster treeMaster = new TreeMaster();

	@Test
	public void generateFraomExcel() throws Exception {
		treeMaster.readExcel("test-case-01.xlsx");
		BufferedImage	image	= treeMaster.generate("test-case-01");
		MyCanvas		c		= new MyCanvas(image);
		while (c.f.isVisible())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
