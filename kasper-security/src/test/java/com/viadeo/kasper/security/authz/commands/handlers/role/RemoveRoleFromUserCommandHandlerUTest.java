// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.role;

import com.google.common.base.Optional;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.role.RemoveRoleFromUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Role;
import com.viadeo.kasper.security.authz.entities.relations.ids.UserRoleAssociationId;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleRemovedFromUserEvent;
import com.viadeo.kasper.security.authz.repositories.RoleAddedToUserRepository;
import com.viadeo.kasper.security.authz.repositories.RoleRepository;
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
public class RemoveRoleFromUserCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private RoleAddedToUserRepository repository;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new RoleAddedToUserRepository(authorizationStorage);
        roleRepository = new RoleRepository(authorizationStorage);
        userRepository = new UserRepository(authorizationStorage);

        RemoveRoleFromUserCommandHandler commandHandler = new RemoveRoleFromUserCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, roleRepository, userRepository)
                        .build()
        );

    }

    @Test
    public void removeRoleFromUser_shouldBeOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        User_has_Role user_has_role = User_has_Role.build(user, role);
        when(authorizationStorage.getRole(role.getIdentifier())).thenReturn(Optional.of(role));
        when(authorizationStorage.getUser(user.getIdentifier())).thenReturn(Optional.of(user));
        when(authorizationStorage.getUserHasRole(user.getEntityId(), role.getEntityId())).thenReturn(user_has_role);
        when(authorizationStorage.removeRoleFromUser(any(User_has_Role.class))).thenReturn(true);
        fixture.given()
                .when(new RemoveRoleFromUserCommand(user.getEntityId(), role.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationRoleRemovedFromUserEvent(
                                new UserRoleAssociationId(
                                        user.getEntityId(),
                                        role.getEntityId()
                                )
                        )
                );
    }

}
