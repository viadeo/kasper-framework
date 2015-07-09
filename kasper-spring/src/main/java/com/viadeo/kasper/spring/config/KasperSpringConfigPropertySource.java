package com.viadeo.kasper.spring.config;

import com.typesafe.config.Config;
import org.springframework.core.env.PropertySource;

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

    /**
     * Create a property source for the given config
     *
     * @param config typesafe config
     * @param name name of this property source
     */
    public KasperSpringConfigPropertySource(Config config, String name) {
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
