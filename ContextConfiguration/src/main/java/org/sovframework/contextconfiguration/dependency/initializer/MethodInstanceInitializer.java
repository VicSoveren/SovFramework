package org.sovframework.contextconfiguration.dependency.initializer;

import org.sovframework.contextconfiguration.exception.InstanceCreationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class MethodInstanceInitializer implements InstanceInitializer {

	private final Method method;
	private final Object instance;

	public MethodInstanceInitializer(Method method, Object instance) {
		this.method = method;
		this.instance = instance;
	}

	@Override
	public Object apply(List<Object> parameters) {
		try {
			if (parameters.isEmpty()) {
				return method.invoke(instance);
			}
			return method.invoke(instance, parameters.toArray(new Object[0]));
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new InstanceCreationException(method.getReturnType(), e);
		}
	}

	@Override
	public List<Parameter> getRequiredParameters() {
		return Arrays.asList(method.getParameters());
	}
}
