package org.sovframework.contextconfiguration.dependency.initializer;

import org.sovframework.contextconfiguration.exception.InstanceCreationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class ConstructorInstanceInitializer implements InstanceInitializer {

	private final Constructor<?> constructor;
	private final Class<?> declaration;

	public ConstructorInstanceInitializer(Constructor<?> constructor, Class<?> declaration) {
		this.constructor = constructor;
		this.declaration = declaration;
	}

	@Override
	public Object apply(List<Object> parameters) {
		try {
			if (parameters.isEmpty()) {
				return constructor.newInstance();
			}
			return constructor.newInstance(parameters.toArray(new Object[0]));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new InstanceCreationException(declaration, e);
		}
	}

	@Override
	public List<Parameter> getRequiredParameters() {
		return Arrays.asList(constructor.getParameters());
	}
}
