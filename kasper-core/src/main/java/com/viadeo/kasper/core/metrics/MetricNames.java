// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

public class MetricNames {

    public static MetricNames of(Class<?> componentClass) {
        return new MetricNames(
                KasperMetrics.name(componentClass, "errors"),
                KasperMetrics.name(componentClass, "requests"),
                KasperMetrics.name(componentClass, "requests-time")
        );
    }

    public static MetricNames byDomainOf(Class<?> componentClass) {
        return new MetricNames(
                KasperMetrics.name(MetricNameStyle.DOMAIN_TYPE, componentClass, "errors"),
                KasperMetrics.name(MetricNameStyle.DOMAIN_TYPE, componentClass, "requests"),
                KasperMetrics.name(MetricNameStyle.DOMAIN_TYPE, componentClass, "requests-time")
        );
    }

    public final String errors;
    public final String requests;
    public final String requestsTime;

    public MetricNames(final String errors, final String requests, final String requestsTime) {
        this.errors = errors;
        this.requests = requests;
        this.requestsTime = requestsTime;
    }
}
