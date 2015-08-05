// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.command.handler;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.command.NoticeTheWorldCommand;
import com.viadeo.kasper.domain.sample.hello.api.event.NoticeSentToTheWorldEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * We do not implement EntityCommandHandler here in order to demonstrate
 * how to access another repository without the facility of this.getRepository()
 */
@XKasperCommandHandler( /* Required annotation to define the sticked domain */
        domain = HelloDomain.class,
        description = "Send a response to an existing Hello message"
)
public class NoticeTheWorldCommandHandler extends AutowiredCommandHandler<NoticeTheWorldCommand> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeTheWorldCommandHandler.class);
    private final KasperEventBus eventBus;

    @Inject
    public NoticeTheWorldCommandHandler(KasperEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public CommandResponse handle(final NoticeTheWorldCommand command) {

        LOGGER.info(
            "Noticed the world : {}",
            command.getNotice()
        );

        this.eventBus.publish(
            new NoticeSentToTheWorldEvent(
                command.getNotice()
            )
        );

        return CommandResponse.ok();
    }

}
