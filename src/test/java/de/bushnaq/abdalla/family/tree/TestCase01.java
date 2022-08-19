package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.tree.util.Base;

public class TestCase01 extends Base {

	@Test
	public void generateFraomExcel() throws Exception {
		generate("test-case-01");
//		treeMaster.readExcel("test-case-01.xlsx");
//		BufferedImage	image	= treeMaster.generate("test-case-01");
//		MyCanvas		c		= new MyCanvas(image);
//		while (c.f.isVisible())
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
	}

}
