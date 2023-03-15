package org.sovframework.contextconfiguration.dependency.tree;

import org.sovframework.contextconfiguration.dependency.tree.node.DependencyTreeNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DependencyTree extends HashMap<DependencyTreeNode, DependencyTreeNode> {

	private final Set<DependencyTreeNode> roots = new HashSet<>();

	public DependencyTreeNode put(DependencyTreeNode node) {
		if (containsKey(node)) {
			DependencyTreeNode existingNode = get(node).merge(node);
			handleRoots(existingNode);

			return existingNode;
		} else {
			super.put(node, node);
			handleRoots(node);

			return node;
		}
	}

	private void handleRoots(DependencyTreeNode node) {
		if (node.getRoots().isEmpty()) {
			roots.add(node);
		} else {
			node.getRoots().stream()
					.map(this::putRoot)
					.forEach(root -> root.appendBranches(node));
		}
	}

	private DependencyTreeNode putRoot(DependencyTreeNode root) {
		if (containsKey(root)) {
			return get(root);
		} else {
			super.put(root, root);
			return root;
		}
	}

	@Override
	public DependencyTreeNode put(DependencyTreeNode key, DependencyTreeNode value) {
		return put(key);
	}

	public Set<DependencyTreeNode> getRoots() {
		return new HashSet<>(roots);
	}
}
