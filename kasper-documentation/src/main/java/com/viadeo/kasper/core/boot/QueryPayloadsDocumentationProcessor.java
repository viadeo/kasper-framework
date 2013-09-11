// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper event dynamic registration at platform boot
 *
 * @see XKasperQueryPayload
 */

public class QueryPayloadsDocumentationProcessor  extends DocumentationProcessor<XKasperQueryPayload,QueryPayload>{

    private static final Logger LOGGER =LoggerFactory.getLogger(QueryPayloadsDocumentationProcessor.class);

    /**
    * Annotation is optional for query payloads
    */
    public boolean isAnnotationMandatory(){
        return false;
    }

    /**
     * Process Kasper command
     *
     * @see com.viadeo.kasper.cqrs.query.QueryPayload
     * @see AnnotationProcessor#process(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Class<?> queryPayloadClazz){
        LOGGER.info("Record on queryPayload library : " + queryPayloadClazz.getName());

        //- Register the domain to the locator --------------------------
        getKasperLibrary().recordQueryPayload((Class<? extends QueryPayload>) queryPayloadClazz);
    }

}
