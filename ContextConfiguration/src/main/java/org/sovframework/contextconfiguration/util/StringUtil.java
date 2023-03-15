package org.sovframework.contextconfiguration.util;

public class StringUtil {

	private StringUtil() {
	}

	public static String uncapitalize(String value) {
		char[] c = value.toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		return new String(c);
	}

	public static boolean isEmpty(String value) {
		return value == null || value.isBlank();
	}

	public static String toLowerDotCase(String value) {
		return value.toLowerCase().replace("_", ".");
	}
}
