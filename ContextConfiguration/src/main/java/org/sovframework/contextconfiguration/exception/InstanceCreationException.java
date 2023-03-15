package org.sovframework.contextconfiguration.exception;

public class InstanceCreationException extends ContextConfigurationException {

	public InstanceCreationException(Class<?> type, Throwable cause) {
		super("Cannot create instance of type " + type, cause);
	}
}
