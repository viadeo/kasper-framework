package com.viadeo.kasper.test.root.handlers;

import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.commands.AddConnectionToGroupCommand;


@XKasperCommandHandler(domain=Facebook.class)
@Deprecated
public class AddConnectionToGroupHandler extends CommandHandler<AddConnectionToGroupCommand> {
}
