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

public class AutoDocumentationSpringConfiguration {

    @Bean
    public KasperLibrary getKasperLibrary() {
        return new KasperLibrary();
    }

    @Bean
    public DomainsDocumentationProcessor getDomainsDocumentationProcessor(final KasperLibrary library) {
        final DomainsDocumentationProcessor proc = new DomainsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public RepositoriesDocumentationProcessor getRepositoriesDocumentationProcessor(final KasperLibrary library) {
        final RepositoriesDocumentationProcessor proc = new RepositoriesDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public CommandsDocumentationProcessor getCommandsDocumentationProcessor(final KasperLibrary library) {
        final CommandsDocumentationProcessor proc = new CommandsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public QueryServicesDocumentationProcessor getQueryServicesDocumentationProcessor(final KasperLibrary library) {
        final QueryServicesDocumentationProcessor proc = new QueryServicesDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public EventsDocumentationProcessor getEventsDocumentationProcessor(final KasperLibrary library) {
        final EventsDocumentationProcessor proc = new EventsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public ConceptsDocumentationProcessor getConceptsDocumentationProcessor(final KasperLibrary library) {
        final ConceptsDocumentationProcessor proc = new ConceptsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public RelationsDocumentationProcessor getRelationsDocumentationProcessor(final KasperLibrary library) {
        final RelationsDocumentationProcessor proc = new RelationsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }


    @Bean
    public ListenersDocumentationProcessor getListenersDocumentationProcessor(final KasperLibrary library) {
        final ListenersDocumentationProcessor proc = new ListenersDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

    @Bean
    public HandlersDocumentationProcessor getHandlersDocumentationProcessor(final KasperLibrary library) {
        final HandlersDocumentationProcessor proc = new HandlersDocumentationProcessor();
        proc.setKasperLibrary(library);
        return proc;
    }

}
