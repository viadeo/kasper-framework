// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper event dynamic registration at platform boot
 *
 * @see XKasperQueryResult
 */

public class QueryResultsDocumentationProcessor  extends DocumentationProcessor<XKasperQueryResult, QueryResult>{

    private static final Logger LOGGER =LoggerFactory.getLogger(QueryResultsDocumentationProcessor.class);

    /**
    * Annotation is optional for query payloads
    */
    public boolean isAnnotationMandatory(){
        return false;
    }

    /**
     * Process Kasper command
     *
     * @see com.viadeo.kasper.cqrs.query.QueryResult
     * @see AnnotationProcessor#process(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Class queryResultClazz){
        LOGGER.info("Record on queryResult library : " + queryResultClazz.getName());

        //- Register the domain to the locator --------------------------
        getKasperLibrary().recordQueryResult((Class<? extends QueryResult>) queryResultClazz);
    }

}
