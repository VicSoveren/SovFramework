package org.sovframework.contextconfigurationexample.configuration;

import org.sovframework.contextconfiguration.annotation.Configuration;
import org.sovframework.contextconfiguration.annotation.Factory;
import org.sovframework.contextconfigurationexample.service.ConfigurableService;

@Configuration
public class ExampleConfiguration {

	@Factory
	public ConfigurableService configurableService() {
		return new ConfigurableService();
	}
}
