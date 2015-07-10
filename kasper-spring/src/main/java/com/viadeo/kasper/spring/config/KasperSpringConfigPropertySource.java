// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.config;

import com.typesafe.config.Config;
import org.springframework.core.env.PropertySource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allow user to inject typesafe config properties in
 * their stereotypes, without having to explicitly declare
 * a Bean factory
 */
public class KasperSpringConfigPropertySource extends PropertySource<Object> {

    /**
     * A typesafe config instance
     */
    private final Config config;

    // ------------------------------------------------------------------------

    /**
     * Create a property source for the given config
     *
     * @param config typesafe config
     * @param name name of this property source
     */
    public KasperSpringConfigPropertySource(final Config config, final String name) {
        super(checkNotNull(name));
        this.config = checkNotNull(config);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean containsProperty(final String name) {
        return config.hasPath(name);
    }

    @Override
    public Object getProperty(final String name) {
        return containsProperty(name) ? config.getAnyRef(name) : null;
    }

}
