package de.bushnaq.abdalla.family.tree.ui;

import javax.swing.*;
import java.awt.*;

public class ImageComponent extends JComponent {
    final Image image;
    final Dimension size;

    public ImageComponent(Image image) {
        this.image = image;
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException e) {
            // error ...
        }

        size = new Dimension(image.getWidth(null), image.getHeight(null));
        setSize(size);
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
}