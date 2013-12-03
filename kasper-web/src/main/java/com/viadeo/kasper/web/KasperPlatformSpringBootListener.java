// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.web;

import com.viadeo.kasper.client.platform.OldPlatform;
import com.viadeo.kasper.exception.KasperException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/*
 * This is a servlet listener that can be used to boot a Spring-configured platform
 */
public class KasperPlatformSpringBootListener implements ServletContextListener {

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        /* do nothing */
    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext ctx = sce.getServletContext();
        final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ctx);

        if (null == springContext) {
            throw new KasperException("Unable to find Spring context !");
        }

        final OldPlatform platform = springContext.getBean(OldPlatform.class);

        if (null == platform) {
            throw new KasperException("No Kasper kasper bean found in current Spring context !");
        }

        platform.boot();
    }

}
