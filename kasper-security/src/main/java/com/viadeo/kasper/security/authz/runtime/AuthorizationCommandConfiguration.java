// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.runtime;

import com.viadeo.kasper.security.authz.commands.handlers.group.CreateGroupCommandHandler;
import com.viadeo.kasper.security.authz.commands.handlers.group.DeleteGroupCommandHandler;
import com.viadeo.kasper.security.authz.commands.handlers.permission.*;
import com.viadeo.kasper.security.authz.commands.handlers.role.*;
import com.viadeo.kasper.security.authz.commands.handlers.user.AddUserToGroupCommandHandler;
import com.viadeo.kasper.security.authz.commands.handlers.user.CreateUserCommandHandler;
import com.viadeo.kasper.security.authz.commands.handlers.user.DeleteUserCommandHandler;
import com.viadeo.kasper.security.authz.commands.handlers.user.RemoveUserFromGroupCommandHandler;

public class AuthorizationCommandConfiguration {


    public CreateGroupCommandHandler createGroupCommandHandler() {
        return new CreateGroupCommandHandler();
    }

    public DeleteGroupCommandHandler deleteGroupCommandHandler() {
        return new DeleteGroupCommandHandler();
    }

    public AddPermissionToGroupCommandHandler addPermissionToGroupCommandHandler() {
        return new AddPermissionToGroupCommandHandler();
    }

    public AddPermissionToRoleCommandHandler addPermissionToRoleCommandHandler() {
        return new AddPermissionToRoleCommandHandler();
    }

    public AddPermissionToUserCommandHandler addPermissionToUserCommandHandler() {
        return new AddPermissionToUserCommandHandler();
    }

    public CreatePermissionCommandHandler createPermissionCommandHandler() {
        return new CreatePermissionCommandHandler();
    }

    public DeletePermissionCommandHandler deletePermissionCommandHandler() {
        return new DeletePermissionCommandHandler();
    }

    public RemovePermissionFromGroupCommandHandler removePermissionFromGroupCommandHandler() {
        return new RemovePermissionFromGroupCommandHandler();
    }

    public RemovePermissionFromRoleCommandHandler removePermissionFromRoleCommandHandler() {
        return new RemovePermissionFromRoleCommandHandler();
    }

    public RemovePermissionFromUserCommandHandler removePermissionFromUserCommandHandler() {
        return new RemovePermissionFromUserCommandHandler();
    }

    public AddRoleToGroupCommandHandler addRoleToGroupCommandHandler() {
        return new AddRoleToGroupCommandHandler();
    }

    public AddRoleToUserCommandHandler addRoleToUserCommandHandler() {
        return new AddRoleToUserCommandHandler();
    }

    public CreateRoleCommandHandler createRoleCommandHandler() {
        return new CreateRoleCommandHandler();
    }

    public DeleteRoleCommandHandler deleteRoleCommandHandler() {
        return new DeleteRoleCommandHandler();
    }

    public RemoveRoleFromGroupCommandHandler removeRoleFromGroupCommandHandler() {
        return new RemoveRoleFromGroupCommandHandler();
    }

    public RemoveRoleFromUserCommandHandler removeRoleFromUserCommandHandler() {
        return new RemoveRoleFromUserCommandHandler();
    }

    public AddUserToGroupCommandHandler addUserToGroupCommandHandler() {
        return new AddUserToGroupCommandHandler();
    }

    public CreateUserCommandHandler createUserCommandHandler() {
        return new CreateUserCommandHandler();
    }

    public DeleteUserCommandHandler deleteUserCommandHandler() {
        return new DeleteUserCommandHandler();
    }

    public RemoveUserFromGroupCommandHandler removeUserFromGroupCommandHandler() {
        return new RemoveUserFromGroupCommandHandler();
    }

}
