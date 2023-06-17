package de.bushnaq.abdalla.util;

import java.util.Random;

public class ObfuscatingBase {
    private static final int RANDOM_SEED = 1;
    Random generator = new Random(RANDOM_SEED);

    char[] lowerCaseVowels = {'a', 'e', 'i', 'o', 'u'};

    char[] upperCaseVowels = {'A', 'E', 'I', 'O', 'U'};

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

    public String obfuscateString(String text) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = obfuscateCharacter(text.charAt(i));
            sb.append(c);
        }
        return sb.toString();
    }

    public void reseed() {
        generator.setSeed(RANDOM_SEED);
    }

}
