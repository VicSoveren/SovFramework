package org.sovframework.contextconfiguration.dependency.descriptor;

import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;

import java.util.Set;

public interface DeclarationDescriptor {

	Set<DependencyTreeNode> describe(Class<?> declaration);
}
