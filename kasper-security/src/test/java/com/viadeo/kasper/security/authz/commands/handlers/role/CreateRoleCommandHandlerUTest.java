// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.role;


import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.role.CreateRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleCreatedEvent;
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
public class CreateRoleCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private RoleRepository repository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new RoleRepository(authorizationStorage);

        CreateRoleCommandHandler commandHandler = new CreateRoleCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository)
                        .build()
        );

    }

    @Test
    public void createRoleWithGoodName_shouldBeOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        when(authorizationStorage.createRole(any(Role.class))).thenReturn(true);
        fixture.given()
                .when(new CreateRoleCommand(role.getEntityId(), role.getName()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(new AuthorizationRoleCreatedEvent(role.getEntityId()));
    }

}

