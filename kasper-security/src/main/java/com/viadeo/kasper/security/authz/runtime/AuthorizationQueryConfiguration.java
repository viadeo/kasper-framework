// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.runtime;

import com.viadeo.kasper.security.authz.queries.handlers.*;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationQueryConfiguration {

    @Bean
    public GetGroupQueryHandler getGroupQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupQueryHandler(authorizationStorage);
    }

    @Bean
    public GetGroupsHavingPermissionQueryHandler getGroupsHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsHavingPermissionQueryHandler(authorizationStorage);
    }

    @Bean
    public GetGroupsHavingRoleQueryHandler getGroupsHavingRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsHavingRoleQueryHandler(authorizationStorage);
    }

    @Bean
    public GetGroupsHavingUserQueryHandler getGroupsHavingUserQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsHavingUserQueryHandler(authorizationStorage);
    }

    @Bean
    public GetGroupsQueryHandler getGroupsQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsQueryHandler(authorizationStorage);
    }

    @Bean
    public GetRoleQueryHandler getRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetRoleQueryHandler(authorizationStorage);
    }

    @Bean
    public GetRolesHavingPermissionQueryHandler getRolesHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetRolesHavingPermissionQueryHandler(authorizationStorage);
    }

    @Bean
    public GetRolesQueryHandler getRolesQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetRolesQueryHandler(authorizationStorage);
    }

    @Bean
    public GetUserQueryHandler getUserQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUserQueryHandler(authorizationStorage);
    }

    @Bean
    public GetUsersHavingPermissionQueryHandler getUsersHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUsersHavingPermissionQueryHandler(authorizationStorage);
    }

    @Bean
    public GetUsersHavingRoleQueryHandler getUsersHavingRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUsersHavingRoleQueryHandler(authorizationStorage);
    }

    @Bean
    public GetUsersQueryHandler getUsersQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUsersQueryHandler(authorizationStorage);
    }

}
