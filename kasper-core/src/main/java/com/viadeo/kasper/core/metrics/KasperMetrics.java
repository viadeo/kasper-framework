// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.resolvers.Resolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.event.EventListener;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperMetrics {

    /* Command gateway */
    public static final Timer GLOBAL_QUERY_TIMER_REQUESTS_TIME = R().timer(name(QueryGateway.class, "requests-time"));
    public static final Histogram GLOBAL_QUERY_HISTO_REQUESTS_TIMES = R().histogram(name(QueryGateway.class, "requests-times"));
    public static final Meter GLOBAL_QUERY_METER_REQUESTS = R().meter(name(QueryGateway.class, "requests"));
    public static final Meter GLOBAL_QUERY_METER_ERRORS = R().meter(name(QueryGateway.class, "errors"));

    /* Query gateway */
    public static final Timer GLOBAL_COMMAND_TIMER_REQUESTS_TIME = R().timer(name(CommandGateway.class, "requests-time"));
    public static final Meter GLOBAL_COMMAND_METER_REQUESTS = R().meter(name(CommandGateway.class, "requests"));
    public static final Meter GLOBAL_COMMAND_METER_ERRORS = R().meter(name(CommandGateway.class, "errors"));

    /* Event listeners */
    public static final Histogram GLOBAL_EVENTLISTENER_HISTO_HANDLE_TIMES = R().histogram(name(EventListener.class, "handle-times"));
    public static final Meter GLOBAL_EVENTLISTENER_METER_HANDLES = R().meter(name(EventListener.class, "handles"));
    public static final Meter GLOBAL_EVENTLISTENER_METER_ERRORS = R().meter(name(EventListener.class, "errors"));

    /* Repositories */
    public static final Histogram GLOBAL_REPOSITORY_HISTO_SAVE_TIMES = R().histogram(name(IRepository.class, "save-times"));
    public static final Meter GLOBAL_REPOSITORY_METER_SAVES = R().meter(name(IRepository.class, "saves"));
    public static final Meter GLOBAL_REPOSITORY_METER_SAVE_ERRORS = R().meter(name(IRepository.class, "save-errors"));

    public static final Histogram GLOBAL_REPOSITORY_HISTO_LOAD_TIMES = R().histogram(name(IRepository.class, "load-times"));
    public static final Meter GLOBAL_REPOSITORY_METER_LOADS = R().meter(name(IRepository.class, "loads"));
    public static final Meter GLOBAL_REPOSITORY_METER_LOAD_ERRORS = R().meter(name(IRepository.class, "load-errors"));

    public static final Histogram GLOBAL_REPOSITORY_HISTO_DELETE_TIMES = R().histogram(name(IRepository.class, "delete-times"));
    public static final Meter GLOBAL_REPOSITORY_METER_DELETES = R().meter(name(IRepository.class, "deletes"));
    public static final Meter GLOBAL_REPOSITORY_METER_DELETE_ERRORS = R().meter(name(IRepository.class, "delete-errors"));

    // ------------------------------------------------------------------------

    private static KasperMetrics instance;

    public static KasperMetrics instance() {
        if (null == instance) {
            instance = new KasperMetrics();
        }
        return instance;
    }

    // ------------------------------------------------------------------------

    /* Static access to the KasperMetrics instance */
    public static void setNamePrefix(final String prefix) { instance()._setNamePrefix(prefix); }
    public static String name(final String name, final String...names) { return instance()._name(name, names); }
    public static String name(final Class clazz, final String...names) { return instance()._name(clazz, names); }
    public static void setResolverFactory(final ResolverFactory resolverFactory) { instance()._setResolverFactory(resolverFactory); }
    public static void unsetResolverFactory() { instance()._unsetResolverFactory(); }
    public static void clearCache() { instance()._clearCache(); }
    public static MetricRegistry getMetricRegistry() { return instance().getRegistry(); }
    public static void setMetricRegistry(MetricRegistry metricRegistry) { instance().setRegistry(metricRegistry); }

    private static MetricRegistry R() { return getMetricRegistry(); }

    // ------------------------------------------------------------------------

    private String namePrefix = "";
    private ConcurrentMap<Class, String> pathCache = Maps.newConcurrentMap();
    private ResolverFactory resolverFactory;
    private MetricRegistry metricRegistry;

    KasperMetrics() { /* Utility class */ }

    public MetricRegistry getRegistry() {
        if(null == metricRegistry) {
            throw new IllegalStateException("The metric registry is not initialized.");
        }
        return metricRegistry;
    }

    public void setRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void _setNamePrefix(final String prefix) {
        namePrefix = prefix;
    }

    public String _name(final String name, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(name, names);
    }

    public String _name(final Class clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(pathForKasperComponent(clazz), names);
    }

    @SuppressWarnings("unchecked")
    protected String pathForKasperComponent(final Class clazz) {

        if (pathCache.containsKey(clazz)) {
            return pathCache.get(clazz);
        }

        String componentPath = clazz.getName().toLowerCase();

        if (null != resolverFactory) {
            final String name = clazz.getSimpleName();

            final Optional<Resolver> resolver = resolverFactory.getResolverFromClass(clazz);
            if (resolver.isPresent()) {
                final String domainName = resolver.get().getDomainLabel(clazz);
                final String type = resolver.get().getTypeName();
                componentPath = String.format("%s.%s.%s", domainName, type, name).toLowerCase();
                pathCache.put(clazz, componentPath);
            }
        }

        return componentPath;
    }

    public void _setResolverFactory(final ResolverFactory resolverFactory) {
        this.resolverFactory = checkNotNull(resolverFactory);
    }

    public void _unsetResolverFactory() {
        resolverFactory = null;
    }

    public void _clearCache() {
        pathCache.clear();
    }

}
