// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.doc.KasperLibrary;
import org.springframework.context.annotation.Bean;

public class DefaultAutoDocumentationSpringConfiguration extends DefaultAutoDocumentationConfiguration {

    @Bean
    @Override
    public KasperLibrary getKasperLibrary(final ResolverFactory resolverFactory) {
        return super.getKasperLibrary(resolverFactory);
    }

    @Bean
    @Override
    public DomainsDocumentationProcessor getDomainsDocumentationProcessor(final KasperLibrary library) {
        return super.getDomainsDocumentationProcessor(library);
    }
    @Bean
    @Override
    public RepositoriesDocumentationProcessor getRepositoriesDocumentationProcessor(final KasperLibrary library) {
        return super.getRepositoriesDocumentationProcessor(library);
    }

    @Bean
    @Override
    public CommandsDocumentationProcessor getCommandsDocumentationProcessor(final KasperLibrary library) {
        return super.getCommandsDocumentationProcessor(library);
    }

    @Bean
    @Override
    public QueryServicesDocumentationProcessor getQueryServicesDocumentationProcessor(final KasperLibrary library) {
        return super.getQueryServicesDocumentationProcessor(library);
    }

    @Bean
    @Override
    public EventsDocumentationProcessor getEventsDocumentationProcessor(final KasperLibrary library) {
        return super.getEventsDocumentationProcessor(library);
    }

    @Bean
    @Override
    public ConceptsDocumentationProcessor getConceptsDocumentationProcessor(final KasperLibrary library) {
        return super.getConceptsDocumentationProcessor(library);
    }

    @Bean
    @Override
    public RelationsDocumentationProcessor getRelationsDocumentationProcessor(final KasperLibrary library) {
        return super.getRelationsDocumentationProcessor(library);
    }

    @Bean
    @Override
    public ListenersDocumentationProcessor getListenersDocumentationProcessor(final KasperLibrary library) {
        return super.getListenersDocumentationProcessor(library);
    }

    @Bean
    @Override
    public HandlersDocumentationProcessor getHandlersDocumentationProcessor(final KasperLibrary library) {
        return super.getHandlersDocumentationProcessor(library);
    }

}
