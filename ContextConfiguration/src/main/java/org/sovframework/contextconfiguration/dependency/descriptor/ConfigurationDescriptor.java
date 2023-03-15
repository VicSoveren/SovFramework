package org.sovframework.contextconfiguration.dependency.descriptor;

import org.sovframework.contextconfiguration.annotation.Declared;
import org.sovframework.contextconfiguration.dependency.initializer.MethodInstanceInitializer;
import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;
import org.sovframework.contextconfiguration.exception.EmptyConstructorNotFoundException;
import org.sovframework.contextconfiguration.exception.InstanceCreationException;
import org.sovframework.contextconfiguration.exception.InvalidDeclarationException;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationDescriptor extends DeclarationDescriptorHelper implements DeclarationDescriptor {

	public ConfigurationDescriptor(List<String> packages) {
		super(packages);
	}

	@Override
	public Set<DependencyTreeNode> describe(Class<?> declaration) {
		Set<Method> declared = findDeclared(Arrays.asList(declaration.getMethods()));
		if (declared.isEmpty()) {
			return Collections.emptySet();
		}

		if (declared.stream().anyMatch(it -> it.getReturnType().equals(Void.TYPE))) {
			throw new InvalidDeclarationException(declaration, "void return type is not allowed");
		}

		Optional<Constructor<?>> emptyConstructor = findEmptyConstructor(Arrays.asList(declaration.getConstructors()));
		if (emptyConstructor.isEmpty()) {
			throw new EmptyConstructorNotFoundException(declaration);
		}

		Object instance;
		try {
			instance = emptyConstructor.get().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new InstanceCreationException(declaration, e);
		}

		return declared.stream()
				.map(it -> new DependencyTreeNode(StringUtil.uncapitalize(it.getName()),
						it.getReturnType())
						.withRoots(createDependencies(Set.of(it.getParameterTypes()), it.getReturnType()))
						.withInitializer(new MethodInstanceInitializer(it, instance)))
				.collect(Collectors.toSet());
	}

	private Set<Method> findDeclared(List<Method> methods) {
		return methods.stream()
				.filter(method -> method.isAnnotationPresent(Declared.class))
				.collect(Collectors.toSet());
	}
}
