// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.interceptor;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.query.interceptor.validation.QueryValidationInterceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

import static org.junit.Assert.fail;

public class ValidationInterceptorTest {

    public static class QueryToValidate implements Query {
        private static final long serialVersionUID = -2017104008425866649L;

        @NotNull
        @Size(min = 36, max = 36)
        private String field;

        QueryToValidate() { }

        QueryToValidate(final String value) {
            field = value;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testNotNullValidation() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final QueryValidationInterceptor<QueryToValidate, QueryResult> actor = new QueryValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                    new QueryToValidate(),
                    Contexts.empty(),
                    InterceptorChain.<QueryToValidate, QueryResponse<QueryResult>>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then should raise exception
        }
    }

    @Test
    public void testSizeValidation() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final QueryValidationInterceptor<QueryToValidate, QueryResult> actor = new QueryValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                new QueryToValidate("fr"),
                    Contexts.empty(),
                InterceptorChain.<QueryToValidate, QueryResponse<QueryResult>>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then should raise exception
        }
    }

}
