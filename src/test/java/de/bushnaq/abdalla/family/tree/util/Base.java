package de.bushnaq.abdalla.family.tree.util;

import java.awt.image.BufferedImage;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.tree.HorizontalTree;
import de.bushnaq.abdalla.family.tree.Tree;
import de.bushnaq.abdalla.family.tree.VerticalTree;
import de.bushnaq.abdalla.util.ObfuscatingBase;
import de.bushnaq.abdalla.util.TestUtil;

public class Base {
	private ObfuscatingBase	ObfuscatingBase	= new ObfuscatingBase();
	protected boolean					obfuscate		= true;

	public void generateHorizontal(Context context, String inputName, String outputDecorator) throws Exception {
		if (obfuscate)
			inputName = ObfuscatingBase.obfuscateString(inputName);
		String	outputName		= inputName + outputDecorator;
		Tree	horizontalTree	= new HorizontalTree(context);
		horizontalTree.readExcel(inputName + ".xlsx");
		BufferedImage image = horizontalTree.generate(context, outputName);
		if (TestUtil.isRunningInEclipse())
			showImage(image, outputName);
	}

	public void generateVertical(Context context, String inputName, String outputDecorator) throws Exception {
		if (obfuscate)
			inputName = ObfuscatingBase.obfuscateString(inputName);
		String	outputName		= inputName + outputDecorator;
		Tree	verticalTree	= new VerticalTree(context);
		verticalTree.readExcel(inputName + ".xlsx");
		BufferedImage image = verticalTree.generate(context, outputName);
		if (TestUtil.isRunningInEclipse())
			showImage(image, outputName);
	}

	private void showImage(BufferedImage image, String title) {
		MyCanvas c = new MyCanvas(image);
		c.f.setTitle(title);
		while (c.f.isVisible())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
