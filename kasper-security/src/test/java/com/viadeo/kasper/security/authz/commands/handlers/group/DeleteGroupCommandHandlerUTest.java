// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.group;


import com.google.common.base.Optional;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.group.DeleteGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.group.AuthorizationGroupDeletedEvent;
import com.viadeo.kasper.security.authz.repositories.GroupRepository;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteGroupCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private GroupRepository repository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new GroupRepository(authorizationStorage);

        DeleteGroupCommandHandler commandHandler = new DeleteGroupCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository)
                        .build()
        );
    }

    @Test
    public void deleteGroupWithGoodName_shouldBeOk() throws Exception{
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        when(authorizationStorage.getGroup(group.getIdentifier())).thenReturn(Optional.of(group));
        when(authorizationStorage.deleteGroup(group)).thenReturn(true);

        fixture.given()
                .when(new DeleteGroupCommand(group.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(new AuthorizationGroupDeletedEvent(group.getEntityId()));
    }

}

