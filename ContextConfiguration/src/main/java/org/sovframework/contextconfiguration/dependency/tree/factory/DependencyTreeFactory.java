package org.sovframework.contextconfiguration.dependency.tree.factory;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.sovframework.contextconfiguration.dependency.descriptor.DeclarationDescriptor;
import org.sovframework.contextconfiguration.dependency.descriptor.GeneralDeclarationDescriptor;
import org.sovframework.contextconfiguration.dependency.tree.DependencyTree;
import org.sovframework.contextconfiguration.util.DeclarationUtil;

import java.util.List;
import java.util.Set;

public class DependencyTreeFactory {

	private final DeclarationDescriptor declarationDescriptor;

	private final List<String> packages;

	public DependencyTreeFactory(List<String> packages) {
		this.declarationDescriptor = new GeneralDeclarationDescriptor(packages);
		this.packages = packages;
	}

	public DependencyTree create() {
		DependencyTree dependencyTree = new DependencyTree();

		packages.stream()
				.flatMap(it -> getAllClassesInPackage(it).stream())
				.filter(DeclarationUtil::isDeclaration)
				.flatMap(declaration -> declarationDescriptor.describe(declaration).stream())
				.forEach(dependencyTree::put);

		return dependencyTree;
	}

	private Set<Class<?>> getAllClassesInPackage(String packageName) {
		return new Reflections(packageName, Scanners.SubTypes.filterResultsBy(s -> true))
				.getSubTypesOf(Object.class);
	}
}
