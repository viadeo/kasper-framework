package com.viadeo.kasper.spring.config;

import com.typesafe.config.Config;
import com.viadeo.kasper.core.config.KasperConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This spring context exposes the defined configuration inside the type-safe config files (or CLI passed ones).
 * <p/>
 * It registers a {@link com.typesafe.config.Config} bean to use to get values from the type-safe configuration.
 *
 * @see <a href="https://github.com/typesafehub/config/blob/master/README.md">Type Safe Config</a>
 */
@Configuration
public class KasperSpringConfiguration {

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
