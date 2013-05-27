package com.viadeo.kasper.exposition;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import static com.google.common.base.Preconditions.*;

import com.viadeo.kasper.platform.IPlatform;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895624L;

	private IPlatform platform;

	protected HttpExposer() {
	}

	@Override
	public final void init(ServletConfig config) throws ServletException {
		// ugly :/
		WebApplicationContext context = checkNotNull(WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext()));
		platform = checkNotNull(context.getBean(IPlatform.class));

		configure(platform, context);
	}

	protected abstract void configure(IPlatform platform,
			WebApplicationContext context);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void _putKey(final String key, final Class newValue,
			final Map mapping) {
		final Class<?> value = (Class<?>) mapping.get(key);
		if (value != null)
			throw new IllegalArgumentException("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
		mapping.put(key, newValue);
	}

	protected String resourceName(String uri) {
		checkNotNull(uri);
		int idx = uri.lastIndexOf('/');
		if (idx > -1) {
			return uri.substring(idx + 1);
		} else {
			return uri;
		}
	}

	protected IPlatform platform() {
		return platform;
	}
}
