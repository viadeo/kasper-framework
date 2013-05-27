package com.viadeo.kasper.exposition;

import java.util.Map;

import javax.servlet.http.HttpServlet;


import static com.google.common.base.Preconditions.*;

import com.viadeo.kasper.platform.IPlatform;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895624L;

	private IPlatform platform;

	protected HttpExposer(final IPlatform platform) {
	    this.platform = platform;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void _putKey(final String key, final Class newValue,
			final Map mapping) {
		final Class<?> value = (Class<?>) mapping.get(key);
		if (value != null)
			throw new IllegalArgumentException("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
		mapping.put(key, newValue);
	}

	protected final String resourceName(String uri) {
		checkNotNull(uri);
		int idx = uri.lastIndexOf('/');
		if (idx > -1) {
			return uri.substring(idx + 1);
		} else {
			return uri;
		}
	}

	protected final IPlatform platform() {
		return platform;
	}
}
