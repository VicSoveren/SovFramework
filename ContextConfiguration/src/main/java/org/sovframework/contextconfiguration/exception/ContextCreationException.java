package org.sovframework.contextconfiguration.exception;

public class ContextCreationException extends ContextConfigurationException {

	public ContextCreationException(String verboseMessage) {
		super("Error during context creation " + " - " + verboseMessage);
	}
}
