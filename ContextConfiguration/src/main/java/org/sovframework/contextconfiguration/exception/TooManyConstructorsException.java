package org.sovframework.contextconfiguration.exception;

public class TooManyConstructorsException extends ContextConfigurationException {

	public TooManyConstructorsException(Class<?> type) {
		super("Too many constructors found for " + type);
	}
}
