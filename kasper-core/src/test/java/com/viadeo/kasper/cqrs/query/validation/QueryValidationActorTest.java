// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.validation;

import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import lombok.Data;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

import static org.junit.Assert.fail;

public class QueryValidationActorTest {

    @Test
    public void testNotNullValidation() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final QueryValidationActor<QueryToValidate, QueryResult> actor = new QueryValidationActor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                    new QueryToValidate(),
                    new DefaultContextBuilder().build(),
                    RequestActorsChain.<QueryToValidate, QueryResponse<QueryResult>>tail()
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
        final QueryValidationActor<QueryToValidate, QueryResult> actor = new QueryValidationActor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                new QueryToValidate("fr"),
                new DefaultContextBuilder().build(),
                RequestActorsChain.<QueryToValidate, QueryResponse<QueryResult>>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then should raise exception
        }
    }

    @Data
    public static class QueryToValidate implements Query {
        @NotNull
        @Size(min = 36, max = 36)
        private String field;

        QueryToValidate() { }

        QueryToValidate(final String value) {
            field = value;
        }
    }
}
