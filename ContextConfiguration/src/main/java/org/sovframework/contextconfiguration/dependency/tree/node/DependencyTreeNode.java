package org.sovframework.contextconfiguration.dependency.tree.node;

import org.sovframework.contextconfiguration.dependency.initializer.InstanceInitializer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DependencyTreeNode {

	private final String name;

	private final Class<?> type;

	private final Set<DependencyTreeNode> branches;

	private final Set<DependencyTreeNode> roots;

	private InstanceInitializer instanceInitializer;

	public DependencyTreeNode(String name, Class<?> type) {
		this.name = name;
		this.type = type;
		this.branches = new HashSet<>();
		this.roots = new HashSet<>();
		this.instanceInitializer = null;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Set<DependencyTreeNode> getBranches() {
		return branches;
	}

	public void appendBranches(DependencyTreeNode node) {
		branches.add(node);
	}

	public Set<DependencyTreeNode> getRoots() {
		return roots;
	}

	public void appendRoots(DependencyTreeNode node) {
		roots.add(node);
	}

	public DependencyTreeNode withRoots(Set<DependencyTreeNode> nodes) {
		roots.addAll(nodes);
		return this;
	}

	public InstanceInitializer getInstanceInitializer() {
		return instanceInitializer;
	}

	public DependencyTreeNode withInitializer(InstanceInitializer instanceInitializer) {
		this.instanceInitializer = instanceInitializer;
		return this;
	}

	public DependencyTreeNode merge(DependencyTreeNode that) {
		this.roots.addAll(that.roots);
		this.instanceInitializer = that.instanceInitializer;

		return this;
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
