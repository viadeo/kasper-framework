// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.user;

import com.google.common.base.Optional;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.user.RemoveUserFromGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_User;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupUserAssociationId;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserRemovedFromGroupEvent;
import com.viadeo.kasper.security.authz.repositories.GroupRepository;
import com.viadeo.kasper.security.authz.repositories.UserAddedToGroupRepository;
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
public class RemoveUserFromGroupCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private UserAddedToGroupRepository repository;
    private UserRepository userRepository;
    private GroupRepository groupRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new UserAddedToGroupRepository(authorizationStorage);
        userRepository = new UserRepository(authorizationStorage);
        groupRepository = new GroupRepository(authorizationStorage);

        RemoveUserFromGroupCommandHandler commandHandler = new RemoveUserFromGroupCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, userRepository, groupRepository)
                        .build()
        );

    }

    @Test
    public void removeUserFromGroup_shouldBeOk() {
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        Group_has_User group_has_user = Group_has_User.build(group, user);
        when(authorizationStorage.getUser(user.getIdentifier())).thenReturn(Optional.of(user));
        when(authorizationStorage.getGroup(group.getIdentifier())).thenReturn(Optional.of(group));
        when(authorizationStorage.getGroupHasUser(group.getEntityId(), user.getEntityId())).thenReturn(group_has_user);
        when(authorizationStorage.removeUserFromGroup(any(Group_has_User.class))).thenReturn(true);
        fixture.given()
                .when(new RemoveUserFromGroupCommand(group.getEntityId(), user.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationUserRemovedFromGroupEvent(
                                new GroupUserAssociationId(
                                        group.getEntityId(),
                                        user.getEntityId()
                                )
                        )
                );
    }

}
