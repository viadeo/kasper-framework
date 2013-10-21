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
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryValidationActorTest {

    @Test
    public void testValidation() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final QueryValidationActor<QueryToValidate, QueryResult> actor = new QueryValidationActor<>(Validation.buildDefaultValidatorFactory());

        // When
        final QueryResponse<QueryResult> response = actor.process(
                new QueryToValidate(),
                new DefaultContextBuilder().build(),
                RequestActorsChain.<QueryToValidate, QueryResponse<QueryResult>>tail());

        // Then
        assertTrue(response.isError());
        assertEquals("notNullField : may not be null", response.getError().getMessages().toArray()[0]);
    }

    @Data
    public static class QueryToValidate implements Query {
        @NotNull
        private String notNullField;
    }
}
