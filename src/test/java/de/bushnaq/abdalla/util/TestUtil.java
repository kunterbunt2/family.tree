package de.bushnaq.abdalla.util;

public class TestUtil {
    //	public static boolean isRunningInEclipse() {
//		String	path		= System.getProperty("java.class.path").toLowerCase();
//		boolean	isEclipse	= path.contains(".m2");
//		return isEclipse;
//	}
    public static boolean isRunningInEclipse() {
        boolean isEclipse = System.getProperty("java.class.path").toLowerCase().contains("eclipse");
        return isEclipse;
    }
}
