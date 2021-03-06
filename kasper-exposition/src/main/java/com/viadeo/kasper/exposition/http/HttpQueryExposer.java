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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.common.exposition.query.QueryFactory;
import com.viadeo.kasper.common.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpQueryExposer extends HttpExposer<Query, QueryHandler, QueryResponse> {
    private static final long serialVersionUID = 8448984922303895624L;

    private final transient QueryGateway queryGateway;

    private final ObjectToHttpServletResponse objectToHttpResponse;
    private final HttpServletRequestToObject httpRequestToObjectWithJson;
    private final HttpServletRequestToObject httpRequestToObjectWithString;

    // ------------------------------------------------------------------------

    public HttpQueryExposer(final Platform platform, final List<ExposureDescriptor<Query,QueryHandler>> descriptors) {
        this(
                platform.getQueryGateway(),
                platform.getMeta(),
                descriptors,
                new QueryFactoryBuilder().create(),
                new SimpleHttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }

    public HttpQueryExposer(final QueryGateway queryGateway,
                            final Meta meta,
                            final List<ExposureDescriptor<Query,QueryHandler>> descriptors,
                            final QueryFactory queryAdapterFactory,
                            final HttpContextDeserializer contextDeserializer,
                            final ObjectMapper mapper) {
        super(contextDeserializer, meta, descriptors);
        this.queryGateway = checkNotNull(queryGateway);

        this.objectToHttpResponse = new ObjectToHttpServletResponse(mapper);
        this.httpRequestToObjectWithJson = new HttpServletRequestToObject.JsonToObjectMapper(mapper);
        this.httpRequestToObjectWithString = new HttpServletRequestToObject.StringRequestToObjectMapper(mapper, queryAdapterFactory);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing queries ===============");

        /* expose all registered queries and commands */
        for (final ExposureDescriptor<Query,QueryHandler> descriptor : getDescriptors()) {
            expose(descriptor);
        }

        LOGGER.info("Total exposed {} queries.", getExposedInputs().size());

        if ( ! getUnexposedInputs().isEmpty()) {
            LOGGER.info("Total unexposed {} queries.", getUnexposedInputs().size());
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObjectWithJson, objectToHttpResponse, req, resp);
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObjectWithString, objectToHttpResponse, req, resp);
    }

    @Override
    protected void checkMediaType(final HttpServletRequest httpRequest) throws HttpExposerException {
        if( "POST".equals(httpRequest.getMethod()) ){
            if( null == httpRequest.getContentType() || ! httpRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE) ){
                throw new HttpExposerException(
                        CoreReasonCode.UNSUPPORTED_MEDIA_TYPE,
                        "Accepting and producing only " + MediaType.APPLICATION_JSON_VALUE
                );
            }
        }
    }

    @Override
    protected QueryResponse createErrorResponse(CoreReasonCode code, List<String> reasons) {
        return QueryResponse.error(new KasperReason(code, reasons));
    }

    @Override
    protected QueryResponse createRefusedResponse(final CoreReasonCode code, final List<String> reasons) {
        return QueryResponse.refused(new KasperReason(code, reasons));
    }

    @Override
    public QueryResponse doHandle(Query query, Context context) throws Exception {
        return queryGateway.retrieve(query, context);
    }

    // ------------------------------------------------------------------------

    @Override
    protected String toPath(final Class<? extends Query> exposedQuery) {
        return Introspector.decapitalize(exposedQuery.getSimpleName().replaceAll("Query", ""));
    }

}
