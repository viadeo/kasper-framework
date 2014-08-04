// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.resilience;

import com.netflix.hystrix.*;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.event.Event;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class HystrixHelper {

    private static final String COMMAND_GROUP_NAME = "command";
    private static final String EVENT_GROUP_NAME = "event";
    private static final String QUERY_GROUP_NAME = "query";

    // ------------------------------------------------------------------------

    private HystrixHelper() { /* utility class */ }

    // ------------------------------------------------------------------------

    /**
     * Build setter for a hystrix command for a kasper command<br>
     * Example:
     * <pre>{@code
     * HystrixHelper.buildSetter(myCommand);
     * }
     * </pre>
     * @param command a kasper command
     * @return a setter to build a HystrixCommand
     */
    public static final HystrixCommand.Setter buildSetter(final @NotNull Command command) {
        return buildSpecificSetter(command, COMMAND_GROUP_NAME);
    }

    /**
     * Build setter for a hystrix command for a kasper query<br>
     * Example:
     * <pre>{@code
     * HystrixHelper.buildSetter(myQuery);
     * }
     * </pre>
     * @param query a kasper command
     * @return a setter to build a HystrixCommand
     */
    public static final HystrixCommand.Setter buildSetter(final @NotNull Query query) {
        return buildSpecificSetter(query, QUERY_GROUP_NAME);
    }

    /**
     * Build setter for a hystrix command for a kasper event<br>
     * Example:
     * <pre>{@code
     * HystrixHelper.buildSetter(myEvent);
     * }
     * </pre>
     * @param event a kasper command
     * @return a setter to build a HystrixCommand
     */
    public static final HystrixCommand.Setter buildSetter(final @NotNull Event event) {
        return buildSpecificSetter(event, EVENT_GROUP_NAME);
    }

    /**
     * Build hystrix command setter.
     * @param object an object
     * @param objGroupName the object group logical name (ex: command, event or query)
     * @return hystrix command setter
     * @throws java.lang.NullPointerException if any parameter is null
     */
    private static final HystrixCommand.Setter buildSpecificSetter(final @NotNull Object object, final @NotNull String objGroupName) {
        checkNotNull(object);
        checkNotNull(objGroupName);

        final Class<?> currentClass = object.getClass();

        return HystrixCommand.Setter

                // configure command group and command name
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(objGroupName))
                .andCommandKey(HystrixCommandKey.Factory.asKey(currentClass.getName()))

                // configure thread pool key
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(currentClass.getPackage().getName()))

                // configure circuit breaker
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                            .withCircuitBreakerSleepWindowInMilliseconds(60000) // default to 500 ms
                            .withMetricsRollingStatisticalWindowInMilliseconds(60000) // default to 10000 ms
                            .withMetricsRollingStatisticalWindowBuckets(60) // default to 10
                );
    }

}
