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
import com.viadeo.kasper.security.authz.queries.GetUsersHavingPermissionQuery;
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
public class GetUsersHavingPermissionQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetUsersHavingPermissionQueryHandler queryHandler = new GetUsersHavingPermissionQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getUsersHavingPassedPermissionWithGoodId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), Arrays.asList(permission));
        List<User> users = Arrays.asList(user);
        when(authorizationStorage.getUsersHavingPermission(permission.getIdentifier())).thenReturn(users);
        fixture.given()
                .when(new GetUsersHavingPermissionQuery(permission.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new UsersResult(UserResult.getUserResults(users))));
    }

    @Test
    public void getUsersHavingPassedPermissionWithGoodIdButEmptyResults_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        List<User> users = new ArrayList<>();
        when(authorizationStorage.getUsersHavingPermission(permission.getIdentifier())).thenReturn(users);
        fixture.given()
                .when(new GetUsersHavingPermissionQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new UsersResult(UserResult.getUserResults(users))));
    }

    @Test
    public void getUsersHavingPassedPermissionWithWrongId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), Arrays.asList(permission));
        List<User> users = Arrays.asList(user);
        when(authorizationStorage.getUsersHavingPermission(permission.getIdentifier())).thenReturn(users);
        fixture.given()
                .when(new GetUsersHavingPermissionQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new UsersResult(UserResult.getUserResults(new ArrayList<User>()))));
    }


}
