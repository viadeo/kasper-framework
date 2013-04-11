package com.viadeo.kasper.test.root.commands;

import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.test.root.Facebook;

@XKasperCommand(domain = Facebook.class)
public class AddConnectionToMemberCommand implements ICommand {
	private static final long serialVersionUID = -5348191495602297087L;

}
