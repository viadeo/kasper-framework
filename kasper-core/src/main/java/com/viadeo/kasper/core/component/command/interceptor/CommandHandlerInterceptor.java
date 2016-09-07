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
package com.viadeo.kasper.core.component.command.interceptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.command.gateway.AxonCommandHandler;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.axonframework.commandhandling.DefaultInterceptorChain;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.unitofwork.CurrentUnitOfWork;

import java.util.List;
import java.util.Map;

public class CommandHandlerInterceptor implements Interceptor<Command, CommandResponse> {

    private final AxonCommandHandler handler;

    public CommandHandlerInterceptor(final AxonCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public CommandResponse process(
            final Command command,
            final Context context,
            final InterceptorChain<Command, CommandResponse> chain
    ) {

        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, context);

        final GenericCommandMessage newCommandMessage =
                new GenericCommandMessage<>(command).withMetaData(metaData);

        try {
            final List<? extends org.axonframework.commandhandling.CommandHandlerInterceptor> interceptors = Lists.newArrayList();
            return CommandResponse.class.cast(
                    new DefaultInterceptorChain(newCommandMessage, CurrentUnitOfWork.get(), handler, interceptors).proceed()
            );
        } catch (final Throwable throwable) {
            throw new KasperException(throwable);
        }
    }

}
