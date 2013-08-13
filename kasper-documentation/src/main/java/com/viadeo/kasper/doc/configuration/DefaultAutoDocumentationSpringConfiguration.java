// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.doc.KasperLibrary;
import org.springframework.context.annotation.Bean;

public class DefaultAutoDocumentationSpringConfiguration extends DefaultAutoDocumentationConfiguration {

    @Bean
    public KasperLibrary getKasperLibrary() {
        return super.getKasperLibrary();
    }

    @Bean
    public DomainsDocumentationProcessor getDomainsDocumentationProcessor(final KasperLibrary library) {
        return super.getDomainsDocumentationProcessor(library);
    }

    @Bean
    public RepositoriesDocumentationProcessor getRepositoriesDocumentationProcessor(final KasperLibrary library) {
        return super.getRepositoriesDocumentationProcessor(library);
    }

    @Bean
    public CommandsDocumentationProcessor getCommandsDocumentationProcessor(final KasperLibrary library) {
        return super.getCommandsDocumentationProcessor(library);
    }

    @Bean
    public QueryServicesDocumentationProcessor getQueryServicesDocumentationProcessor(final KasperLibrary library) {
        return super.getQueryServicesDocumentationProcessor(library);
    }

    @Bean
    public EventsDocumentationProcessor getEventsDocumentationProcessor(final KasperLibrary library) {
        return super.getEventsDocumentationProcessor(library);
    }

    @Bean
    public ConceptsDocumentationProcessor getConceptsDocumentationProcessor(final KasperLibrary library) {
        return super.getConceptsDocumentationProcessor(library);
    }

    @Bean
    public RelationsDocumentationProcessor getRelationsDocumentationProcessor(final KasperLibrary library) {
        return super.getRelationsDocumentationProcessor(library);
    }


    @Bean
    public ListenersDocumentationProcessor getListenersDocumentationProcessor(final KasperLibrary library) {
        return super.getListenersDocumentationProcessor(library);
    }

    @Bean
    public HandlersDocumentationProcessor getHandlersDocumentationProcessor(final KasperLibrary library) {
        return super.getHandlersDocumentationProcessor(library);
    }

}
