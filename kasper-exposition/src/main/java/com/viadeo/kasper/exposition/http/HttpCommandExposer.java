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
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposer extends HttpExposer<Command, CommandHandler, CommandResponse> {

    private static final long serialVersionUID = 8444284922303895624L;

    private final transient CommandGateway commandGateway;

    private final ObjectToHttpServletResponse objectToHttpResponse;
    private final HttpServletRequestToObject httpRequestToObject;

    // ------------------------------------------------------------------------

    public HttpCommandExposer(final Platform platform,
                              final List<ExposureDescriptor<Command,CommandHandler>> descriptors) {
        this(
                platform.getCommandGateway(),
                platform.getMeta(),
                descriptors,
                new SimpleHttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }
    
    public HttpCommandExposer(final CommandGateway commandGateway,
                              final Meta meta,
                              final List<ExposureDescriptor<Command,CommandHandler>> descriptors,
                              final HttpContextDeserializer contextDeserializer,
                              final ObjectMapper mapper) {
        super(contextDeserializer, meta, descriptors);
        this.commandGateway = checkNotNull(commandGateway);

        this.httpRequestToObject = new HttpServletRequestToObject.JsonToObjectMapper(mapper);
        this.objectToHttpResponse = new ObjectToHttpServletResponse(mapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing commands ===============");

        for (final ExposureDescriptor<Command,CommandHandler> descriptor : getDescriptors()) {
            expose(descriptor);
        }

        LOGGER.info("Total exposed {} commands.", getExposedInputs().size());

        if ( ! getUnexposedInputs().isEmpty()) {
            LOGGER.info("Total unexposed {} commands.", getUnexposedInputs().size());
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObject, objectToHttpResponse, req, resp);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObject, objectToHttpResponse, req, resp);
    }

    @Override
    protected CommandResponse createErrorResponse(final CoreReasonCode code, final List<String> reasons) {
        return CommandResponse.error(new KasperReason(code, reasons));
    }

    @Override
    protected CommandResponse createRefusedResponse(final CoreReasonCode code, final List<String> reasons) {
        return CommandResponse.refused(new KasperReason(code, reasons));
    }

    @Override
    public CommandResponse doHandle(final Command command, final Context context) throws Exception {
        CallTypes.CallType callType = CallTypes.SYNC;

        final Optional<Serializable> optionalProperty = context.getProperty(Context.CALL_TYPE);

        if (optionalProperty.isPresent()) {
            final String callTypeAsString = String.valueOf(optionalProperty.get());
            final Optional<CallTypes.CallType> optionalCallType = CallTypes.of(callTypeAsString);

            if (optionalCallType.isPresent()) {
                callType = optionalCallType.get();
            }
        }

        return callType.doCall(commandGateway, command, context);
    }

    @Override
    protected void checkMediaType(final HttpServletRequest httpRequest) throws HttpExposerException {
        if ((null == httpRequest.getContentType()) || ( ! httpRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE))) {
            throw new HttpExposerException(
                    CoreReasonCode.UNSUPPORTED_MEDIA_TYPE,
                    "Accepting and producing only " + MediaType.APPLICATION_JSON_VALUE
            );
        }
    }

    @Override
    protected void sendResponse(
            final HttpServletResponse httpResponse,
            final ObjectToHttpServletResponse objectToHttpResponse,
            final CommandResponse response,
            final UUID kasperCorrelationUUID
    ) throws IOException {
        if (response.isOK()) {
            if (response.getSecurityToken().isPresent()) {
                httpResponse.addHeader(HttpContextHeaders.HEADER_SECURITY_TOKEN.toHeaderName(), response.getSecurityToken().get());
            }
            if (response.getAccessToken().isPresent()) {
                httpResponse.addHeader(HttpContextHeaders.HEADER_ACCESS_TOKEN.toHeaderName(), response.getAccessToken().get());
            }
            if (response.getAuthenticationToken().isPresent()) {
                httpResponse.addHeader(HttpContextHeaders.HEADER_AUTHENTICATION_TOKEN.toHeaderName(), response.getAuthenticationToken().get().toString());
            }
        }
        super.sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);
    }

    // ------------------------------------------------------------------------

    @Override
    protected String toPath(final Class<? extends Command> exposedCommand) {
        return Introspector.decapitalize(exposedCommand.getSimpleName().replaceAll("Command", ""));
    }

}
