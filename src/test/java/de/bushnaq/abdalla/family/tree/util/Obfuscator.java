package de.bushnaq.abdalla.family.tree.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * obfuscates strings ensuring they always end up with the same value
 * strings that have spaces in them are separately obfuscated and put together again.
 */
public class Obfuscator {
    private static final int RANDOM_SEED = 1;
    final Random generator = new Random(RANDOM_SEED);

    final char[] lowerCaseVowels = {'a', 'e', 'i', 'o', 'u'};

    final char[] upperCaseVowels = {'A', 'E', 'I', 'O', 'U'};
    private Map<String, String> dictionary = new HashMap<>();

    /**
     * copies upper/lower character information from text to obfuscated text
     * this ensures that strings are obfuscated independent of their case
     *
     * @param text
     * @param obfuscated
     * @return
     */
    private String cloneCases(String text, String obfuscated) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                sb.append(Character.toUpperCase(obfuscated.charAt(i)));
            } else {
                sb.append(obfuscated.charAt(i));
            }
        }
        return sb.toString();
    }

    private char generateRandomLowerCaseConsonant() {
        char c;
        do {
            c = 'a';
            c += generator.nextInt(26);
        } while (!isConsonant(c));
        return c;
    }

    private char generateRandomLowerCaseVowel() {
        char c;
        do {
            c = 'a';
            c += generator.nextInt(26);
        } while (!isVowel(c));
        return c;
    }

    private char generateRandomUpperCaseConsonant() {
        char c;
        do {
            c = 'A';
            c += generator.nextInt(26);
        } while (!isConsonant(c));
        return c;
    }

    private char generateRandomUpperCaseVowel() {
        char c;
        do {
            c = 'A';
            c += generator.nextInt(26);
        } while (!isVowel(c));
        return c;
    }

    boolean isConsonant(char c) {
        if (Character.isUpperCase(c)) {
            for (char v : upperCaseVowels) {
                if (c == v)
                    return false;
            }
            return true;
        } else if (Character.isLowerCase(c)) {
            for (char v : lowerCaseVowels) {
                if (c == v)
                    return false;
            }
            return true;
        } else if (Character.isDigit(c)) {
            return false;
        }
        return false;
    }

    boolean isVowel(char c) {
        if (Character.isUpperCase(c)) {
            for (char v : upperCaseVowels) {
                if (c == v)
                    return true;
            }
//			if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
//				return true;
//			else
            return false;
        } else if (Character.isLowerCase(c)) {
            for (char v : lowerCaseVowels) {
                if (c == v)
                    return true;
            }
//			if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u')
//				return true;
//			else
            return false;
        } else if (Character.isDigit(c)) {
            return false;
        }
        return false;
    }

    private char obfuscateCharacter(char c) {
        if (Character.isUpperCase(c)) {
            if (isVowel(c))
                c = generateRandomUpperCaseVowel();
            else
                c = generateRandomUpperCaseConsonant();
        } else if (Character.isLowerCase(c)) {
            if (isVowel(c))
                c = generateRandomLowerCaseVowel();
            else
                c = generateRandomLowerCaseConsonant();
        } else if (Character.isDigit(c)) {
            c = '0';
            c += generator.nextInt(10);
        }
        return c;
    }

    public String obfuscateString(String text) {
        String[] list = text.split(" ");
        StringBuffer sb = new StringBuffer();
        for (String s : list) {
            if (!sb.isEmpty())
                sb.append(" ");
            sb.append(obfuscateStringWith0utSpaces(s));
        }
        return sb.toString();
    }

    private String obfuscateStringWith0utSpaces(String text) {
        String lowerCaseText = text.toLowerCase();
        String obfuscated = dictionary.get(lowerCaseText);
        if (obfuscated != null)
            return cloneCases(text, obfuscated);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lowerCaseText.length(); i++) {
            char c = obfuscateCharacter(lowerCaseText.charAt(i));
            sb.append(c);
        }
        dictionary.put(lowerCaseText, sb.toString().toLowerCase());
        return sb.toString();
    }

    public void reseed() {
        generator.setSeed(RANDOM_SEED);
    }

}
