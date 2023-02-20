package org.sovframework.contextconfiguration.context;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationContext extends HashMap<ContextNode, Object> {

	public <T> T get(String name, Class<T> type) {
		return (T)get(new ContextNode(name, type));
	}

	public <T> List<T> getByType(Class<T> type) {
		return entrySet().stream()
				.filter(it -> it.getKey().equalsBy(type))
				.map(it -> (T)it.getValue())
				.collect(Collectors.toList());
	}

	public void put(String name, Class<?> type, Object value) {
		put(new ContextNode(name, type), value);
	}
}
