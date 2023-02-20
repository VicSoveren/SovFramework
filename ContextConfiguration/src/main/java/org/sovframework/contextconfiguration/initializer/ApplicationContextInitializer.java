package org.sovframework.contextconfiguration.initializer;

import org.sovframework.contextconfiguration.context.ApplicationContext;
import org.sovframework.contextconfiguration.dependency.DependencyTree;
import org.sovframework.contextconfiguration.dependency.DependencyTreeBuilder;
import org.sovframework.contextconfiguration.dependency.DependencyTreeNode;
import org.sovframework.contextconfiguration.exception.InstanceCreationException;
import org.sovframework.contextconfiguration.exception.TooManyConstructorsException;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContextInitializer {

	private final ApplicationContext context;

	public ApplicationContextInitializer(List<String> packages) {
		context = new ApplicationContext();

		DependencyTree dependencyTree = new DependencyTreeBuilder().build(packages);
		Set<DependencyTreeNode> nodes = dependencyTree.getRoots();
		while (!nodes.isEmpty()) {
			nodes = appendContext(nodes, dependencyTree);
		}

		dependencyTree.stream().findAny()
				.ifPresent(it -> {
					throw new InstanceCreationException(it.getType());
				});
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
					.map(type -> context.get(StringUtil.uncapitalize(type.getSimpleName()), type))
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
