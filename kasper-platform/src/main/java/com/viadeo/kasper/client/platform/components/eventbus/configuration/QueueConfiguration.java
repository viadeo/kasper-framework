// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.typesafe.config.Config;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Common properties that describes a queue component.
 */
public class QueueConfiguration {

    private final String name;
    private final Boolean durable;
    private final Boolean autoDelete;

    public QueueConfiguration(final Config config) {
        this(config.getString("name"), config.getBoolean("durable"), config.getBoolean("autoDelete"));
    }

    /**
     * Construct a new configuration of a Queue
     *
     * @param name the name of the queue
     * @param durable true if we are declaring a durable queue (true by default)
     * @param autoDelete true if the server should delete the queue when it is no longer in use (false by default)
     */
    public QueueConfiguration(final String name, final Boolean durable, final Boolean autoDelete) {
        this.name = checkNotNull(name);
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
     * @return true if we are declaring a durable queue (the queue will survive a server restart)
     */
    public Boolean getDurable() {
        return durable;
    }

    /**
     * @return true if the server should delete the queue when it is no longer in use.
     */
    public Boolean getAutoDelete() {
        return autoDelete;
    }
}
