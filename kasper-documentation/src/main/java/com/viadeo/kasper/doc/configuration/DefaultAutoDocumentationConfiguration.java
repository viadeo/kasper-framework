// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.doc.KasperLibrary;

public class DefaultAutoDocumentationConfiguration implements AutoDocumentationConfiguration {

    private AnnotationRootProcessor rootProcessor;
    private KasperLibrary kasperLibrary;

    // ------------------------------------------------------------------------

    public void registerToRootProcessor(final AnnotationRootProcessor rootProcessor) {
        this.rootProcessor = rootProcessor;

        final KasperLibrary library = this.getKasperLibrary();

        this.getDomainsDocumentationProcessor(library);
        this.getRepositoriesDocumentationProcessor(library);
        this.getCommandsDocumentationProcessor(library);
        this.getQueryServicesDocumentationProcessor(library);
        this.getEventsDocumentationProcessor(library);
        this.getConceptsDocumentationProcessor(library);
        this.getRelationsDocumentationProcessor(library);
        this.getListenersDocumentationProcessor(library);
        this.getHandlersDocumentationProcessor(library);
        this.getQueriesDocumentationProcessor(library);
        this.getQueryPayloadsDocumentationProcessor(library);
    }

    // ------------------------------------------------------------------------

    protected <P extends AnnotationProcessor<?,?>> P registerProcessor(final P processor) {
        if (null != this.rootProcessor) {
            this.rootProcessor.registerProcessor(processor);
        }
        return processor;
    }

    // ------------------------------------------------------------------------

    public KasperLibrary getKasperLibrary() {
        if (null == this.kasperLibrary) {
            this.kasperLibrary = new KasperLibrary();
        }
        return this.kasperLibrary;
    }

    public DomainsDocumentationProcessor getDomainsDocumentationProcessor(final KasperLibrary library) {
        final DomainsDocumentationProcessor proc = new DomainsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public RepositoriesDocumentationProcessor getRepositoriesDocumentationProcessor(final KasperLibrary library) {
        final RepositoriesDocumentationProcessor proc = new RepositoriesDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public CommandsDocumentationProcessor getCommandsDocumentationProcessor(final KasperLibrary library) {
        final CommandsDocumentationProcessor proc = new CommandsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public QueryServicesDocumentationProcessor getQueryServicesDocumentationProcessor(final KasperLibrary library) {
        final QueryServicesDocumentationProcessor proc = new QueryServicesDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public EventsDocumentationProcessor getEventsDocumentationProcessor(final KasperLibrary library) {
        final EventsDocumentationProcessor proc = new EventsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public ConceptsDocumentationProcessor getConceptsDocumentationProcessor(final KasperLibrary library) {
        final ConceptsDocumentationProcessor proc = new ConceptsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public RelationsDocumentationProcessor getRelationsDocumentationProcessor(final KasperLibrary library) {
        final RelationsDocumentationProcessor proc = new RelationsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public ListenersDocumentationProcessor getListenersDocumentationProcessor(final KasperLibrary library) {
        final ListenersDocumentationProcessor proc = new ListenersDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public HandlersDocumentationProcessor getHandlersDocumentationProcessor(final KasperLibrary library) {
        final HandlersDocumentationProcessor proc = new HandlersDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public QueriesDocumentationProcessor getQueriesDocumentationProcessor(final KasperLibrary library){
        final QueriesDocumentationProcessor proc= new QueriesDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

    public QueryPayloadsDocumentationProcessor getQueryPayloadsDocumentationProcessor(final KasperLibrary library){
        final QueryPayloadsDocumentationProcessor proc=new QueryPayloadsDocumentationProcessor();
        proc.setKasperLibrary(library);
        return this.registerProcessor(proc);
    }

}
