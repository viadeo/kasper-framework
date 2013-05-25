// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class HttpExpositionBundle implements Bundle {

	private final String queryUrlPattern;
	private final String commandUrlPattern;

    // ------------------------------------------------------------------------

    public HttpExpositionBundle() {
		this("/query/*", "/command/*");
	}

	public HttpExpositionBundle(final String queryUrlPattern, final String commandUrlPattern) {
		super();
		this.queryUrlPattern = queryUrlPattern;
		this.commandUrlPattern = commandUrlPattern;
	}

    // ------------------------------------------------------------------------

	@Override
	public void initialize(final Bootstrap<?> bootstrap) {
	}

	@Override
	public void run(final Environment environment) {
		environment.addServlet(HttpQueryExposer.class, queryUrlPattern);
		environment.addServlet(HttpCommandExposer.class, commandUrlPattern);
	}

}
