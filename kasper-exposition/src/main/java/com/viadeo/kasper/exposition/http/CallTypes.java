package com.viadeo.kasper.exposition.http;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.api.component.command.CommandResponse;

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
        private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

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
                    throw Throwables.propagate(e.getCause());
                } else {
                    throw Throwables.propagate(e);
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
