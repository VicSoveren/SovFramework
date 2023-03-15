package org.sovframework.contextconfiguration.context;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext extends HashSet<Instance> {

	public Set<Instance> get(Class<?> type) {
		return stream()
				.filter(instance -> instance.subtypeOf(type))
				.collect(Collectors.toSet());
	}

	public Optional<Instance> get(String name, Class<?> type) {
		return stream()
				.filter(instance -> instance.getName().equals(name) && instance.subtypeOf(type))
				.findAny();
	}

	public boolean add(String name, Class<?> type, Object value) {
		return add(new Instance(name, type, value));
	}

	@Override
	public boolean add(Instance instance) {
		return super.add(instance);
	}
}
