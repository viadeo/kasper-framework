// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.runtime;

import com.viadeo.kasper.security.authz.repositories.*;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

public class AuthorizationRepositoryConfiguration {

    public GroupRepository groupRepository(AuthorizationStorage authorizationStorage) {
        return new GroupRepository(authorizationStorage);
    }

    public PermissionAddedToGroupRepository permissionAddedToGroupRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionAddedToGroupRepository(authorizationStorage);
    }

    public PermissionAddedToRoleRepository permissionAddedToRoleRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionAddedToRoleRepository(authorizationStorage);
    }

    public PermissionAddedToUserRepository permissionAddedToUserRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionAddedToUserRepository(authorizationStorage);
    }

    public PermissionRepository permissionRepository(AuthorizationStorage authorizationStorage) {
        return new PermissionRepository(authorizationStorage);
    }

    public RoleAddedToGroupRepository roleAddedToGroupRepository(AuthorizationStorage authorizationStorage) {
        return new RoleAddedToGroupRepository(authorizationStorage);
    }

    public RoleAddedToUserRepository roleAddedToUserRepository(AuthorizationStorage authorizationStorage) {
        return new RoleAddedToUserRepository(authorizationStorage);
    }

    public RoleRepository roleRepository(AuthorizationStorage authorizationStorage) {
        return new RoleRepository(authorizationStorage);
    }

    public UserAddedToGroupRepository userAddedToGroupRepository(AuthorizationStorage authorizationStorage) {
        return new UserAddedToGroupRepository(authorizationStorage);
    }

    public UserRepository userRepository(AuthorizationStorage authorizationStorage) {
        return new UserRepository(authorizationStorage);
    }

}
