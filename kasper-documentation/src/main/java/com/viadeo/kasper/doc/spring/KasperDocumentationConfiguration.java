// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.spring;

import com.viadeo.kasper.doc.DocumentationPlugin;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperDocumentationConfiguration {

    @Bean
    public Plugin documentationPlugin() {
        return new DocumentationPlugin();
    }
}
