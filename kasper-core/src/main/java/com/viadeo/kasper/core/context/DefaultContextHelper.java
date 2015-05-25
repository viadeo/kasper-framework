// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.context;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.IDBuilder;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.ContextHelper;
import com.viadeo.kasper.context.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultContextHelper implements ContextHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContextHelper.class);
    private static final Version NONE = new Version() {
        @Override
        public Context apply(Context context) {
            return context;
        }
    };

    private final IDBuilder idBuilder;
    private final Version version;

    public DefaultContextHelper(final IDBuilder idBuilder) {
        this(NONE, idBuilder);
    }

    public DefaultContextHelper(final Version version, final IDBuilder idBuilder) {
        this.version = checkNotNull(version);
        this.idBuilder = checkNotNull(idBuilder);
    }

    @Override
    public Context createFrom(Map<String, String> contextAsMap) {
        Map<String,String> contextCopy = Maps.newHashMap(contextAsMap);
        String urn = contextCopy.remove(Context.USER_ID_SHORTNAME);
        String kasperCorrelationUuid = contextCopy.get(Context.KASPER_CID_SHORTNAME);
        String sequence = contextCopy.get(Context.SEQ_INC_SHORTNAME);

        Context.Builder builder = new Context.Builder(
                kasperCorrelationUuid == null ? UUID.randomUUID() : UUID.fromString(kasperCorrelationUuid),
                sequence == null ? 1 : Integer.parseInt(sequence)
        );

        if (urn != null && !urn.trim().isEmpty()) {
            try {
                builder.withUserID(idBuilder.build(urn));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Failed to build ID", e);
            }
        }

        for (Map.Entry<String, String> entry : contextCopy.entrySet()) {
            builder.with(entry.getKey(), entry.getValue());
        }

        return version.apply(builder.build());
    }
}
