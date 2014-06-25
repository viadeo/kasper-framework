package com.viadeo.kasper.security.authz.runtime;

import com.viadeo.kasper.security.authz.commands.handlers.group.*;
import com.viadeo.kasper.security.authz.commands.handlers.permission.*;
import com.viadeo.kasper.security.authz.commands.handlers.role.*;
import com.viadeo.kasper.security.authz.commands.handlers.user.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationCommandConfiguration {

    @Bean
    public CreateGroupCommandHandler createGroupCommandHandler() {
        return new CreateGroupCommandHandler();
    }

    @Bean
    public DeleteGroupCommandHandler deleteGroupCommandHandler() {
        return new DeleteGroupCommandHandler();
    }

    @Bean
    public AddPermissionToGroupCommandHandler addPermissionToGroupCommandHandler() {
        return new AddPermissionToGroupCommandHandler();
    }

    @Bean
    public AddPermissionToRoleCommandHandler addPermissionToRoleCommandHandler() {
        return new AddPermissionToRoleCommandHandler();
    }

    @Bean
    public AddPermissionToUserCommandHandler addPermissionToUserCommandHandler() {
        return new AddPermissionToUserCommandHandler();
    }

    @Bean
    public CreatePermissionCommandHandler createPermissionCommandHandler() {
        return new CreatePermissionCommandHandler();
    }

    @Bean
    public DeletePermissionCommandHandler deletePermissionCommandHandler() {
        return new DeletePermissionCommandHandler();
    }

    @Bean
    public RemovePermissionFromGroupCommandHandler removePermissionFromGroupCommandHandler() {
        return new RemovePermissionFromGroupCommandHandler();
    }

    @Bean
    public RemovePermissionFromRoleCommandHandler removePermissionFromRoleCommandHandler() {
        return new RemovePermissionFromRoleCommandHandler();
    }

    @Bean
    public RemovePermissionFromUserCommandHandler removePermissionFromUserCommandHandler() {
        return new RemovePermissionFromUserCommandHandler();
    }

    @Bean
    public AddRoleToGroupCommandHandler addRoleToGroupCommandHandler() {
        return new AddRoleToGroupCommandHandler();
    }

    @Bean
    public AddRoleToUserCommandHandler addRoleToUserCommandHandler() {
        return new AddRoleToUserCommandHandler();
    }

    @Bean
    public CreateRoleCommandHandler createRoleCommandHandler() {
        return new CreateRoleCommandHandler();
    }

    @Bean
    public DeleteRoleCommandHandler deleteRoleCommandHandler() {
        return new DeleteRoleCommandHandler();
    }

    @Bean
    public RemoveRoleFromGroupCommandHandler removeRoleFromGroupCommandHandler() {
        return new RemoveRoleFromGroupCommandHandler();
    }

    @Bean
    public RemoveRoleFromUserCommandHandler removeRoleFromUserCommandHandler() {
        return new RemoveRoleFromUserCommandHandler();
    }

    @Bean
    public AddUserToGroupCommandHandler addUserToGroupCommandHandler() {
        return new AddUserToGroupCommandHandler();
    }

    @Bean
    public CreateUserCommandHandler createUserCommandHandler() {
        return new CreateUserCommandHandler();
    }

    @Bean
    public DeleteUserCommandHandler deleteUserCommandHandler() {
        return new DeleteUserCommandHandler();
    }

    @Bean
    public RemoveUserFromGroupCommandHandler removeUserFromGroupCommandHandler() {
        return new RemoveUserFromGroupCommandHandler();
    }

}
