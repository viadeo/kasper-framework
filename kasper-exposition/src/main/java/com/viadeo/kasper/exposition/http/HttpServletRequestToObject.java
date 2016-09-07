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
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.common.exposition.query.QueryFactory;
import com.viadeo.kasper.common.exposition.query.QueryParser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

public abstract class HttpServletRequestToObject {

    protected final ObjectReader reader;

    // ------------------------------------------------------------------------

    protected HttpServletRequestToObject(final ObjectMapper objectMapper) {
        this.reader = objectMapper.reader();
    }

    // ------------------------------------------------------------------------

    public abstract <T> T map(
            final HttpServletRequest request
            , final String payload
            , final Class<T> clazz
    ) throws Exception;

    public static class StringRequestToObjectMapper extends HttpServletRequestToObject {

        private final QueryFactory factory;

        protected StringRequestToObjectMapper(final ObjectMapper objectMapper, final QueryFactory factory) {
            super(objectMapper);
            this.factory = factory;
        }

        @Override
        public <T> T map(final HttpServletRequest request, final String payload, final Class<T> clazz) throws IOException {
            final ImmutableSetMultimap.Builder<String, String> params = new ImmutableSetMultimap.Builder<>();
            final Enumeration<String> keys = request.getParameterNames();

            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                params.putAll(key, Arrays.asList(request.getParameterValues(key)));
            }

            try {
                return factory.create(TypeToken.of(clazz)).adapt(new QueryParser(params.build()));
            } catch (final Throwable t) {
                throw new RuntimeException(
                        String.format(
                                "Unable to parse input to [%s] with parameters [%s]",
                                clazz,
                                request.getQueryString()
                        ),
                        t
                );
            }
        }
    }

    public static class JsonToObjectMapper extends HttpServletRequestToObject {

        protected JsonToObjectMapper(final ObjectMapper objectMapper) {
            super(objectMapper);
        }

        public <T> T map(final HttpServletRequest request, final String payload, final Class<T> clazz) throws Exception {
            if (payload == null || payload.trim().length() == 0) {
                return clazz.newInstance();
            }
            try (final JsonParser parser = reader.getFactory().createParser(payload)) {
                return reader.readValue(parser, clazz);
            }
        }
    }

}
