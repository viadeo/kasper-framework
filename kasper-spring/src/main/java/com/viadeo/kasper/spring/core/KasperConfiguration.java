// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.typesafe.config.Config;
import com.viadeo.kasper.core.config.ConfigurationLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperConfiguration {

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
            config = new ConfigurationLoader(
                    ConfigurationLoader.Options.defaults().forcedEnvironment(environment)
            ).load();
        }
        return config;
    }
}
