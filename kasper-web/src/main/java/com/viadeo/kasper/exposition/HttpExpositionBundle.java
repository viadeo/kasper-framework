// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import com.viadeo.kasper.platform.IPlatform;
import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import static com.google.common.base.Preconditions.*;

public class HttpExpositionBundle implements Bundle {
    private final IPlatform platform;
    private final IQueryServicesLocator queryServiceLocator;
    private final IDomainLocator domainLocator;
    private final String queryUrlPattern;
    private final String commandUrlPattern;

    // ------------------------------------------------------------------------

    public HttpExpositionBundle(IPlatform platform, IQueryServicesLocator queryServiceLocator,
            IDomainLocator domainLocator, String queryUrlPattern, String commandUrlPattern) {
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
        private IPlatform platform;
        private IQueryServicesLocator queryServiceLocator;
        private IDomainLocator domainLocator;
        private String queryUrlPattern;
        private String commandUrlPattern;

        public Builder use(final IPlatform platform) {
            this.platform = platform;
            return this;
        }

        public Builder use(final IQueryServicesLocator queryServiceLocator) {
            this.queryServiceLocator = queryServiceLocator;
            return this;
        }

        public Builder use(final IDomainLocator domainLocator) {
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
