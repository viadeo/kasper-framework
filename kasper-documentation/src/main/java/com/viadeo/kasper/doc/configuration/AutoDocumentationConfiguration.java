// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.doc.KasperLibrary;

interface AutoDocumentationConfiguration {

     KasperLibrary getKasperLibrary();

     DomainsDocumentationProcessor getDomainsDocumentationProcessor( KasperLibrary library);

     RepositoriesDocumentationProcessor getRepositoriesDocumentationProcessor( KasperLibrary library);

     CommandsDocumentationProcessor getCommandsDocumentationProcessor( KasperLibrary library);

     QueryServicesDocumentationProcessor getQueryServicesDocumentationProcessor( KasperLibrary library);

     EventsDocumentationProcessor getEventsDocumentationProcessor( KasperLibrary library);

     ConceptsDocumentationProcessor getConceptsDocumentationProcessor( KasperLibrary library);

     RelationsDocumentationProcessor getRelationsDocumentationProcessor( KasperLibrary library);

     ListenersDocumentationProcessor getListenersDocumentationProcessor( KasperLibrary library);

     HandlersDocumentationProcessor getHandlersDocumentationProcessor( KasperLibrary library);

     QueriesDocumentationProcessor getQueriesDocumentationProcessor( KasperLibrary library);

     QueryPayloadsDocumentationProcessor getQueryPayloadsDocumentationProcessor( KasperLibrary library);

}
