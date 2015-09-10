// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResilienceConfigurator {

    private final Config config;
    private final InputConfig defaultInputConfig;
    private final Map<String,InputConfig> configByInputName;

    // ------------------------------------------------------------------------

    public ResilienceConfigurator(final Config config) {
        this.config = checkNotNull(config);
        this.configByInputName = Maps.newHashMap();
        this.defaultInputConfig = new InputConfig(
                config.getBoolean("runtime.hystrix.circuitBreaker.enable"),
                config.getInt("runtime.hystrix.circuitBreaker.thresholdInPercent"),
                config.getInt("runtime.hystrix.circuitBreaker.sleepWindowInMillis"),
                config.getInt("runtime.hystrix.execution.timeoutInMillis")
        );
    }

    // ------------------------------------------------------------------------

    public InputConfig configure(final Object input) {
        checkNotNull(input);

        InputConfig inputConfig = configByInputName.get(input.getClass().getName());

        if (inputConfig == null) {
            try {
                final Config config = this.config.getConfig(
                        "runtime.hystrix.input." + input.getClass().getSimpleName()
                );

                inputConfig = new InputConfig(
                        getBooleanOr(config, "circuitBreaker.enable", defaultInputConfig.circuitBreakerEnable),
                        getIntOr(config, "circuitBreaker.thresholdInPercent", defaultInputConfig.circuitBreakerThresholdInPercent),
                        getIntOr(config, "circuitBreaker.sleepWindowInMillis", defaultInputConfig.circuitBreakerSleepWindowInMillis),
                        getIntOr(config, "execution.timeoutInMillis", defaultInputConfig.executionTimeoutInMillis)
                );

            } catch (final ConfigException e) {
                inputConfig = defaultInputConfig;
            }
            configByInputName.put(input.getClass().getName(), inputConfig);
        }

        return inputConfig;
    }

    protected Boolean getBooleanOr(final Config config, final String path, final Boolean defaultValue) {
        try {
            return config.getBoolean(path);
        } catch (final ConfigException e) {
            return defaultValue;
        }
    }

    protected Integer getIntOr(Config config, String path, Integer defaultValue) {
        try {
            return config.getInt(path);
        } catch (final ConfigException e) {
            return defaultValue;
        }
    }

    public static class InputConfig {

        public final Boolean circuitBreakerEnable;
        public final Integer circuitBreakerThresholdInPercent;
        public final Integer circuitBreakerSleepWindowInMillis;
        public final Integer executionTimeoutInMillis;

        public InputConfig(
                final Boolean circuitBreakerEnable,
                final Integer circuitBreakerThresholdInPercent,
                final Integer circuitBreakerSleepWindowInMillis,
                final Integer executionTimeoutInMillis
        ) {
            this.circuitBreakerEnable = circuitBreakerEnable;
            this.circuitBreakerThresholdInPercent = circuitBreakerThresholdInPercent;
            this.circuitBreakerSleepWindowInMillis = circuitBreakerSleepWindowInMillis;
            this.executionTimeoutInMillis = executionTimeoutInMillis;
        }
    }

}
