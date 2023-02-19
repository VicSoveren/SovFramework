package org.sovframework.contextconfiguration.dependency;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DependencyTree extends HashSet<DependencyTreeNode> {

	private final Set<DependencyTreeNode> roots;

	public DependencyTree() {
		roots = new HashSet<>();
	}

	private DependencyTreeNode appendIfAbsent(String name, Class<?> type) {
		DependencyTreeNode newNode = new DependencyTreeNode(name, type);

		Optional<DependencyTreeNode> node = stream()
				.filter(it -> it.equals(newNode))
				.findAny();
		if (node.isEmpty()) {
			add(newNode);
			return newNode;
		} else {
			return node.get();
		}
	}

	public void add(@Nonnull String name, @Nonnull Class<?> type, @Nonnull Map<String, Class<?>> dependencies) {
		DependencyTreeNode node = appendIfAbsent(name, type);
		if (dependencies.isEmpty()) {
			roots.add(node);
			return;
		}

		dependencies.entrySet().stream()
				.map(it -> appendIfAbsent(it.getKey(), it.getValue()))
				.forEach(it -> it.appendDependentNodes(node));
	}

	public Set<DependencyTreeNode> getRoots() {
		return new HashSet<>(roots);
	}
}
