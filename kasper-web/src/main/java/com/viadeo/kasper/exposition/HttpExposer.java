// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.platform.IPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.beans.Introspector;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895424L;
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	        
	private IPlatform platform;

    // ------------------------------------------------------------------------

	protected HttpExposer(final IPlatform platform) {
	    this.platform = platform;
	}

    // ------------------------------------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void putKey(final String key, final Class newValue, final Map mapping) {
		final Class<?> value = (Class<?>) mapping.get(key);

		if (null != value) {
			throw new IllegalArgumentException("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
        }

		mapping.put(key, newValue);
	}

    // ------------------------------------------------------------------------

	protected final String resourceName(final String uri) {
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

    // ------------------------------------------------------------------------

	protected final IPlatform platform() {
		return platform;
	}

}
