// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.config.spring;

import com.typesafe.config.Config;
import com.viadeo.kasper.core.config.KasperConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperTypesafeConfigConfiguration {

    public static Config config = null;

    @Bean
    public static Config configuration() {
        return configuration(false);
    }

    public static Config configuration(final boolean reload) {
        return configuration(null, reload);
    }

    public static Config configuration(final String environment, final boolean reload) {
        if ((null == config) || reload) {
            config = new KasperConfiguration(
                    KasperConfiguration.KasperConfigurationOptions.defaults().forcedEnvironment(environment)
            ).load();
        }
        return config;
    }
}
