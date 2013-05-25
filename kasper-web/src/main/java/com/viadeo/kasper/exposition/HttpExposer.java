// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.platform.IPlatform;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895624L;

	private IPlatform platform;

    // ------------------------------------------------------------------------

	protected HttpExposer() { }

    // ------------------------------------------------------------------------

	@Override
	public final void init(final ServletConfig config) throws ServletException {
		// ugly :/
        final ServletContext context = config.getServletContext();
        final WebApplicationContext wcontext = WebApplicationContextUtils.getWebApplicationContext(context);

		platform = checkNotNull(wcontext.getBean(IPlatform.class));

		configure(platform, wcontext);
	}

	protected abstract void configure(IPlatform platform, WebApplicationContext context);

    // ------------------------------------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void putKey(final String key, final Class newValue, final Map mapping) {
		final Class<?> value = (Class<?>) mapping.get(key);

		if (value != null) {
			throw new IllegalArgumentException("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
        }

		mapping.put(key, newValue);
	}

    // ------------------------------------------------------------------------

	protected String resourceName(final String uri) {
		checkNotNull(uri);

		final int idx = uri.lastIndexOf('/');
		if (-1 < idx) {
			return uri.substring(idx + 1);
		} else {
			return uri;
		}
	}

    // ------------------------------------------------------------------------

	protected IPlatform platform() {
		return platform;
	}

}
