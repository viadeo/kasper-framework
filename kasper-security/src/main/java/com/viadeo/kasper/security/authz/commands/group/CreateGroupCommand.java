// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.group;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CreateCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@XKasperCommand(description = "Create a Group for security authorization")
public class CreateGroupCommand extends CreateCommand {

    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    private List<User> users;

    @NotNull
    private List<Role> roles;

    @NotNull
    private List<WildcardPermission> permissions;

    public CreateGroupCommand(final KasperID kasperID,
                              final String name,
                              final List<Role> roles,
                              final List<WildcardPermission> permissions,
                              final List<User> users) {
        super(kasperID);
        this.name = name;
        this.roles = roles;
        this.permissions = permissions;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<WildcardPermission> getPermissions() {
        return permissions;
    }
}
