package de.bushnaq.abdalla.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MavenPropertiesProvider {
    // private static final Logger logger = LoggerFactory.getLogger(MavenProperiesProvider.class.getName());

    @SuppressWarnings("unused")
    private static final MavenPropertiesProvider INSTANCE = new MavenPropertiesProvider();
    static ResourceBundle rb;

    private MavenPropertiesProvider() {
        // try {
        // rb = ResourceBundle.getBundle("maven");
        // } catch (MissingResourceException e) {
        // logger.warn("Resource bundle 'maven' was not found or error while reading current version.");
        // }
    }

    public static String getProperty(Class<?> clazz, String name) {
        ResourceBundle bundle = ResourceBundle.getBundle("maven", Locale.getDefault(), clazz.getClassLoader());
        return bundle.getString(name);

        // return INSTANCE.rb.getString(name);
    }
}
