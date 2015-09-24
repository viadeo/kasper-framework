// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.core.TestDomain;
import com.viadeo.kasper.core.config.spring.KasperConfiguration;
import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.core.TestDomain.TestQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResilienceConfiguratorUTest {

    private final Config config = KasperConfiguration.configuration("test", true)
            .withFallback(ConfigFactory.parseMap(
                    ImmutableMap.<String, Object>builder()
                            .put("runtime.hystrix.input." + TestQuery.class.getSimpleName() + ".circuitBreaker.enable", false)
                            .put("runtime.hystrix.input." + TestQuery.class.getSimpleName() + ".circuitBreaker.requestVolumeThreshold", 10)
                            .put("runtime.hystrix.input." + TestQuery.class.getSimpleName() + ".circuitBreaker.thresholdInPercent", 20)
                            .put("runtime.hystrix.input." + TestQuery.class.getSimpleName() + ".circuitBreaker.sleepWindowInMillis", 1800000)
                            .put("runtime.hystrix.input." + TestQuery.class.getSimpleName() + ".execution.timeoutInMillis", 3000)
                            .build()
            ));

    private ResilienceConfigurator configurer;

    @Before
    public void setUp() throws Exception {
        configurer = new ResilienceConfigurator(config);
    }

    @Test
    public void configure_from_an_any_input() {
        // When
        final ResilienceConfigurator.InputConfig inputConfig = configurer.configure(new TestDomain.TestCommand());

        // Then
        assertNotNull(inputConfig);
        assertEquals(true, inputConfig.circuitBreakerEnable);
        assertEquals(20, (int) inputConfig.circuitBreakerRequestVolumeThreshold);
        assertEquals(40, (int) inputConfig.circuitBreakerThresholdInPercent);
        assertEquals(3600000, (int) inputConfig.circuitBreakerSleepWindowInMillis);
        assertEquals(2000, (int) inputConfig.executionTimeoutInMillis);
    }

    @Test
    public void configure_from_an_input_identified_as_custom() {
        // When
        final ResilienceConfigurator.InputConfig inputConfig = configurer.configure(new TestQuery());

        // Then
        assertNotNull(inputConfig);
        assertEquals(false, inputConfig.circuitBreakerEnable);
        assertEquals(10, (int) inputConfig.circuitBreakerRequestVolumeThreshold);
        assertEquals(20, (int) inputConfig.circuitBreakerThresholdInPercent);
        assertEquals(1800000, (int) inputConfig.circuitBreakerSleepWindowInMillis);
        assertEquals(3000, (int) inputConfig.executionTimeoutInMillis);
    }

}