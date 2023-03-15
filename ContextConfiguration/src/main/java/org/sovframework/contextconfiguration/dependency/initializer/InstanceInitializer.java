package org.sovframework.contextconfiguration.dependency.initializer;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Function;

public interface InstanceInitializer extends Function<List<Object>, Object> {

	@Override
	Object apply(List<Object> params);

	List<Parameter> getRequiredParameters();
}
