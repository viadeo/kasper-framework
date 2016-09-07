// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.DefaultKasperRelationId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.id.KasperRelationID;

import java.io.IOException;

public class KasperIdModule extends SimpleModule {

    public KasperIdModule() {
        super();
        setDeserializers(new KasperIdAdapter());
        addDeserializer(DefaultKasperId.class, new DefaultKasperIdDeserializer());
        addDeserializer(DefaultKasperRelationId.class, new DefaultKasperRelationIdDeserializer());
        addSerializer(DefaultKasperId.class, new DefaultKasperIdSerializer());
        addSerializer(DefaultKasperRelationId.class, new DefaultKasperRelationIdSerializer());
    }

    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------

    public static class DefaultKasperRelationIdSerializer extends JsonSerializer<DefaultKasperRelationId> {
        @Override
        public void serialize(
                final DefaultKasperRelationId value,
                final JsonGenerator jsongenerator,
                final SerializerProvider provider
        ) throws IOException {
            jsongenerator.writeString(value.toString());
        }
    }

    // ------------------------------------------------------------------------

    public static final class DefaultKasperRelationIdDeserializer extends JsonDeserializer<DefaultKasperRelationId> {
        @Override
        public DefaultKasperRelationId deserialize(
                final JsonParser jp,
                final DeserializationContext context
        ) throws IOException {
            final String textValue = jp.getText();
            return new DefaultKasperRelationId(textValue);
        }
    }

    // ------------------------------------------------------------------------

    public static class DefaultKasperIdSerializer extends JsonSerializer<DefaultKasperId> {
        @Override
        public void serialize(
                final DefaultKasperId value,
                final JsonGenerator jsongenerator,
                final SerializerProvider provider
        ) throws IOException {
            jsongenerator.writeString(value.toString());
        }
    }

    // ------------------------------------------------------------------------

    public static final class DefaultKasperIdDeserializer extends JsonDeserializer<DefaultKasperId> {
        @Override
        public DefaultKasperId deserialize(final JsonParser jp, final DeserializationContext context) throws IOException {
            final String textValue = jp.getText();
            return new DefaultKasperId(textValue);
        }
    }
}
