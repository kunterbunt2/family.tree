package de.bushnaq.abdalla.family.tree.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MyCanvas extends Canvas {

    public final JFrame f;
//	final Logger	logger	= LoggerFactory.getLogger(this.getClass());

    public MyCanvas(BufferedImage image) {
        f = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        f.add(panel, BorderLayout.CENTER);

        ImageComponent imagePanel = new ImageComponent(image);
        JScrollPane scrollPanel = new JScrollPane(imagePanel);
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPanel, BorderLayout.CENTER);

        f.add(panel, BorderLayout.CENTER);
        f.setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = Math.min(image.getHeight() + f.getInsets().top + f.getInsets().bottom, (int) (screenSize.height * 0.8));
        int w = Math.min(image.getWidth() + f.getInsets().left + f.getInsets().right, (int) (screenSize.width * 0.8));
        f.setSize(new Dimension(w, h));
    }

}