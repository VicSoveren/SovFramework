package org.sovframework.contextconfiguration.dependency;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DependencyTreeNode {

	private final String name;

	private final Class<?> type;

	private final Set<DependencyTreeNode> dependentNodes;

	public DependencyTreeNode(String name, Class<?> type) {
		this.name = name;
		this.type = type;
		this.dependentNodes = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public void appendDependentNodes(DependencyTreeNode node) {
		dependentNodes.add(node);
	}

	public Set<DependencyTreeNode> getDependentNodes() {
		return dependentNodes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DependencyTreeNode that = (DependencyTreeNode) o;
		return name.equals(that.name) &&
				type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
}
