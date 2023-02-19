package org.sovframework.contextconfiguration.initializer;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.sovframework.contextconfiguration.annotation.Instance;
import org.sovframework.contextconfiguration.context.ApplicationContext;
import org.sovframework.contextconfiguration.dependency.DependencyTree;
import org.sovframework.contextconfiguration.dependency.DependencyTreeNode;
import org.sovframework.contextconfiguration.exception.InstanceCreationException;
import org.sovframework.contextconfiguration.exception.NotInstanceException;
import org.sovframework.contextconfiguration.exception.TooManyConstructorsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContextInitializer {

	private final ApplicationContext context;

	public ApplicationContextInitializer(List<String> packages) {
		context = new ApplicationContext();

		Set<Class<?>> instances = packages.stream()
				.flatMap(it -> getAllClassesInPackage(it).stream())
				.filter(this::isInstance)
				.collect(Collectors.toSet());

		DependencyTree dependencyTree = new DependencyTree();
		instances.forEach(instance -> dependencyTree.add(
				instance.getSimpleName(),
				instance,
				getDependencies(instance)
		));

		Set<DependencyTreeNode> nodes = dependencyTree.getRoots();
		while (!nodes.isEmpty()) {
			nodes = appendContext(nodes, dependencyTree);
		}

		dependencyTree.stream().findAny()
				.ifPresent(it -> {
					throw new InstanceCreationException(it.getType());
				});
	}

	private Set<Class<?>> getAllClassesInPackage(String packageName) {
		return new Reflections(packageName, Scanners.SubTypes.filterResultsBy(s -> true))
				.getSubTypesOf(Object.class);
	}

	private Boolean isInstance(Class<?> clazz) {
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
				.map(it -> Map.entry(it.getSimpleName(), it))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Set<DependencyTreeNode> appendContext(Set<DependencyTreeNode> nodes, DependencyTree dependencyTree) {
		return nodes.stream()
				.flatMap(node -> appendContext(node, dependencyTree).stream())
				.collect(Collectors.toSet());
	}

	private Set<DependencyTreeNode> appendContext(DependencyTreeNode node, DependencyTree dependencyTree) {
		if (context.get(node.getName(), node.getType()) == null) {
			Constructor<?>[] constructors = node.getType().getConstructors();
			if (constructors.length > 1) {
				throw new TooManyConstructorsException(node.getType());
			}
			Constructor<?> constructor = constructors[0];

			List<Object> parameters = List.of(constructor.getParameterTypes()).stream()
					.map(type -> context.get(type.getSimpleName(), type))
					.collect(Collectors.toList());

			if (parameters.stream().anyMatch(Objects::isNull)) {
				return new HashSet<>();
			}

			Object instance;
			try {
				instance = constructor.newInstance(parameters.toArray());
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
				throw new InstanceCreationException(node.getType(), e);
			}

			context.put(node.getName(), node.getType(), instance);
		}

		dependencyTree.remove(node);

		return node.getDependentNodes();
	}

	public ApplicationContext getContext() {
		return context;
	}
}
