// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.commands;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

@XKasperCommand
@Deprecated
public class AddConnectionToGroupCommand implements Command {
	private static final long serialVersionUID = -5348191495602297087L;

    @NotNull
    public Integer id;

    @NotNull
    public String type;
}
