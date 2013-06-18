package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionDTO;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObjectMapperProviderTest {
    
    @Test
    public void queryExceptionRoundTrip() throws IOException {
        final KasperQueryException expected = new KasperQueryException("some message", null,
                Arrays.asList(new KasperError("aCode", "aMessage")));
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();

        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expected);
        final KasperQueryException actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                KasperQueryException.class);

        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getErrors().get().size(), actual.getErrors().get().size());
        
        for (int i = 0; i < expected.getErrors().get().size(); i++) {
            assertEquals(expected.getErrors().get().get(i), actual.getErrors().get().get(i));
        }
    }

    @Test
    public void deserializeSingleKasperError() throws IOException {
        final KasperError expected = new KasperError(KasperError.UNKNOWN_ERROR, "some error");

        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expected);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final KasperError actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                KasperError.class);

        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    public void deserializeErrorCommandResultWithSingleKasperError() throws IOException {
        final KasperError expectedError = new KasperError(KasperError.UNKNOWN_ERROR, "some error");
        final CommandResult expectedResult = CommandResult.error().addError(expectedError).create();

        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expectedResult);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                CommandResult.class);

        assertEquals(expectedResult.getStatus(), actualResult.getStatus());

        assertEquals(expectedResult.getErrors().get().get(0).getCode(), actualResult.getErrors().get().get(0).getCode());
        assertEquals(expectedResult.getErrors().get().get(0).getMessage(), actualResult.getErrors().get().get(0)
                .getMessage());
    }

    @Test
    public void deserializeErrorCommandResultWithMultipleKasperError() throws IOException {
        final List<KasperError> expectedErrors = Arrays.asList(new KasperError(KasperError.CONFLICT, "too late..."),
                new KasperError(KasperError.UNKNOWN_ERROR, "some error"));

        final CommandResult expectedResult = CommandResult.error().addErrors(expectedErrors).create();

        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expectedResult);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                CommandResult.class);

        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedErrors.size(), actualResult.getErrors().get().size());

        for (int i = 0; i < expectedErrors.size(); i++) {
            assertEquals(expectedResult.getErrors().get().get(i).getCode(), actualResult.getErrors().get().get(i)
                    .getCode());
            assertEquals(expectedResult.getErrors().get().get(i).getMessage(), actualResult.getErrors().get().get(i)
                    .getMessage());
        }
    }

    @Test
    public void deserializeErrorCommandResultWithNoKasperError() throws IOException {
        final CommandResult expectedResult = CommandResult.error().create();

        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expectedResult);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                CommandResult.class);

        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedResult.getErrors().get().size(), actualResult.getErrors().get().size());
    }

    @Test
    public void dontFailOnUnknownProperty() throws IOException {
        final SomeCollectionDTO dto = new SomeCollectionDTO();
        dto.setList(Arrays.asList(new SomeDTO(), new SomeDTO()));

        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(dto);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final SomeCollectionDTO actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                SomeCollectionDTO.class);

        assertEquals(dto.getCount(), actual.getCount());
    }

    static class SomeDTO implements IQueryDTO {
        private static final long serialVersionUID = -3621610243017076348L;

        public String getStr() {
            return "str";
        }
    }

    static class SomeCollectionDTO extends AbstractQueryCollectionDTO<SomeDTO> {
        private static final long serialVersionUID = 8849846911146025322L;

    }
}
