// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.RemovePermissionFromRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Role_has_Permission;
import com.viadeo.kasper.security.authz.entities.relations.ids.RolePermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionRemovedFromRoleEvent;
import com.viadeo.kasper.security.authz.repositories.PermissionAddedToRoleRepository;
import com.viadeo.kasper.security.authz.repositories.PermissionRepository;
import com.viadeo.kasper.security.authz.repositories.RoleRepository;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemovePermissionFromRoleCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private PermissionAddedToRoleRepository repository;
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new PermissionAddedToRoleRepository(authorizationStorage);
        permissionRepository = new PermissionRepository(authorizationStorage);
        roleRepository = new RoleRepository(authorizationStorage);

        RemovePermissionFromRoleCommandHandler commandHandler = new RemovePermissionFromRoleCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, permissionRepository, roleRepository)
                        .build()
        );

    }

    @Test
    public void removePermissionFromRole_shouldBeOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        Role_has_Permission role_has_permission = Role_has_Permission.build(role, permission);
        when(authorizationStorage.getPermission(permission.getIdentifier())).thenReturn(Optional.of(permission));
        when(authorizationStorage.getRole(role.getIdentifier())).thenReturn(Optional.of(role));
        when(authorizationStorage.getRoleHasPermission(role.getEntityId(), permission.getEntityId())).thenReturn(role_has_permission);
        when(authorizationStorage.removePermissionFromRole(any(Role_has_Permission.class))).thenReturn(true);
        fixture.given()
                .when(new RemovePermissionFromRoleCommand(role.getEntityId(), permission.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationPermissionRemovedFromRoleEvent(
                                new RolePermissionAssociationId(
                                        role.getEntityId(),
                                        permission.getEntityId()
                                )
                        )
                );
    }

}
