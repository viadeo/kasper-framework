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
import com.viadeo.kasper.security.authz.commands.permission.RemovePermissionFromUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Permission;
import com.viadeo.kasper.security.authz.entities.relations.ids.UserPermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionRemovedFromUserEvent;
import com.viadeo.kasper.security.authz.repositories.PermissionAddedToUserRepository;
import com.viadeo.kasper.security.authz.repositories.PermissionRepository;
import com.viadeo.kasper.security.authz.repositories.UserRepository;
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
public class RemovePermissionFromUserCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private PermissionAddedToUserRepository repository;
    private PermissionRepository permissionRepository;
    private UserRepository userRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new PermissionAddedToUserRepository(authorizationStorage);
        permissionRepository = new PermissionRepository(authorizationStorage);
        userRepository = new UserRepository(authorizationStorage);

        RemovePermissionFromUserCommandHandler commandHandler = new RemovePermissionFromUserCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, permissionRepository, userRepository)
                        .build()
        );

    }

    @Test
    public void removePermissionFromUser_shouldBeOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        User_has_Permission user_has_permission = User_has_Permission.build(user, permission);
        when(authorizationStorage.getPermission(permission.getIdentifier())).thenReturn(Optional.of(permission));
        when(authorizationStorage.getUser(user.getIdentifier())).thenReturn(Optional.of(user));
        when(authorizationStorage.getUserHasPermission(user.getEntityId(), permission.getEntityId())).thenReturn(user_has_permission);
        when(authorizationStorage.removePermissionFromUser(any(User_has_Permission.class))).thenReturn(true);
        fixture.given()
                .when(new RemovePermissionFromUserCommand(user.getEntityId(), permission.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationPermissionRemovedFromUserEvent(
                                new UserPermissionAssociationId(
                                        user.getEntityId(),
                                        permission.getEntityId()
                                )
                        )
                );
    }

}
