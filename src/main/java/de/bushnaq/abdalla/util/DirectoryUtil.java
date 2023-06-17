package de.bushnaq.abdalla.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class DirectoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryUtil.class);

    public static void createDirectory(String folderName) throws Exception {

        boolean accessDenied = false;
        int retries = 5;
        Path path = Paths.get(folderName);
        do {
            try {
                accessDenied = false;
                // Util.logger.trace(String.format("[+]creating %s", path.toAbsolutePath()));
                if (!new File(folderName).exists()) {
                    Files.createDirectories(path);
                }
            } catch (AccessDeniedException e) {
                logger.warn(String.format("Retrying (%d/5) to create %s", retries, path.toAbsolutePath()));
                accessDenied = true;
                Thread.sleep(300);
                if (retries-- <= 0) {
                    throw new Exception(e);
                }
            }
        } while (accessDenied);
    }

    private static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        logger.trace(String.format("[X]deleting %s", path.toAbsolutePath()));
        Files.delete(path);
    }

    public static void removeDirectory(String folderName) throws Exception {
        File file = new File(folderName);
        boolean accessDenied = false;
        int retries = 5;
        Path path = Paths.get(folderName);
        do {
            try {
                accessDenied = false;
                if (file.exists()) {
                    deleteDirectoryRecursion(Paths.get(folderName));
                }
            } catch (java.nio.file.DirectoryNotEmptyException | java.nio.file.AccessDeniedException e) {
                logger.warn(String.format("Retrying (%d/5) to delete %s", retries, path.toAbsolutePath()));
                accessDenied = true;
                Thread.sleep(300);
                if (retries-- <= 0) {
                    throw new Exception(e);
                }
            }
        } while (accessDenied);
    }

}
