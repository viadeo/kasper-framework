// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper query answer dynamic registration at platform boot
 *
 * @see XKasperQueryAnswer
 */

public class QueryAnswersDocumentationProcessor extends DocumentationProcessor<XKasperQueryAnswer, QueryAnswer>{

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryAnswersDocumentationProcessor.class);

    /**
    * Annotation is optional for query answers
    */
    public boolean isAnnotationMandatory(){
        return false;
    }

    /**
     * Process Kasper query answer
     *
     * @see com.viadeo.kasper.cqrs.query.QueryAnswer
     * @see AnnotationProcessor#process(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Class queryAnswerClazz) {
        LOGGER.info("Record on queryAnswer library : " + queryAnswerClazz.getName());

        //- Register the domain to the locator --------------------------
        getKasperLibrary().recordQueryAnswer((Class<? extends QueryAnswer>) queryAnswerClazz);
    }

}
