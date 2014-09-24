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
import com.viadeo.kasper.security.authz.commands.role.DeleteRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleDeletedEvent;
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
public class DeleteRoleCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private RoleRepository repository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new RoleRepository(authorizationStorage);

        DeleteRoleCommandHandler commandHandler = new DeleteRoleCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository)
                        .build()
        );

    }

    @Test
    public void deleteRoleWithGoodName_shouldBeOk() throws Exception {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        when(authorizationStorage.getRole(role.getIdentifier())).thenReturn(Optional.of(role));
        when(authorizationStorage.deleteRole(any(Role.class))).thenReturn(true);

        fixture.given()
                .when(new DeleteRoleCommand(role.getEntityId()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(new AuthorizationRoleDeletedEvent(role.getEntityId()));
    }

}

