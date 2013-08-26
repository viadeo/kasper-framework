// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.QueryResult;

class CommandQueryResultDeserializerAdapter extends SimpleDeserializers {
    private static final long serialVersionUID = 1995270375280248186L;

    @Override
    public JsonDeserializer<?> findBeanDeserializer(final JavaType type,
                                                    final DeserializationConfig config,
                                                    final BeanDescription beanDesc)
            throws JsonMappingException {

        if (type.hasRawClass(QueryResult.class)) {
            return new QueryResultDeserializer(type.containedType(0));

        } else if (type.hasRawClass(CommandResult.class)) {
            return new CommandResultDeserializer();

        } else {
            return super.findBeanDeserializer(type, config, beanDesc);
        }
    }

}
