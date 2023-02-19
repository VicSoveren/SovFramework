package org.sovframework.contextconfiguration.exception;

public class NotInstanceException extends ContextConfigurationException {

	public NotInstanceException(Class<?> type) {
		super(type + " is not an Instance");
	}
}
