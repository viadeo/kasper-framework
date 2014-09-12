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
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetRolesHavingPermissionQuery;
import com.viadeo.kasper.security.authz.queries.results.RoleResult;
import com.viadeo.kasper.security.authz.queries.results.RolesResult;
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
public class GetRolesHavingPermissionQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetRolesHavingPermissionQueryHandler queryHandler = new GetRolesHavingPermissionQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getRolesHavingPassedPermissionWithGoodId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        Role role = Role.build(new DefaultKasperId(), "role", Arrays.asList(permission));
        List<Role> roles = Arrays.asList(role);
        when(authorizationStorage.getRolesHavingPermission(permission.getIdentifier())).thenReturn(roles);
        fixture.given()
                .when(new GetRolesHavingPermissionQuery(permission.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new RolesResult(RoleResult.getRoleResults(roles))));
    }

    @Test
    public void getRolesHavingPassedPermissionWithGoodIdButEmptyResults_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        List<Role> roles = new ArrayList<>();
        when(authorizationStorage.getRolesHavingPermission(permission.getIdentifier())).thenReturn(roles);
        fixture.given()
                .when(new GetRolesHavingPermissionQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new RolesResult(RoleResult.getRoleResults(roles))));
    }

    @Test
    public void getRolesHavingPassedPermissionWithWrongId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        Role role = Role.build(new DefaultKasperId(), "role", Arrays.asList(permission));
        List<Role> roles = Arrays.asList(role);
        when(authorizationStorage.getRolesHavingPermission(permission.getIdentifier())).thenReturn(roles);
        fixture.given()
                .when(new GetRolesHavingPermissionQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new RolesResult(RoleResult.getRoleResults(new ArrayList<Role>()))));
    }

}
