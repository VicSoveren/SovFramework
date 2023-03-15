package org.sovframework.contextconfiguration.exception;

public class TooManyDeclarationsException extends ContextConfigurationException {

	public TooManyDeclarationsException(Class<?> type) {
		super("Too many declarations for " + type);
	}
}
