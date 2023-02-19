package org.sovframework.contextconfiguration.exception;

public class ContextConfigurationException extends RuntimeException {

	public ContextConfigurationException(String message) {
		super(message);
	}

	public ContextConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
