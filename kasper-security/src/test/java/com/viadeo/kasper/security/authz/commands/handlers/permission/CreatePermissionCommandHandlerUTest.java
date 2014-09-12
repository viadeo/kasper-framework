// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.permission;


import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.CreatePermissionCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionCreatedEvent;
import com.viadeo.kasper.security.authz.repositories.PermissionRepository;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreatePermissionCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private PermissionRepository repository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new PermissionRepository(authorizationStorage);

        CreatePermissionCommandHandler commandHandler = new CreatePermissionCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository)
                        .build()
        );

    }

    @Test
    public void createPermissionWithGoodName_shouldBeOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        when(authorizationStorage.createPermission(any(WildcardPermission.class))).thenReturn(true);
        fixture.given()
                .when(new CreatePermissionCommand(permission.getEntityId(), permission.toString()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(new AuthorizationPermissionCreatedEvent(permission.getEntityId()));
    }

}

