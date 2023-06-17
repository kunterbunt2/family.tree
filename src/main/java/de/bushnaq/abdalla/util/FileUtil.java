package de.bushnaq.abdalla.util;

public class FileUtil {
    public static String extractFileNamePart(String originalName) {
        int slash = originalName.lastIndexOf('/');
        if (slash != -1) {
            return originalName.substring(slash + 1);
        } else {
            return originalName;
        }
    }

    public static String extractFolderNamePart(String originalName) {
        int slash = originalName.lastIndexOf('/');
        if (slash != -1) {
            return originalName.substring(0, slash);
        } else {
            return originalName;
        }
    }

    public static String removeExtension(String originalName) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot != -1) {
            return originalName.substring(0, lastDot);
        } else {
            return originalName;
        }
    }

}
