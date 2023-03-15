package org.sovframework.contextconfiguration.exception;

public class EmptyConstructorNotFoundException extends ContextConfigurationException {

	public EmptyConstructorNotFoundException(Class<?> type) {
		super("Empty constructor not found for " + type);
	}
}
