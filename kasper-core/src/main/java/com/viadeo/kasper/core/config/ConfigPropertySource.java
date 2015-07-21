// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.config;

import com.typesafe.config.Config;
import org.springframework.core.env.PropertySource;

/**
 * Allow user to inject typesafe config properties in
 * their stereotypes, without having to explicitly declare
 * a Bean factory
 */
public class ConfigPropertySource extends PropertySource<Object> {

    /**
     * A typesafe config instance
     */
    private final Config config;

    /**
     * Create a property source for the given config
     *
     * @param config typesafe config
     * @param name name of this property source
     */
    public ConfigPropertySource(Config config, String name) {
        super(name);
        this.config = config;
    }

    @Override
    public boolean containsProperty(String name) {
        return config.hasPath(name);
    }

    @Override
    public Object getProperty(String name) {
        return containsProperty(name) ? config.getAnyRef(name) : null;
    }
}
