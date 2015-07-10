// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.starters;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Configuration
public class KasperMetricsConfiguration {

    protected static final int DEFAULT_METRICS_TIMEOUT_SECONDS = 20;

    @Bean
    @Profile({ "demo", "preprod", "prod" })
    public GraphiteReporter reporter(MetricRegistry metricRegistry, Config config) throws IOException {

        final String host = config.getString("infrastructure.graphite.host");
        final Integer port = config.getInt("infrastructure.graphite.port");

        final Graphite graphite = new Graphite(new InetSocketAddress(host, port));
        final String prefix = "servers." + getFQDN() + ".kasper";

        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(prefix)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(graphite);

        reporter.start(DEFAULT_METRICS_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return reporter;
    }

    @Bean
    public MetricRegistry metricRegistry() throws UnknownHostException {
        return new MetricRegistry();
    }


    /**
     * This should probably removed from the context...
     *
     * @return domain helper
     */
    @Bean
    public DomainHelper domainHelper() {
        return new DomainHelper();
    }


    /**
     * Instanciate the central metric registry
     * <p/>
     * TODO : Why there is so much work to do in order to instanciate a metric registry ?
     *
     * @param domainHelper (?)
     * @return metric registry
     */
    @Bean
    public ResolverFactory resolverFactory(final MetricRegistry metricRegistry, final DomainHelper domainHelper) {

        // FIXME here we declare resolver allowing to defined the name of metrics
        final ConceptResolver conceptResolver = new ConceptResolver();
        final RelationResolver relationResolver = new RelationResolver(conceptResolver);
        final EntityResolver entityResolver = new EntityResolver(conceptResolver, relationResolver);

        final DomainResolver domainResolver = new DomainResolver();
        domainResolver.setDomainHelper(domainHelper);

        final EventListenerResolver eventListenerResolver = new EventListenerResolver();
        eventListenerResolver.setDomainResolver(domainResolver);

        final CommandHandlerResolver commandHandlerResolver = new CommandHandlerResolver();
        commandHandlerResolver.setDomainResolver(domainResolver);

        final RepositoryResolver repositoryResolver = new RepositoryResolver(entityResolver);
        repositoryResolver.setDomainResolver(domainResolver);

        final QueryHandlerResolver queryHandlerResolver = new QueryHandlerResolver(domainResolver);

        final CommandResolver commandResolver = new CommandResolver();
        commandResolver.setCommandHandlerResolver(commandHandlerResolver);
        commandResolver.setDomainResolver(domainResolver);

        final QueryResolver queryResolver = new QueryResolver();
        queryResolver.setQueryHandlerResolver(queryHandlerResolver);
        queryResolver.setDomainResolver(domainResolver);

        final QueryResultResolver queryResultResolver = new QueryResultResolver();
        queryResultResolver.setQueryHandlerResolver(queryHandlerResolver);
        queryResultResolver.setDomainResolver(domainResolver);

        final EventResolver eventResolver = new EventResolver();
        eventResolver.setDomainResolver(domainResolver);

        final ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setCommandHandlerResolver(commandHandlerResolver);
        resolverFactory.setEventListenerResolver(eventListenerResolver);
        resolverFactory.setEventResolver(eventResolver);
        resolverFactory.setRepositoryResolver(repositoryResolver);
        resolverFactory.setQueryHandlerResolver(queryHandlerResolver);
        resolverFactory.setCommandResolver(commandResolver);
        resolverFactory.setQueryResolver(queryResolver);
        resolverFactory.setQueryResultResolver(queryResultResolver);

        KasperMetrics.setResolverFactory(resolverFactory);
        KasperMetrics.setMetricRegistry(metricRegistry);

        return resolverFactory;
    }

    /**
     * Get the host fqdn
     *
     * @return FQDN with dot replaced by underscore
     * @throws java.net.UnknownHostException
     */
    protected String getFQDN() throws UnknownHostException {
        return InetAddress.getLocalHost().getCanonicalHostName().replace(".", "_");
    }

}
