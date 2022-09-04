package de.bushnaq.abdalla.theme;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;

public class ThemedImageIcon extends ImageIcon {
	private static Boolean		darkLaf;

	private static final long	serialVersionUID	= -8721258530978506860L;

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

	private Class<?>	clazz;

	private boolean		dark;

	private String		name;

	public ThemedImageIcon(Class<?> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
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
		if (dark == isDarkLaf() /* && diagram != null */ ) {
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
