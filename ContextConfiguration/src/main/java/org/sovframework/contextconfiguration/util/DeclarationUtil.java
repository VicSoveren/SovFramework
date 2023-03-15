package org.sovframework.contextconfiguration.util;

import org.sovframework.contextconfiguration.annotation.Declaration;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class DeclarationUtil {

	private DeclarationUtil() {
	}

	public static Boolean isInvalid(Class<?> type) {
		return type.isAnnotation() || type.isEnum() || type.isPrimitive();
	}

	public static Boolean isDeclarable(Class<?> type) {
		return !isInvalid(type) && !type.isInterface();
	}

	public static Boolean isDeclaration(Class<?> type) {
		if (!isDeclarable(type)) {
			return false;
		}

		if (type.isAnnotationPresent(Declaration.class)) {
			return true;
		}

		return Arrays.stream(type.getAnnotations())
				.map(Annotation::annotationType)
				.anyMatch(annotationType -> annotationType.isAnnotationPresent(Declaration.class));
	}
}
