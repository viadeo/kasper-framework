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
import com.viadeo.kasper.security.authz.commands.user.AddUserToGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_User;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupUserAssociationId;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserAddedToGroupEvent;
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
public class AddUserToGroupCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private UserAddedToGroupRepository repository;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new UserAddedToGroupRepository(authorizationStorage);
        groupRepository = new GroupRepository(authorizationStorage);
        userRepository = new UserRepository(authorizationStorage);

        AddUserToGroupCommandHandler commandHandler = new AddUserToGroupCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, groupRepository, userRepository)
                        .build()
        );

    }

    @Test
    public void addUserToGroup_shouldBeOk() {
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        when(authorizationStorage.getGroup(group.getIdentifier())).thenReturn(Optional.of(group));
        when(authorizationStorage.getUser(user.getIdentifier())).thenReturn(Optional.of(user));
        when(authorizationStorage.addUserToGroup(any(Group_has_User.class))).thenReturn(true);
        fixture.given()
                .when(new AddUserToGroupCommand(group.getEntityId(), user.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationUserAddedToGroupEvent(
                                new GroupUserAssociationId(
                                        group.getEntityId(),
                                        user.getEntityId()
                                ),
                                group,
                                user
                        )
                );
    }

}
