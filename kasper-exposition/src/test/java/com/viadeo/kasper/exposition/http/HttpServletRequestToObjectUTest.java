// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
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
        assertNotNull(query);
    }

    @Test(expected = InstantiationException.class)
    public void map_withJsonToObjectMapper_withEmptyInput_forQueryWithParameters_isKo() throws Exception {
        mapper.map(request, payload, TestQueryWithParameters.class);
    }
}
