package org.sovframework.contextconfiguration.exception;

public class NoArgsConstructorNotFoundException extends ContextConfigurationException {

	public NoArgsConstructorNotFoundException(Throwable cause) {
		super("Constructor without parameters not found", cause);
	}
}
