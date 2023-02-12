package org.sovframework.contextconfiguration.exception;

import java.lang.reflect.Method;

public class MethodInvocationException extends ContextConfigurationException {

	public MethodInvocationException(Method method, Throwable cause) {
		super("Failed to invoke method " + method, cause);
	}
}
