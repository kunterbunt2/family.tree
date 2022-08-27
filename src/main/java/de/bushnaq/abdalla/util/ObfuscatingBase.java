package de.bushnaq.abdalla.util;

import java.util.Random;

public class ObfuscatingBase {
	private static final int	RANDOM_SEED	= 1;
	Random						generator	= new Random(RANDOM_SEED);

	public String obfuscateString(String text) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = obfuscateCharacter(text.charAt(i));
			sb.append(c);
		}
		return sb.toString();
	}

	private char obfuscateCharacter(char c) {
		if (Character.isUpperCase(c)) {
			c = 'A';
			c += generator.nextInt(26);
		} else if (Character.isLowerCase(c)) {
			c = 'a';
			c += generator.nextInt(26);
		} else if (Character.isDigit(c)) {
			c = '0';
			c += generator.nextInt(10);
		}
		return c;
	}

	public void reseed() {
		generator.setSeed(RANDOM_SEED);
	}

}
