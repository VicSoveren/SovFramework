package org.sovframework.contextconfiguration.context;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.sovframework.contextconfiguration.annotation.Factory;
import org.sovframework.contextconfiguration.annotation.Instance;
import org.sovframework.contextconfiguration.exception.InstanceCreationException;
import org.sovframework.contextconfiguration.exception.MethodInvocationException;
import org.sovframework.contextconfiguration.exception.NoArgsConstructorNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext {

	private final Map<Class<?>, Object> context;

	public ApplicationContext(List<String> packages) {
		context = packages.stream()
				.flatMap(it -> getAllClassesInPackage(it).stream())
				.filter(it -> Arrays.stream(it.getAnnotations())
						.map(Annotation::annotationType)
						.anyMatch(annotationType -> annotationType.isAnnotationPresent(Instance.class)))
				.map(this::createInstanceDto)
				.filter(Objects::nonNull)
				.flatMap(it -> it.methods.stream()
						.map(method -> Map.entry(method.getReturnType(), invokeMethod(method, it.instance)))
				)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public <T> Object getInstance(Class<T> type) {
		return context.get(type);
	}

	private Set<Class<?>> getAllClassesInPackage(String packageName) {
		return new Reflections(packageName, Scanners.SubTypes.filterResultsBy(s -> true))
				.getSubTypesOf(Object.class);
	}

	private InstanceDto createInstanceDto(Class<?> clazz) {
		Set<Method> factories = Arrays.stream(clazz.getMethods())
				.filter(method -> method.isAnnotationPresent(Factory.class))
				.collect(Collectors.toSet());

		if (factories.isEmpty()) {
			return null;
		}

		return new InstanceDto(
				createInstance(clazz),
				factories
		);
	}

	private Object createInstance(Class<?> type) {
		try {
			Constructor<?> constructor = type.getConstructor();
			return constructor.newInstance();
		} catch (NoSuchMethodException e) {
			throw new NoArgsConstructorNotFoundException(e);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new InstanceCreationException(type, e);
		}
	}

	private Object invokeMethod(Method method, Object instance) {
		try {
			return method.invoke(instance);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new MethodInvocationException(method, e);
		}
	}

	private static class InstanceDto {

		private final Object instance;

		private final Set<Method> methods;

		private InstanceDto(Object instance, Set<Method> methods) {
			this.instance = instance;
			this.methods = methods;
		}
	}
}
