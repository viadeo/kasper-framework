// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.use_case_1;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCreateCommand;
import com.viadeo.kasper.cqrs.command.impl.AbstractEntityCommandHandler;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommandHandler( domain = TestDomain.class )
public class TestChangeNameCommandHandler extends AbstractEntityCommandHandler<TestChangeNameCommandHandler.TestChangeNameCommand, TestEntity> {

    @XKasperCommand
    public static final class TestChangeNameCommand extends AbstractCreateCommand {

        private final String name;

        public TestChangeNameCommand(final KasperID id, final String name) {
            super(id);
            this.name = checkNotNull(name);
        }

        public String getName() {
            return this.name;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult handle(final KasperCommandMessage<TestChangeNameCommand> message) throws Exception {
        final TestEntity entity = this.getRepository().load(message.getCommand().getIdToUse());
        entity.changeName(message.getCommand().getName());
        return CommandResult.ok();
    }

}
