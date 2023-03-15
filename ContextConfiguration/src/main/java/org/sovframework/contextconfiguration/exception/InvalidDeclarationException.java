package org.sovframework.contextconfiguration.exception;

public class InvalidDeclarationException extends ContextConfigurationException {

	public InvalidDeclarationException(Class<?> type, String verboseMessage) {
		super("Invalid declaration " + type + " - " + verboseMessage);
	}
}
