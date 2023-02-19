package org.sovframework.contextconfiguration.context;

import javax.annotation.Nonnull;
import java.util.Objects;

class ContextNode {

	private final String name;

	private final Class<?> type;

	public ContextNode(@Nonnull String name, @Nonnull Class<?> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ContextNode that = (ContextNode) o;
		return name.equals(that.name) &&
				type.equals(that.type);
	}

	public boolean equalsBy(Class<?> type) {
		return this.type.equals(type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
}
