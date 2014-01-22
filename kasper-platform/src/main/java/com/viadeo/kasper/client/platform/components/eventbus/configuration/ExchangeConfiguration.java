// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.typesafe.config.Config;
import org.springframework.amqp.core.ExchangeTypes;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Common properties that describes an exchange component.
 */
public class ExchangeConfiguration {

    private final String name;
    private final String type;
    private final Boolean durable;
    private final Boolean autoDelete;

    public ExchangeConfiguration(final Config config) {
        this(
                config.getString("name"),
                config.getString("type"),
                config.getBoolean("durable"),
                config.getBoolean("autoDelete")
        );
    }

    /**
     * Construct a new configuration of an Exchange
     *
     * @param name the name of the exchange
     * @param type the type of the exchange (ExchangeTypes.TOPIC by default)
     * @param durable true if we are declaring a durable exchange (true by default)
     * @param autoDelete true if the server should delete the exchange when it is no longer in use (false by default)
     */
    public ExchangeConfiguration(
            final String name,
            final String type,
            final Boolean durable,
            final Boolean autoDelete
    ) {
        this.name = checkNotNull(name);
        this.type = firstNonNull(type, ExchangeTypes.TOPIC);
        this.durable = firstNonNull(durable, Boolean.TRUE);
        this.autoDelete = firstNonNull(autoDelete, Boolean.FALSE);
    }

    /**
     * @return the name of the exchange
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type of the exchange
     * @see ExchangeTypes
     */
    public String getType() {
        return type;
    }

    /**
     * @return true if we are declaring a durable exchange (the exchange will survive a server restart)
     */
    public Boolean getDurable() {
        return durable;
    }

    /**
     * @return true if the server should delete the exchange when it is no longer in use
     */
    public Boolean getAutoDelete() {
        return autoDelete;
    }
}
