// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.runtime;

import com.viadeo.kasper.security.authz.repositories.*;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationRepositoryConfiguration {

    @Bean
    public GroupRepository groupRepository(AuthorizationStorage authorizationStorage) {
        return new GroupRepository(authorizationStorage);
    }

    @Bean
    public PermissionAddedToGroupRepository permissionAddedToGroupRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionAddedToGroupRepository(authorizationStorage);
    }

    @Bean
    public PermissionAddedToRoleRepository permissionAddedToRoleRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionAddedToRoleRepository(authorizationStorage);
    }

    @Bean
    public PermissionAddedToUserRepository permissionAddedToUserRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionAddedToUserRepository(authorizationStorage);
    }

    @Bean
    public PermissionRepository permissionRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionRepository(authorizationStorage);
    }

    @Bean
    public RoleAddedToGroupRepository roleAddedToGroupRepository(AuthorizationStorage authorizationStorage) {
        return new RoleAddedToGroupRepository(authorizationStorage);
    }

    @Bean
    public RoleAddedToUserRepository roleAddedToUserRepository(AuthorizationStorage authorizationStorage) {
        return new RoleAddedToUserRepository(authorizationStorage);
    }

    @Bean
    public RoleRepository roleRepository(AuthorizationStorage authorizationStorage) {
        return new RoleRepository(authorizationStorage);
    }

    @Bean
    public UserAddedToGroupRepository userAddedToGroupRepository(AuthorizationStorage authorizationStorage) {
        return new UserAddedToGroupRepository(authorizationStorage);
    }

    @Bean
    public UserRepository userRepository(AuthorizationStorage authorizationStorage) {
        return new UserRepository(authorizationStorage);
    }

}
