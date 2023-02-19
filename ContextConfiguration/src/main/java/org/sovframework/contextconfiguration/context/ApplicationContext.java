package org.sovframework.contextconfiguration.context;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext extends HashMap<ContextNode, Object> {

	public Object get(String name, Class<?> type) {
		return get(new ContextNode(name, type));
	}

	public Set<Object> getByType(Class<?> type) {
		return entrySet().stream()
				.filter(it -> it.getKey().equalsBy(type))
				.map(Entry::getValue)
				.collect(Collectors.toSet());
	}

	public void put(String name, Class<?> type, Object value) {
		put(new ContextNode(name, type), value);
	}
}
