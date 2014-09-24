// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.user;


import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.user.CreateUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserCreatedEvent;
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
public class CreateUserCommandHandlerUTest {

    private KasperPlatformFixture fixture;

    private UserRepository repository;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        repository = new UserRepository(authorizationStorage);

        CreateUserCommandHandler commandHandler = new CreateUserCommandHandler();
        commandHandler.setRepository(repository);

        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(commandHandler)
                        .with(repository)
                        .build()
        );

    }

    @Test
    public void createUserWithGoodName_shouldBeOk() {
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        when(authorizationStorage.createUser(any(User.class))).thenReturn(true);
        fixture.given()
                .when(new CreateUserCommand(user.getEntityId(), user.getFirstName(), user.getLastName()), context)
                .expectReturnOK()
                .expectExactSequenceOfEvents(new AuthorizationUserCreatedEvent(user.getEntityId()));
    }
}

