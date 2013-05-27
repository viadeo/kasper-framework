package com.viadeo.kasper.exposition;

import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class HttpExpositionBundle implements Bundle {
	private final String queryUrlPattern;
	private final String commandUrlPattern;

	public HttpExpositionBundle() {
		this("/query/*", "/command/*");
	}

	public HttpExpositionBundle(String queryUrlPattern, String commandUrlPattern) {
		super();
		this.queryUrlPattern = queryUrlPattern;
		this.commandUrlPattern = commandUrlPattern;
	}

	@Override
	public void initialize(Bootstrap<?> bootstrap) {
	}

	@Override
	public void run(Environment environment) {
		environment.addServlet(HttpQueryExposer.class, queryUrlPattern);
		environment.addServlet(HttpCommandExposer.class, commandUrlPattern);
	}

}
