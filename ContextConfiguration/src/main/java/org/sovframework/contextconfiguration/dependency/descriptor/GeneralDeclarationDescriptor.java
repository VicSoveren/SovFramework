package org.sovframework.contextconfiguration.dependency.descriptor;

import org.sovframework.contextconfiguration.annotation.Configuration;
import org.sovframework.contextconfiguration.annotation.Service;
import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;

import java.util.List;
import java.util.Set;

public class GeneralDeclarationDescriptor implements DeclarationDescriptor {

	private final DeclarationDescriptor serviceDescriptor;

	private final DeclarationDescriptor configurationDescriptor;

	public GeneralDeclarationDescriptor(List<String> packages) {
		this.serviceDescriptor = new ServiceDescriptor(packages);
		this.configurationDescriptor = new ConfigurationDescriptor(packages);
	}

	@Override
	public Set<DependencyTreeNode> describe(Class<?> declaration) {
		if (declaration.isAnnotationPresent(Configuration.class)) {
			return configurationDescriptor.describe(declaration);
		} else if (declaration.isAnnotationPresent(Service.class)) {
			return serviceDescriptor.describe(declaration);
		} else {
			return serviceDescriptor.describe(declaration);
		}
	}
}
