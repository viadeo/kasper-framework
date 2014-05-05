// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal;

import com.viadeo.axonframework.eventhandling.terminal.kafka.EventMessageSerializer;
import com.viadeo.kasper.client.platform.components.eventbus.JacksonSerializer;
import com.viadeo.kasper.tools.ObjectMapperProvider;

/**
 * This class provide an implementation of the message serializer that will be used by Kafka a distributed messaging
 * system.
 * </p>
 * You should specify the fully qualified name as value of 'serializer.class' property in order to use it with the
 * distributed event bus. Thereby the Kafka consumer and producer could create a new instance using the default
 * constructor.
 */
public class KafkaMessageSerializer extends EventMessageSerializer {

    public KafkaMessageSerializer() {
        super(new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper()));
    }
}
