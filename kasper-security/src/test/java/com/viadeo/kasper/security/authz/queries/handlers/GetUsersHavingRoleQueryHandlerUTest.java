// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.handlers;

import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetUsersHavingRoleQuery;
import com.viadeo.kasper.security.authz.queries.results.UserResult;
import com.viadeo.kasper.security.authz.queries.results.UsersResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetUsersHavingRoleQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetUsersHavingRoleQueryHandler queryHandler = new GetUsersHavingRoleQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getUsersHavingPassedRoleWithGoodId_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", Arrays.asList(role), new ArrayList<WildcardPermission>());
        List<User> users = Arrays.asList(user);
        when(authorizationStorage.getUsersHavingRole(role.getIdentifier())).thenReturn(users);
        fixture.given()
                .when(new GetUsersHavingRoleQuery(role.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new UsersResult(UserResult.getUserResults(users))));
    }

    @Test
    public void getUsersHavingPassedRoleWithGoodIdButEmptyResults_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        List<User> users = new ArrayList<>();
        when(authorizationStorage.getUsersHavingRole(role.getIdentifier())).thenReturn(users);
        fixture.given()
                .when(new GetUsersHavingRoleQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new UsersResult(UserResult.getUserResults(users))));
    }

    @Test
    public void getUsersHavingPassedRoleWithWrongId_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", Arrays.asList(role), new ArrayList<WildcardPermission>());
        List<User> users = Arrays.asList(user);
        when(authorizationStorage.getUsersHavingRole(role.getIdentifier())).thenReturn(users);
        fixture.given()
                .when(new GetUsersHavingRoleQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new UsersResult(UserResult.getUserResults(new ArrayList<User>()))));
    }


}
