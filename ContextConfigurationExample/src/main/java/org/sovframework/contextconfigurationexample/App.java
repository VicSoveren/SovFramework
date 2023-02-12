package org.sovframework.contextconfigurationexample;

import org.sovframework.contextconfiguration.context.ApplicationContext;
import org.sovframework.contextconfigurationexample.service.ConfigurableService;

import java.util.List;

public class App {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ApplicationContext(List.of("org.sovframework.contextconfigurationexample"));
		ConfigurableService instance = (ConfigurableService) applicationContext.getInstance(ConfigurableService.class);
		instance.print();
	}
}
