// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.platform.IPlatform;

public class KasperPlatformBootListener implements ServletContextListener {

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext ctx = sce.getServletContext();
        final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ctx);

        if (null == springContext) {
            throw new KasperRuntimeException("Unable to find Spring context !");
        }

        final IPlatform platform = springContext.getBean(IPlatform.class);

        if (null == platform) {
            throw new KasperRuntimeException("No Kasper kasper bean found in current Spring context !");
        }

        platform.boot();
    }

}
