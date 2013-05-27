// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.platform.IPlatform;

import javax.servlet.http.HttpServlet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895624L;

	private IPlatform platform;

	protected HttpExposer(final IPlatform platform) {
	    this.platform = platform;
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

		final int idx = uri.lastIndexOf('/');
		if (-1 < idx) {
			return uri.substring(idx + 1);
		} else {
			return uri;
		}
	}

	protected final IPlatform platform() {
		return platform;
	}

}
