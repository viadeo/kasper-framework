// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;

public class KasperIdAdapter extends SimpleDeserializers {

    @Override
    public JsonDeserializer findBeanDeserializer(
            final JavaType type,
            final DeserializationConfig config,
            final BeanDescription beanDesc
    ) throws JsonMappingException {

        if (type.hasRawClass(KasperRelationID.class)) {
            return new KasperIdModule.DefaultKasperRelationIdDeserializer();

        } else if (type.hasRawClass(KasperID.class)) {
            return new KasperIdModule.DefaultKasperIdDeserializer();

        } else {
            return super.findBeanDeserializer(type, config, beanDesc);
        }
    }
}
