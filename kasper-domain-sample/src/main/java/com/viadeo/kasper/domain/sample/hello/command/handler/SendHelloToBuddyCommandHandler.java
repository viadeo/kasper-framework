// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.command.handler;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredEntityCommandHandler;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.command.SendHelloToBuddyCommand;
import com.viadeo.kasper.domain.sample.hello.command.entity.Hello;
import com.viadeo.kasper.domain.sample.hello.command.repository.HelloRepository;

import javax.inject.Inject;

@XKasperCommandHandler( /* Required annotation to define the sticked domain */
        domain = HelloDomain.class,
        description = "Submit a new Hello message to a specified buddy"
)
public class SendHelloToBuddyCommandHandler extends AutowiredEntityCommandHandler<SendHelloToBuddyCommand, Hello> {

    private HelloRepository repository;

    @Inject
    public SendHelloToBuddyCommandHandler(HelloRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommandResponse handle(final SendHelloToBuddyCommand command) {

        /**
         * Just check for aggregate presence
         */
        if (repository.has(command.getIdToUse())) {
            return CommandResponse.error(
                CoreReasonCode.CONFLICT,
                "An hello message already exists for this id"
            );
        }

        /**
         * Creates the new aggregate
         */
        final Hello newHello = new Hello(
            command.getIdToUse(),
            command.getMessage(),
            command.getForBuddy()
        );

        /**
         * Checks whether this buddy has not already sent the same hello message
         * using the dedicated business method from the repository
         */
        if ( repository.hasTheSameMessage(newHello)) {
            return CommandResponse.error(
                CoreReasonCode.CONFLICT,
                "This buddy already sent the same hello message"
            );
        }

        /**
         * Add the new aggregate to its repository
         */
        repository.add(newHello);

        return CommandResponse.ok();
    }

}
