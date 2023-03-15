package org.sovframework.contextconfiguration.dependency.descriptor;

import org.sovframework.contextconfiguration.annotation.Declared;
import org.sovframework.contextconfiguration.dependency.initializer.ConstructorInstanceInitializer;
import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;
import org.sovframework.contextconfiguration.exception.NoDeclarationsException;
import org.sovframework.contextconfiguration.exception.TooManyDeclarationsException;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceDescriptor extends DeclarationDescriptorHelper implements DeclarationDescriptor {

	public ServiceDescriptor(List<String> packages) {
		super(packages);
	}

	@Override
	public Set<DependencyTreeNode> describe(Class<?> declaration) {
		Constructor<?> constructor = findConstructor(Arrays.asList(declaration.getConstructors()), declaration);

		DependencyTreeNode dependencyTreeNode = new DependencyTreeNode(StringUtil.uncapitalize(declaration.getSimpleName()),
				declaration)
				.withRoots(createDependencies(Set.of(constructor.getParameterTypes()), declaration))
				.withInitializer(new ConstructorInstanceInitializer(constructor, declaration));

		return Collections.singleton(dependencyTreeNode);
	}

	private Constructor<?> findConstructor(List<Constructor<?>> constructors, Class<?> declaration) {
		if (constructors.size() == 1) {
			return constructors.get(0);
		}

		Optional<Constructor<?>> emptyConstructor = findEmptyConstructor(constructors);
		if (emptyConstructor.isPresent()) {
			return emptyConstructor.get();
		}

		Set<Constructor<?>> declared = findDeclared(constructors);
		if (declared.isEmpty()) {
			throw new NoDeclarationsException(declaration);
		} else if (declared.size() > 1) {
			throw new TooManyDeclarationsException(declaration);
		} else {
			return declared.stream().findAny().get();
		}
	}

	private Set<Constructor<?>> findDeclared(List<Constructor<?>> constructors) {
		return constructors.stream()
				.filter(constructor -> constructor.isAnnotationPresent(Declared.class))
				.collect(Collectors.toSet());
	}
}
