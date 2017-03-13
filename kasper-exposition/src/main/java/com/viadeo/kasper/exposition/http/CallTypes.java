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

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;

import java.io.Serializable;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CallTypes {

    private CallTypes() { }

    /**
     * Defined a synchronous call
     */
    public static CallType SYNC = new CallTypeAdapter("sync") {
        @Override
        public CommandResponse doCall(
                final CommandGateway gateway,
                final Command command,
                final Context context
        ) throws Exception {
            return gateway.sendCommandAndWaitForAResponse(command, context);
        }
    };

    /**
     * Defined an asynchronous call
     */
    public static CallType ASYNC = new CallTypeAdapter("async") {
        @Override
        public CommandResponse doCall(
                final CommandGateway gateway,
                final Command command,
                final Context context
        ) throws Exception {
            gateway.sendCommand(command, context);
            return CommandResponse.accepted();
        }
    };

    public static Optional<CallType> of(final String type) {
        Optional<CallType> optionalCallType = Optional.absent();

        if (SYNC.name().equals(type)) {
            optionalCallType = Optional.of(SYNC);
        } else if (ASYNC.name().equals(type)) {
            optionalCallType = Optional.of(ASYNC);
        } else {
            final Matcher matcher = TimeCallType.TIME_PATTERN.matcher(type);

            if (matcher.find()) {
                String time = matcher.group(1);
                optionalCallType = Optional.<CallType>of(new TimeCallType(Long.parseLong(time)));
            }
        }

        return optionalCallType;
    }

    public interface CallType extends Serializable {
        String name();
        CommandResponse doCall(CommandGateway gateway, Command command, Context context) throws Exception;
    }

    public static abstract class CallTypeAdapter implements CallType {

        private final String name;

        public CallTypeAdapter(final String name) {
            this.name = checkNotNull(name);
        }

        @Override
        public String name() {
            return name;
        }

        public abstract CommandResponse doCall(final CommandGateway gateway, final Command command, final Context context) throws Exception;

        @Override
        public String toString() {
            return name();
        }
    }

    public static class TimeCallType extends CallTypeAdapter {

        private static final Pattern TIME_PATTERN = Pattern.compile("time\\((\\d+)\\)");
        private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                        .setNameFormat("call-with-limit-time-%d")
                        .build()
        );

        private Long time;

        public TimeCallType(final Long time) {
            super("time");
            this.time = checkNotNull(time);
        }

        public Long getTime() {
            return time;
        }

        @Override
        public CommandResponse doCall(
                final CommandGateway gateway,
                final Command command,
                final Context context
        ) throws Exception {

            final Future<CommandResponse> commandResponseFuture = EXECUTOR.submit(new Callable<CommandResponse>() {
                @Override
                public CommandResponse call() throws Exception {
                    return gateway.sendCommandAndWaitForAResponse(command, context);
                }
            });

            try {
                return commandResponseFuture.get(time, TimeUnit.MILLISECONDS);

            } catch (TimeoutException t) {
                return CommandResponse.accepted();

            } catch (ExecutionException e) {
                if (e.getCause() != null) {
                    throw new RuntimeException(e.getCause());
                } else {
                    throw new RuntimeException(e);
                }

            } finally {
                commandResponseFuture.cancel(false);
            }
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", name(), getTime());
        }
    }
}
