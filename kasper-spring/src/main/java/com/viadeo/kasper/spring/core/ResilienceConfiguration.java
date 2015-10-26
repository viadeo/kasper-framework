// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.typesafe.config.Config;
import com.viadeo.kasper.core.interceptor.resilience.ResilienceConfigurator;
import com.viadeo.kasper.core.interceptor.resilience.ResiliencePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResilienceConfiguration {

    @Bean
    public ResiliencePolicy resiliencePolicy() {
        return new ResiliencePolicy();
    }

    @Bean
    public ResilienceConfigurator resilienceConfigurator(final Config config) {
        return new ResilienceConfigurator(config);
    }
}
