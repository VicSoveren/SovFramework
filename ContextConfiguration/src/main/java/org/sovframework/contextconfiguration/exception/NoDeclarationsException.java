package org.sovframework.contextconfiguration.exception;

public class NoDeclarationsException extends ContextConfigurationException {

	public NoDeclarationsException(Class<?> type) {
		super(type + " must have at least one declaration");
	}
}
