package de.bushnaq.abdalla.family.tree.util;

import java.awt.image.BufferedImage;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.tree.HorizontalTree;
import de.bushnaq.abdalla.family.tree.Tree;
import de.bushnaq.abdalla.family.tree.VerticalTree;
import de.bushnaq.abdalla.util.TestUtil;

public class Base {

	public void generateHorizontal(Context context, String fileName, String outputName) throws Exception {
		Tree horizontalTree = new HorizontalTree(context);
		horizontalTree.readExcel(fileName + ".xlsx");
		BufferedImage image = horizontalTree.generate(context, outputName);
		if (TestUtil.isRunningInEclipse())
			showImage(image);
	}

	public void generateVertical(Context context, String fileName, String outputName) throws Exception {
		Tree verticalTree = new VerticalTree(context);
		verticalTree.readExcel(fileName + ".xlsx");
		BufferedImage image = verticalTree.generate(context, outputName);
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
