// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.platform.Platform;
import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpExpositionBundle implements Bundle {
    private final Platform platform;
    private final QueryServicesLocator queryServiceLocator;
    private final DomainLocator domainLocator;
    private final String queryUrlPattern;
    private final String commandUrlPattern;

    // ------------------------------------------------------------------------

    public HttpExpositionBundle(Platform platform, QueryServicesLocator queryServiceLocator,
            DomainLocator domainLocator, String queryUrlPattern, String commandUrlPattern) {
        this.platform = platform;
        this.queryServiceLocator = queryServiceLocator;
        this.domainLocator = domainLocator;
        this.queryUrlPattern = queryUrlPattern;
        this.commandUrlPattern = commandUrlPattern;
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Bootstrap<?> bootstrap) { }

    @Override
    public void run(final Environment environment) {
        environment.addServlet(new HttpQueryExposer(platform, queryServiceLocator), queryUrlPattern);
        environment.addServlet(new HttpCommandExposer(platform, domainLocator), commandUrlPattern);
    }

    // ------------------------------------------------------------------------

    public static class Builder {
        private Platform platform;
        private QueryServicesLocator queryServiceLocator;
        private DomainLocator domainLocator;
        private String queryUrlPattern;
        private String commandUrlPattern;

        public Builder use(final Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder use(final QueryServicesLocator queryServiceLocator) {
            this.queryServiceLocator = queryServiceLocator;
            return this;
        }

        public Builder use(final DomainLocator domainLocator) {
            this.domainLocator = domainLocator;
            return this;
        }

        public Builder queryPath(final String path) {
            this.queryUrlPattern = path;
            return this;
        }

        public Builder commandPath(final String path) {
            this.commandUrlPattern = path;
            return this;
        }

        public HttpExpositionBundle create() {
            return new HttpExpositionBundle(checkNotNull(platform), checkNotNull(queryServiceLocator),
                    checkNotNull(domainLocator), checkNotNull(queryUrlPattern), checkNotNull(commandUrlPattern));
        }
    }

}
