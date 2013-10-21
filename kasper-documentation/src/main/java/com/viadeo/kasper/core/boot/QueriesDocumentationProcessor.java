// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper event dynamic registration at platform boot
 *
 * @see XKasperQuery
 */
public class QueriesDocumentationProcessor extends DocumentationProcessor<XKasperQuery,Query>{

    private static final Logger LOGGER =LoggerFactory.getLogger(QueriesDocumentationProcessor.class);

    /**
     * Annotation is optional for queries
     */
    public boolean isAnnotationMandatory(){
        return false;
    }

    /**
     * Process Kasper command
     *
     * @see com.viadeo.kasper.cqrs.query.Query
     * @see AnnotationProcessor#process(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Class queryClazz){
        LOGGER.info("Record on query library : " + queryClazz.getName());

        //- Register the domain to the locator --------------------------
        getKasperLibrary().recordQuery((Class<? extends Query>) queryClazz);
    }

}
