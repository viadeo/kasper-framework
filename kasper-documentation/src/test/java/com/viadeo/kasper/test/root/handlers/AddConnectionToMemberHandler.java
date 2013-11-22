package com.viadeo.kasper.test.root.handlers;

import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.commands.AddConnectionToMemberCommand;

@XKasperCommandHandler(domain=Facebook.class)
public class AddConnectionToMemberHandler extends CommandHandler<AddConnectionToMemberCommand> {

}
