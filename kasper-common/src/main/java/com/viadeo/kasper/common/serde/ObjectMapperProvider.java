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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.viadeo.kasper.api.response.KasperReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class ObjectMapperProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperProvider.class);

    static final String ID = "id";
    static final String REASON = "reason";
    static final String REASONS = "reasons";
    static final String ERROR = "error";
    static final String ERRORS = "errors";
    static final String MESSAGE = "message";
    static final String MESSAGES = "messages";
    static final String CODE = "code";
    static final String LABEL = "label";
    static final String STATUS = "status";

    public static final ObjectMapperProvider INSTANCE = new ObjectMapperProvider();

    private final ObjectMapper mapper;

    // ------------------------------------------------------------------------

    public ObjectMapperProvider() {
        mapper = new ObjectMapper();

        /* Generic features */
        mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
        mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);

        /* Serialization features */
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        /* De-Serialization features */
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /* Change visibility of constructors if needed */
        mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

        /* Register a specific module for Kasper Ser/Deser */
        mapper.registerModule(new KasperIdModule());
        mapper.registerModule(new KasperResponseModule());
        mapper.registerModule(new ImmutabilityModule());

        /* Third-party modules */
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JodaModule());

        /* Kasper extra modules */
        mapper.registerModule(new JodaMoneyModule());
    }

    public static ObjectMapperProvider defaults() {
        return ObjectMapperProvider.INSTANCE;
    }

    // ------------------------------------------------------------------------

    static KasperReason translateOldErrorToKasperReason(final ObjectNode root) {
        final String globalCode = root.get(ObjectMapperProvider.MESSAGE).asText();
        final List<String> messages = new ArrayList<String>();

        for (final JsonNode node : root.get(ObjectMapperProvider.REASONS)) {
            final String code = node.get(ObjectMapperProvider.CODE).asText();
            final String message = node.get(ObjectMapperProvider.MESSAGE).asText();

            if (globalCode.equals(code)) {
                messages.add(message);
            } else {
                LOGGER.warn("Global code[{}] does not match error code[{}] with message[{}]",
                            globalCode, code, message);
            }
        }

        return new KasperReason(globalCode, messages);
    }

    /**
     * @return the configured instance of ObjectWriter to use.
     */
    public ObjectWriter objectWriter() {
        return mapper.writer();
    }

    /**
     * @return the configured instance of ObjectReader to use.
     */
    public ObjectReader objectReader() {
        return mapper.reader();
    }

    /**
     * @return the configured mapper
     */
    public ObjectMapper mapper() {
        return mapper;
    }

}
