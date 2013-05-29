// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.viadeo.kasper.platform.IPlatform;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895624L;
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	        
	private IPlatform platform;

	protected HttpExposer(final IPlatform platform) {
	    this.platform = platform;
	}

    // ------------------------------------------------------------------------

    @SuppressWarnings("deprecation")
    protected void sendError(final int status, final String message, final HttpServletResponse resp,
            final Throwable exception) throws IOException {

        if (exception != null) {
            LOGGER.error(message, exception);
        } else {
            LOGGER.error(message);
        }

        resp.setStatus(status, message);

        final ObjectWriter writer = ObjectMapperProvider.instance.objectWriter();

        try (final JsonGenerator generator = writer.getJsonFactory().createGenerator(resp.getOutputStream())) {

            generator.writeStartObject();
            // FIXME for the moment lets just put the minimum here
            generator.writeNumberField("code", status);
            generator.writeStringField("reason", message);
            generator.writeEndObject();

        }

        resp.flushBuffer();
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void putKey(final String key, final Class newValue,
			final Map mapping) {
		final Class<?> value = (Class<?>) mapping.get(key);

		if (value != null) {
			throw new IllegalArgumentException("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
        }

		mapping.put(key, newValue);
	}

	protected final String resourceName(String uri) {
		checkNotNull(uri);

        final String resName;

		final int idx = uri.lastIndexOf('/');
		if (-1 < idx) {
			resName = uri.substring(idx + 1);
		} else {
			resName = uri;
		}

        return Introspector.decapitalize(resName);
	}

	protected final IPlatform platform() {
		return platform;
	}

}
