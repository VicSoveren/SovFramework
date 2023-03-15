package org.sovframework.contextconfiguration.context;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Instance {

	private final String name;

	private final Class<?> type;

	private final Object value;

	public Instance(@Nonnull String name, @Nonnull Class<?> type, @Nonnull Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Instance that = (Instance) o;
		return name.equals(that.name) &&
				type.equals(that.type);
	}

	public boolean subtypeOf(Class<?> that) {
		return that.isAssignableFrom(this.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
}
