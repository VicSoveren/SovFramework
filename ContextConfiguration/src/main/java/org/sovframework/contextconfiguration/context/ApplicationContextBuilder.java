package org.sovframework.contextconfiguration.context;

import org.sovframework.contextconfiguration.dependency.initializer.InstanceInitializer;
import org.sovframework.contextconfiguration.dependency.tree.DependencyTree;
import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;
import org.sovframework.contextconfiguration.exception.ContextCreationException;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContextBuilder {

	private final ApplicationContext context;

	private ApplicationContextBuilder() {
		this.context = new ApplicationContext();
	}

	public static ApplicationContextBuilder create() {
		return new ApplicationContextBuilder();
	}

	public ApplicationContextBuilder from(DependencyTree dependencyTree) {
		Set<DependencyTreeNode> nodes = dependencyTree.getRoots();
		if (nodes.isEmpty()) {
			throw new ContextCreationException("no roots found");
		}

		while (!nodes.isEmpty()) {
			nodes = append(nodes);
		}

		postValidation(dependencyTree);

		return this;
	}

	public ApplicationContext build() {
		return context;
	}

	private Set<DependencyTreeNode> append(Set<DependencyTreeNode> nodes) {
		return nodes.stream()
				.flatMap(node -> append(node).stream())
				.collect(Collectors.toSet());
	}

	private Set<DependencyTreeNode> append(DependencyTreeNode node) {
		InstanceInitializer initializer = node.getInstanceInitializer();
		if (initializer == null) {
			throw new ContextCreationException("initializer is not specified for " + node.getName() + " - " + node.getType());
		}

		if (node.getRoots().stream().anyMatch(root -> context.get(root.getName(), root.getType()).isEmpty())) {
			return Collections.emptySet();
		}

		List<Object> parameters = initializer.getRequiredParameters().stream()
				.map(this::getParameterValue)
				.collect(Collectors.toList());

		Object value = initializer.apply(parameters);

		context.add(node.getName(), node.getType(), value);

		return node.getBranches();
	}

	private Object getParameterValue(Parameter parameter) {
		Optional<Instance> instance = context.get(StringUtil.uncapitalize(parameter.getName()), parameter.getType());
		if (instance.isPresent()) {
			return instance.get().getValue();
		} else {
			Set<Instance> instances = context.get(parameter.getType());
			if (instances.size() == 0) {
				throw new ContextCreationException("No Instances of " + parameter.getType() + " found");
			} else if (instances.size() > 1) {
				throw new ContextCreationException("More than one Instance of " + parameter.getType() + " found");
			} else {
				return instances.stream().findAny().get().getValue();
			}
		}
	}

	private void postValidation(DependencyTree dependencyTree) {
		Set<DependencyTreeNode> nodes = dependencyTree.getRoots();

		while (!nodes.isEmpty()) {
			if (nodes.stream().anyMatch(node -> context.get(node.getName(), node.getType()).isEmpty())) {
				throw new ContextCreationException("Context is not created");
			}
			nodes = nodes.stream().flatMap(node -> node.getBranches().stream()).collect(Collectors.toSet());
		}
	}
}
