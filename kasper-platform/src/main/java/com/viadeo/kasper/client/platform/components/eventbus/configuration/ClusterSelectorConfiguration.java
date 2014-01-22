// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.typesafe.config.Config;

import java.util.concurrent.TimeUnit;

public class ClusterSelectorConfiguration {

    private static TimeUnit toTimeUnit(final String toString) {
        final Optional<TimeUnit> timeUnitOptional = Enums.getIfPresent(TimeUnit.class, toString);
        Preconditions.checkArgument(timeUnitOptional.isPresent());
        return timeUnitOptional.get();
    }

    private final String name;
    private final Integer maximumPoolSize;
    private final Long keepAliveTime;
    private final TimeUnit timeUnit;
    private final Integer poolSize;
    private final Boolean asynchronous;

    public ClusterSelectorConfiguration(final Config config) {
        this(
                config.getString("name"),
                config.getLong("keepAliveTime"),
                toTimeUnit(config.getString("timeUnit")),
                config.getInt("pool.size"),
                config.getInt("pool.maxSize"),
                config.getBoolean("asynchronous")
        );
    }

    public ClusterSelectorConfiguration(
            final String name,
            final Long keepAliveTime,
            final TimeUnit timeUnit,
            final Integer poolSize,
            final Integer maximumPoolSize,
            final Boolean asynchronous
    ) {
        this.name = name;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.poolSize = poolSize;
        this.asynchronous = asynchronous;
    }

    public String getName() {
        return name;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public Boolean isAsynchronous() {
        return asynchronous;
    }
}
