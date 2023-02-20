package org.sovframework.contextconfiguration.dependency;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.sovframework.contextconfiguration.annotation.Instance;
import org.sovframework.contextconfiguration.exception.NotInstanceException;
import org.sovframework.contextconfiguration.exception.TooManyConstructorsException;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyTreeBuilder {

	public DependencyTree build(List<String> packages) {
		DependencyTree dependencyTree = new DependencyTree();

		Set<Class<?>> instances = packages.stream()
				.flatMap(it -> getAllClassesInPackage(it).stream())
				.filter(this::isInstance)
				.collect(Collectors.toSet());

		instances.forEach(instance -> dependencyTree.add(
				StringUtil.uncapitalize(instance.getSimpleName()),
				instance,
				getDependencies(instance)
		));

		return dependencyTree;
	}

	private Set<Class<?>> getAllClassesInPackage(String packageName) {
		return new Reflections(packageName, Scanners.SubTypes.filterResultsBy(s -> true))
				.getSubTypesOf(Object.class);
	}

	private Boolean isInstance(Class<?> clazz) {
		if (clazz.isAnnotation()) {
			return false;
		}

		if (clazz.isAnnotationPresent(Instance.class)) {
			return true;
		}

		return Arrays.stream(clazz.getAnnotations())
				.map(Annotation::annotationType)
				.anyMatch(annotationType -> annotationType.isAnnotationPresent(Instance.class));
	}

	private Map<String, Class<?>> getDependencies(Class<?> instance) {
		Constructor<?>[] constructors = instance.getConstructors();
		if (constructors.length > 1) {
			throw new TooManyConstructorsException(instance);
		}
		Constructor<?> constructor = constructors[0];

		List<Class<?>> parameterTypes = List.of(constructor.getParameterTypes());
		parameterTypes.stream()
				.filter(parameter -> !isInstance(parameter))
				.findAny()
				.ifPresent(parameter -> {
					throw new NotInstanceException(parameter);
				});

		return parameterTypes.stream()
				.map(it -> Map.entry(StringUtil.uncapitalize(it.getSimpleName()), it))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
