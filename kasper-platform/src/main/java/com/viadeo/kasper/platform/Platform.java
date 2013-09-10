// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.core.boot.ComponentsInstanceManager;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.platform.components.eventbus.KasperEventBus;

/**
 * The Kasper platform
 *
 * This interface represent the main entry point to your platform front components,
 * the Command and Query gateways from which your can then send commands and queries,
 * or even send Events.
 *
 * This platform has to be booted before usage.
 *
 * You can use the PlatformFactory in order to build a runnable platform, using
 * the default configuration or providing yours.
 *
 * If you are using Spring, you can also directly add the DefaultPlatformSpringConfiguration
 * bean to your running context, this will allow you to inject the Platform, CommandGateway,
 * EventBus or QueryGateway class beans in your project classes
 *
 * A root processor has to be supplied to the platform during its building, this root processor
 * will be called during the boot process in order to analyze, depending on its settings, parts
 * of your classpath searching for your Kasper components (aka. Handlers, Services, Listeners,
 * Repositories, ...)
 *
 */
public interface Platform {

	/** == Boot ============================================================ */

    /**
     * Boot a Kasper platform
     */
	void boot();

    /**
     * @return true of the platform is already booted
     */
    boolean isBooted();

    /** == Root processor ================================================== */

    /**
     * Sets the root processor to be used during boot time
     *
     * @param rootProcessor the root processor to be used at boot time
     */
	void setRootProcessor(AnnotationRootProcessor rootProcessor);

    /**
     * @return the root processor used by the platform
     */
    AnnotationRootProcessor getRootProcessor();

    /**
     * @return the instances manager used to store all platform's components instance
     */
    ComponentsInstanceManager getComponentsInstanceManager();

	/** == Commands ======================================================== */

    /**
     * Sets the Command gateway to be used by the platform
     *
     * @param commandGateway the command gateway to be used by the platform
     */
	void setCommandGateway(CommandGateway commandGateway);

    /**
     * @return the Command gateway to use in order to send commands to the platform
     */
	CommandGateway getCommandGateway();

    /**
     * Send a command to the platform in a "fire and forget" mode
     *
     * For other ways to send a command to the platform, user preferably the command
     * gateway directly
     *
     * @param command the command to be processed by the platform
     * @param context the command context
     * @throws Exception when something bad occurs
     */
    void sendCommand(Command command, Context context) throws Exception;

	/** == Queries ========================================================= */

    /**
     * Sets the Query gatewayto be used by the platform
     *
     * @param queryGateway the query gateway to be used by the platform
     */
	void setQueryGateway(QueryGateway queryGateway);

    /**
     * @return the query gateway to use in order to send queries to the platform
     */
	QueryGateway getQueryGateway();

    /**
     * Sends a query to the platform, retrieving a result for this query
     *
     * @param query the query to be processed by the platform
     * @param context the query context
     *
     * @return the result generated after processing of the query
     * @throws Exception when something bad occurs
     */
    <PAYLOAD extends QueryPayload> QueryResult<PAYLOAD> retrieve(Query query, Context context) throws Exception;

 	/** == Events ========================================================== */

    /**
     * Sets the event bus to be used by the platform
     *
     * @param eventBus the event bus to be used by the platform
     */
	void setEventBus(KasperEventBus eventBus);

    /**
     * @return the event bus used by the platform
     */
    KasperEventBus getEventBus();

    /**
     * Publish an event to the platform, the event must contains the context
     *
     * @param event
     */
    void publishEvent(Event event);

    /**
     * Send an event to the platform, with a specified context
     * If the event already contains a context, it will be overriden
     *
     * @param event the event to be sent
     * @param context the event context
     */
    void publishEvent(Event event, Context context);

}
