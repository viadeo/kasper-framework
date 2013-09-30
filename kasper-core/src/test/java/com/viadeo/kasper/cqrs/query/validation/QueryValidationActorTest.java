package com.viadeo.kasper.cqrs.query.validation;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import lombok.Data;
import org.junit.Test;

import javax.validation.constraints.NotNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryValidationActorTest {

    @Test public void testValidation() throws Exception {
        QueryValidationActor<QueryToValidate, QueryPayload> actor = new QueryValidationActor<>();
        QueryResult<QueryPayload> result = actor.process(new QueryToValidate(), null, null);
        assertTrue(result.isError());
        assertEquals("notNullField : ne peut pas être nul", result.getError().getMessages().get(0));
    }

    @Data
    public static class QueryToValidate implements Query {
        @NotNull private String notNullField;
    }
}
