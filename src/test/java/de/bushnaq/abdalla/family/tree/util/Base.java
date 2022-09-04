package de.bushnaq.abdalla.family.tree.util;

import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.Main;
import de.bushnaq.abdalla.family.tree.ui.MyCanvas;
import de.bushnaq.abdalla.util.TestUtil;

public class Base {
	static {
		System.setProperty("java.awt.headless", "false");
	}
	@Autowired
	Context	context;
	@Autowired
	Main	main;

	public void generate(String[] args) throws Exception {
		BufferedImage	image			= main.start(args);
		String			inputName		= context.getParameterOptions().getInput();
		String			outputDecorator	= context.getParameterOptions().getOutputDecorator();
		String			outputName		= inputName + outputDecorator;
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
