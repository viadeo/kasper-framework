// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.viadeo.kasper.api.response.KasperResponse.Status;

public abstract class KasperResponseDeserializer<R extends KasperResponse> extends JsonDeserializer<R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperResponseDeserializer.class);


    private final KasperReasonDeserializer kasperReasonDeserializer;

    protected KasperResponseDeserializer() {
        this.kasperReasonDeserializer = new KasperReasonDeserializer();
    }

    protected KasperResponse deserialize(final ObjectNode root) throws IOException {
        // STATUS
        Status status = Status.ERROR;
        if (root.has(ObjectMapperProvider.STATUS)) {
            try {
                status = Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
            } catch (final IllegalArgumentException e) {
                LOGGER.error("Unable to determine status", e);
            }
        }

        KasperReason kasperReason = kasperReasonDeserializer.deserialize(root);

        // TODO: add Security Token
        return new KasperResponse(status, kasperReason);

    }

}
