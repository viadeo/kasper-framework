// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.beans.Introspector;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class HttpExposer extends HttpServlet {
	private static final long serialVersionUID = 8448984922303895424L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(HttpExposer.class);

    private Optional<String> serverName = Optional.absent();

    // ------------------------------------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void putKey(final String key, final Class newValue, final Map mapping) {
		final Class value = (Class) mapping.get(key);

		if (null != value) {
			throw new HttpExposerError("Duplicate entry for name="
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

    protected String getFullRequestURI(final HttpServletRequest req){
        final StringBuilder sb = new StringBuilder();
        sb.append(req.getRequestURI());

        final String queryString = req.getQueryString();
        if (StringUtils.hasText(queryString)){
            sb.append("?");
            sb.append(queryString);
        }

        return sb.toString();
    }

    protected String serverName(){
        if(serverName.isPresent()){
            return serverName.get();
        }

        String fqdn;
        try {
            fqdn = InetAddress.getLocalHost().getCanonicalHostName();
            serverName = Optional.of(fqdn);
        } catch (UnknownHostException e) {
            fqdn = "unknown";
        }
        return fqdn;
    }

}
