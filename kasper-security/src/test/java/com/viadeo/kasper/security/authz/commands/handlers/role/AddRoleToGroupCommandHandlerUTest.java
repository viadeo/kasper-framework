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
import com.viadeo.kasper.security.authz.commands.role.AddRoleToGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Role;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupRoleAssociationId;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleAddedToGroupEvent;
import com.viadeo.kasper.security.authz.repositories.GroupRepository;
import com.viadeo.kasper.security.authz.repositories.RoleAddedToGroupRepository;
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
public class AddRoleToGroupCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private RoleAddedToGroupRepository repository;
    private RoleRepository roleRepository;
    private GroupRepository groupRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new RoleAddedToGroupRepository(authorizationStorage);
        roleRepository = new RoleRepository(authorizationStorage);
        groupRepository = new GroupRepository(authorizationStorage);

        AddRoleToGroupCommandHandler commandHandler = new AddRoleToGroupCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, roleRepository, groupRepository)
                        .build()
        );

    }

    @Test
    public void addRoleToGroup_shouldBeOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        when(authorizationStorage.getRole(role.getIdentifier())).thenReturn(Optional.of(role));
        when(authorizationStorage.getGroup(group.getIdentifier())).thenReturn(Optional.of(group));
        when(authorizationStorage.addRoleToGroup(any(Group_has_Role.class))).thenReturn(true);
        fixture.given()
                .when(new AddRoleToGroupCommand(group.getEntityId(), role.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationRoleAddedToGroupEvent(
                                new GroupRoleAssociationId(
                                        group.getEntityId(),
                                        role.getEntityId()
                                ),
                                group,
                                role
                        )
                );
    }
}
