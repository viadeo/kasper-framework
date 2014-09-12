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
import com.viadeo.kasper.security.authz.commands.permission.AddPermissionToGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Permission;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupPermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionAddedToGroupEvent;
import com.viadeo.kasper.security.authz.repositories.GroupRepository;
import com.viadeo.kasper.security.authz.repositories.PermissionAddedToGroupRepository;
import com.viadeo.kasper.security.authz.repositories.PermissionRepository;
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
public class AddPermissionToGroupCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private PermissionAddedToGroupRepository repository;
    private PermissionRepository permissionRepository;
    private GroupRepository groupRepository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new PermissionAddedToGroupRepository(authorizationStorage);
        permissionRepository = new PermissionRepository(authorizationStorage);
        groupRepository = new GroupRepository(authorizationStorage);

        AddPermissionToGroupCommandHandler commandHandler = new AddPermissionToGroupCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository, permissionRepository, groupRepository)
                        .build()
        );

    }

    @Test
    public void addPermissionToGroup_shouldBeOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        when(authorizationStorage.getPermission(permission.getIdentifier())).thenReturn(Optional.of(permission));
        when(authorizationStorage.getGroup(group.getIdentifier())).thenReturn(Optional.of(group));
        when(authorizationStorage.addPermissionToGroup(any(Group_has_Permission.class))).thenReturn(true);
        fixture.given()
                .when(new AddPermissionToGroupCommand(group.getEntityId(), permission.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(
                        new AuthorizationPermissionAddedToGroupEvent(
                                new GroupPermissionAssociationId(
                                        group.getEntityId(),
                                        permission.getEntityId()
                                ),
                                group,
                                permission
                        )
                );
    }

}
