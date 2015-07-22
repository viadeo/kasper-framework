package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletRequestToObjectUTest {

    public static class TestQuery implements Query { }

    public static class TestQueryWithParameters implements Query {

        public TestQueryWithParameters(String parameter) { }
    }

    private HttpServletRequestToObject.JsonToObjectMapper mapper;
    private HttpServletRequest request;
    private String payload;

    @Before
    public void setUp() throws Exception {
        mapper = new HttpServletRequestToObject.JsonToObjectMapper(ObjectMapperProvider.INSTANCE.mapper());
        request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        });
        payload = "";
    }

    @Test
    public void map_withJsonToObjectMapper_withEmptyInput_forQueryWithoutParameters_isOk() throws Exception {
        // When
        TestQuery query = mapper.map(request, payload, TestQuery.class);

        // Then
        Assert.assertNotNull(query);
    }

    @Test(expected = InstantiationException.class)
    public void map_withJsonToObjectMapper_withEmptyInput_forQueryWithParameters_isKo() throws Exception {
        mapper.map(request, payload, TestQueryWithParameters.class);
    }
}
