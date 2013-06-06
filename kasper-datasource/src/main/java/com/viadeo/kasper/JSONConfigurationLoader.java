// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class JSONConfigurationLoader {

    static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
    static ResourcesUtils res = new ResourcesUtils();

    // version jackson 2
    static {
        JSON_OBJECT_MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        JSON_OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        JSON_OBJECT_MAPPER.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        JSON_OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JSON_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        JSON_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JSON_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    // ------------------------------------------------------------------------

    public static <T> T load(final File path, final Class<T> clazz) {
        try {
            return JSON_OBJECT_MAPPER.readValue(path, clazz);
        } catch (final Exception e) {
            throw new RuntimeException("The settings file " + path.getAbsolutePath() + " cannot be parsed", e);
        }
    }

    static public File getFile(final String filename) throws IOException  {
        return res.getFile(filename);
    }

}
