package org.sovframework.contextconfiguration.application;

import org.sovframework.contextconfiguration.context.ApplicationContext;
import org.sovframework.contextconfiguration.context.ApplicationContextBuilder;
import org.sovframework.contextconfiguration.dependency.tree.DependencyTree;
import org.sovframework.contextconfiguration.dependency.tree.factory.DependencyTreeFactory;
import org.sovframework.contextconfiguration.util.StringUtil;

import java.util.Collections;
import java.util.Set;

public class SovApplication {

	private static final String PROFILES_PATH = "sov.profiles";

	private static final String DEFAULT_PROFILE = "default";

	private final Class<?> applicationStart;

	private SovApplication(Class<?> applicationStart) {
		this.applicationStart = applicationStart;
	}

	public static ApplicationContext run(Class<?> applicationStart, String[] args) {
		return new SovApplication(applicationStart).run(args);
	}

	private ApplicationContext run(String... args) {
		Set<String> profiles = getProfiles(findProfiles(args));

		DependencyTree dependencyTree = new DependencyTreeFactory(Collections.singletonList(applicationStart.getPackageName())).create();

		return ApplicationContextBuilder.create().from(dependencyTree).build();
	}

	private String findProfiles(String... args) {
		for (String arg : args) {
			if (StringUtil.toLowerDotCase(arg).startsWith(PROFILES_PATH)) {
				String[] profiles = arg.split("=");
				return profiles[1];
			}
		}

		return null;
	}

	private Set<String> getProfiles(String profiles) {
		if (!StringUtil.isEmpty(profiles)) {
			return Set.of(profiles.split(","));
		} else {
			return Collections.singleton(DEFAULT_PROFILE);
		}
	}
}
