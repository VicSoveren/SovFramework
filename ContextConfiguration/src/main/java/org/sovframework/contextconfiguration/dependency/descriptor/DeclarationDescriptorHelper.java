package org.sovframework.contextconfiguration.dependency.descriptor;

import org.reflections.Reflections;
import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;
import org.sovframework.contextconfiguration.exception.InvalidDeclarationException;
import org.sovframework.contextconfiguration.util.DeclarationUtil;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DeclarationDescriptorHelper {

	private final Reflections reflections;

	protected DeclarationDescriptorHelper(List<String> packages) {
		reflections = new Reflections(packages);
	}

	protected Optional<Constructor<?>> findEmptyConstructor(List<Constructor<?>> constructors) {
		return constructors.stream()
				.filter(it -> it.getParameters().length == 0)
				.findAny();
	}

	protected Set<DependencyTreeNode> createDependencies(Set<Class<?>> parameters, Class<?> declaration) {
		if (parameters.stream().anyMatch(DeclarationUtil::isInvalid)) {
			throw new InvalidDeclarationException(declaration, "wrong type");
		}

		Set<Class<?>> concreteParameters = parameters.stream()
				.flatMap(parameter -> {
					if (parameter.isInterface()) {
						return reflections.getSubTypesOf(parameter).stream();
					} else {
						return Stream.of(parameter);
					}
				}).collect(Collectors.toSet());

		return concreteParameters.stream()
				.filter(DeclarationUtil::isDeclarable)
				.map(this::createDependency)
				.collect(Collectors.toSet());
	}

	private DependencyTreeNode createDependency(Class<?> type) {
		return new DependencyTreeNode(StringUtil.uncapitalize(type.getSimpleName()), type);
	}
}
