package com.viadeo.kasper.test.root.handlers;

import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.commands.AddConnectionToMemberCommand;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.unitofwork.UnitOfWork;

@XKasperCommandHandler(domain=Facebook.class)
public class AddConnectionToMemberHandler implements ICommandHandler<AddConnectionToMemberCommand> {

	@Override
	public Object handle(CommandMessage<AddConnectionToMemberCommand> arg0,
			UnitOfWork arg1) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	}
