// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.api.command;

import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.annotation.XKasperPublic;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@XKasperCommand
@XKasperAlias(values = {"AddConnection"})
@XKasperPublic
public class AddConnectionToMemberCommand implements Command {
	private static final long serialVersionUID = -5348191495602297087L;

    @NotNull
    public Integer id;

    @NotNull
    public User user;

    public List<User> users;

    public Map<String, User> usersByName;

    @NotNull
    public String type;


    public static class User implements Serializable {
        public String firstName;
        public String lastName;
    }
}
