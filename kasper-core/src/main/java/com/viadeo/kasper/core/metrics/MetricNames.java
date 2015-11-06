// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

public class MetricNames {

    public static MetricNames of(Class<?> componentClass) {
        return of(componentClass, "errors", "requests-handle-time");
    }

    public static MetricNames of(Class<?> componentClass, String errors, String requestsTime) {
        return new MetricNames(
                KasperMetrics.name(componentClass, errors),
                KasperMetrics.name(componentClass, requestsTime)
        );
    }

    public static MetricNames byDomainOf(Class<?> componentClass) {
        return byDomainOf(componentClass, "errors", "requests-handle-time");
    }

    public static MetricNames byDomainOf(Class<?> componentClass, String errors, String requestsTime) {
        return new MetricNames(
                KasperMetrics.name(MetricNameStyle.DOMAIN_TYPE, componentClass, errors),
                KasperMetrics.name(MetricNameStyle.DOMAIN_TYPE, componentClass, requestsTime)
        );
    }

    public final String errors;
    public final String requestsTime;

    public MetricNames(final String errors, final String requestsTime) {
        this.errors = errors;
        this.requestsTime = requestsTime;
    }
}
