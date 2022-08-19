package de.bushnaq.abdalla.family.tree.util;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCanvas extends Canvas {

	JFrame					f;
//	JPanel p;
	private BufferedImage	image;
	final Logger			logger	= LoggerFactory.getLogger(this.getClass());

	public MyCanvas(BufferedImage image) {
		this.image = image;
		f = new JFrame();
//		p = new JPanel();
//		f.add(p);
		f.add(this);
//		f.setPreferredSize(new Dimension(image.getWidth(), h));
//		f.setMaximumSize(new Dimension(image.getWidth(), h));
//		f.setMinimumSize(new Dimension(image.getWidth(), h));
		f.setVisible(true);
		int	h	= image.getHeight() + f.getInsets().top + f.getInsets().bottom;
		int	w	= image.getWidth() + f.getInsets().left + f.getInsets().right;
		f.setSize(new Dimension(w, h));
		logger.info(String.format("%d %d %d", image.getHeight(), f.getInsets().top + f.getInsets().bottom, f.getHeight()));
		logger.info(String.format("%d %d %d", image.getWidth(), f.getInsets().left + f.getInsets().right, f.getWidth()));
	}

	@Override
	public void paint(Graphics g) {

//		Toolkit t = Toolkit.getDefaultToolkit();
//		Image i = t.getImage("p3.gif");
		g.drawImage(image, 0, 0, this);

	}

}