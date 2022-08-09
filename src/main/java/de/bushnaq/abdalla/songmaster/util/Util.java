package de.bushnaq.abdalla.songmaster.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
	static public String cleanupSongName(String songName) {
		songName = songName.replace('_', ' ');
		return songName;
	}

	public static String formatDuration(long seconds) {
		// long seconds = duration.getSeconds();
		long	absSeconds	= Math.abs(seconds);
		String	positive;
		if (absSeconds / 3600 != 0)
			positive = String.format("%d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
		else
			positive = String.format("%2d:%02d", (absSeconds % 3600) / 60, absSeconds % 60);

		return seconds < 0 ? "-" + positive : positive;
	}

	public static String formatDurationWithUnits(long seconds) {
		// long seconds = duration.getSeconds();
		long	absSeconds	= Math.abs(seconds);
		String	positive;
		if (absSeconds / 3600 != 0)
			positive = String.format("%dh:%02dm:%02ds", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
		else
			positive = String.format("%2dm:%02ds", (absSeconds % 3600) / 60, absSeconds % 60);

		return seconds < 0 ? "-" + positive : positive;
	}

	public static boolean isOverlapping(long os, long of, boolean overlapping, long ps, long pf) {
		// o | |
		// p | |
		if (os >= ps && os <= pf) {
			overlapping = true;
		}
		// o| |
		// p | |
		if (of >= ps && of <= pf) {
			overlapping = true;
		}
		// o| |
		// p | |
		if (os <= ps && of >= pf) {
			overlapping = true;
		}
		// o | |
		// p | |
		if (os > ps && of < pf) {
			overlapping = true;
		}
		return overlapping;
	}

	static public String prepareAlbumName(String albumName) {
		albumName = albumName.replace(' ', '_');
		albumName = albumName.replace("\'", "_");
		return albumName;
	}

	static public String prepareSongName(String songName) {
		songName = songName.replace(' ', '_');
		songName = songName.replace("\'", "_");
		return songName;
	}

	public static File[] queryAllFiles(String directory, String extension) {
		File[] files = null;
		try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

			List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(extension)).collect(Collectors.toList());

			// result.forEach(System.out::println);
			files = new File[result.size()];
			int i = 0;
			for (String fileName : result) {
				files[i++] = new File(fileName);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;

	}

	static public String removeExtension(String absoluteFile) {
		return absoluteFile.substring(0, absoluteFile.lastIndexOf('.'));
	}

}
