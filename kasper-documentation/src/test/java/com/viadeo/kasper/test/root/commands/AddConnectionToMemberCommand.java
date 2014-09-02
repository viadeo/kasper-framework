package com.viadeo.kasper.test.root.commands;

import com.viadeo.kasper.annotation.XKasperAlias;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.security.annotation.XKasperPublic;

import javax.validation.constraints.NotNull;

@XKasperCommand
@XKasperAlias(values = {"AddConnection"})
@XKasperPublic
public class AddConnectionToMemberCommand implements Command {
	private static final long serialVersionUID = -5348191495602297087L;

    @NotNull
    public Integer id;

    @NotNull
    public String type;
}
