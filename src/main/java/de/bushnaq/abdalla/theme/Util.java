package de.bushnaq.abdalla.theme;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Util {
    private static Boolean darkLaf = true;
    // private static Logger logger = LoggerFactory.getLogger(Util.class);

    public static List<Image> createThemedWindowIconImages(String svgName) {
        boolean dark = isDarkLaf();
        if (dark) {
            int dotIndex = svgName.lastIndexOf('.');
            svgName = svgName.substring(0, dotIndex) + "_dark" + svgName.substring(dotIndex);
        }
        return FlatSVGUtils.createWindowIconImages(svgName);
    }

    public static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        } else {
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            icon.paintIcon(null, g2, 0, 0);
            return image;
        }
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

}
