// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TypeSafeConfigPropertyPlaceholder extends PropertySourcesPlaceholderConfigurer {

    private static final Joiner JOINER = Joiner.on(',');

    protected final Config config;

    public TypeSafeConfigPropertyPlaceholder(Config config) {
        this.config = config;
    }

    public TypeSafeConfigPropertyPlaceholder() {
        config = ConfigFactory.load();
    }

    public TypeSafeConfigPropertyPlaceholder(String resource) {
        config = ConfigFactory.load(resource);
    }

    @Override
    @VisibleForTesting
    @SuppressWarnings("unchecked")
    public void loadProperties(Properties props) throws IOException {
        for (final Map.Entry<String, ConfigValue> confEntry : config.entrySet()) {
            final String key = confEntry.getKey();
            final ConfigValue value = confEntry.getValue();

            switch (value.valueType()) {
                case LIST: {
                    try {
                        props.setProperty(key, JOINER.join(((List<String>) value.unwrapped())));
                    } catch (RuntimeException e) {
                        // TODO permits other types of lists?!
                        throw new IllegalArgumentException("listable parameters should only have strings as elements.", e);
                    }
                    break;
                }
                case NULL: {
                    props.setProperty(key, "");
                    break;
                }
                case NUMBER: {
                    props.setProperty(key, value.render());
                    break;
                }
                case STRING: {
                    props.setProperty(key, (String) value.unwrapped());
                    break;
                }
                default: {
                    final String valueRendred = value.render(ConfigRenderOptions.concise().setJson(false));
                    props.setProperty(key, valueRendred);
                    break;
                }
            }
        }
    }

}