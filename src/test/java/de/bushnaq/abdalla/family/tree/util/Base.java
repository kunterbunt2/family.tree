package de.bushnaq.abdalla.family.tree.util;

import java.awt.image.BufferedImage;

import de.bushnaq.abdalla.family.tree.HorizontalTree;
import de.bushnaq.abdalla.family.tree.Tree;
import de.bushnaq.abdalla.util.TestUtil;

public class Base {
	Tree treeMaster = new HorizontalTree();

	public void generate(String fileName) throws Exception {
		treeMaster.readExcel(fileName + ".xlsx");
		BufferedImage image = treeMaster.generate(fileName);
		if (TestUtil.isRunningInEclipse())
			showImage(image);
	}

	private void showImage(BufferedImage image) {
		MyCanvas c = new MyCanvas(image);
		while (c.f.isVisible())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
