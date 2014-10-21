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
import com.viadeo.kasper.query.exposition.query.QueryFactory;
import com.viadeo.kasper.query.exposition.query.QueryParser;

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

    public abstract <T> T map(final HttpServletRequest request, final Class<T> clazz) throws IOException;

    public static class StringRequestToObjectMapper extends HttpServletRequestToObject {

        private final QueryFactory factory;

        protected StringRequestToObjectMapper(final ObjectMapper objectMapper, final QueryFactory factory) {
            super(objectMapper);
            this.factory = factory;
        }

        @Override
        public <T> T map(final HttpServletRequest request, final Class<T> clazz) throws IOException {
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

        public <T> T map(final HttpServletRequest request, final Class<T> clazz) throws IOException {
            try (final JsonParser parser = reader.getFactory().createParser(request.getInputStream())) {
                return reader.readValue(parser, clazz);
            }
        }
    }

}
