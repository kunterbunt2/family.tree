package de.bushnaq.abdalla.theme;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ThemedImageIcon extends ImageIcon {
    private static final long serialVersionUID = -8721258530978506860L;
    private static Boolean darkLaf;
    private final Class<?> clazz;
    private final String name;
    private boolean dark;

    public ThemedImageIcon(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    private static boolean isDarkLaf() {
        if (darkLaf == null) {
            lafChanged();

            UIManager.addPropertyChangeListener(e -> {
                lafChanged();
            });
        }

        return darkLaf;
    }

    private static void lafChanged() {
        darkLaf = FlatLaf.isLafDark();
    }

    @Override
    public int getIconHeight() {
        update();
        return super.getIconHeight();
    }

    private URL getIconURL(String name, boolean dark) {
        if (dark) {
            int dotIndex = name.lastIndexOf('.');
            name = name.substring(0, dotIndex) + "_dark" + name.substring(dotIndex);
        }
        return clazz.getClassLoader().getResource(name);
    }

    @Override
    public int getIconWidth() {
        update();
        return super.getIconWidth();
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        update();
        super.paintIcon(c, g, x, y);
    }

    private void update() {
        if (dark == isDarkLaf() /* && diagram != null */) {
            return;
        }

        dark = isDarkLaf();
        URL url = getIconURL(name, dark);
        if (url == null & dark) {
            url = getIconURL(name, false);
        }
        this.setImage(Toolkit.getDefaultToolkit().getImage(url));
    }
}
