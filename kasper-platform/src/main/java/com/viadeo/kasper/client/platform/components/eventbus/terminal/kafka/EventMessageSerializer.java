// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.client.platform.components.eventbus.JacksonSerializer;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import kafka.serializer.Decoder;
import kafka.serializer.Encoder;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.io.EventMessageReader;
import org.axonframework.eventhandling.io.EventMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventMessageSerializer implements Encoder<EventMessage>, Decoder<EventMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMessageSerializer.class);

    private JacksonSerializer serializer;

    public EventMessageSerializer(kafka.utils.VerifiableProperties verifiableProperties){
        //FIXME used by our connectors (producers/consumers)
        this();
    }

    public EventMessageSerializer(){
        this(ObjectMapperProvider.INSTANCE.mapper());
    }

    public EventMessageSerializer(final ObjectMapper mapper){
        this.serializer = new JacksonSerializer(mapper);
    }

    @Override
    public EventMessage fromBytes(final byte[] bytes) {
        try {
            final EventMessageReader in = new EventMessageReader(new DataInputStream(new ByteArrayInputStream(bytes)), serializer);
            return in.readEventMessage();
        } catch (IOException e) {
            LOGGER.error("unable to deserialize", e);
            return null;
        }
    }

    @Override
    public byte[] toBytes(EventMessage eventMessage) {
        checkNotNull(eventMessage);

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final EventMessageWriter outputStream = new EventMessageWriter(new DataOutputStream(baos), serializer);
            outputStream.writeEventMessage(eventMessage);

            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("unable to serialize", e);
            return null;
        }
    }
}
