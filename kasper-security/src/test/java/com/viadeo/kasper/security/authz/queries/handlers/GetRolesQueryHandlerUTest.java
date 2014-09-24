// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.handlers;

import com.google.common.base.Optional;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetRoleQuery;
import com.viadeo.kasper.security.authz.queries.GetRolesQuery;
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
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetRolesQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetRolesQueryHandler queryHandler = new GetRolesQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getRolesWithGoodIds_shouldReturnResponseOk() {
        Role role1 = Role.build(new DefaultKasperId(), "role2", new ArrayList<WildcardPermission>());
        Role role2 = Role.build(new DefaultKasperId(), "role2", new ArrayList<WildcardPermission>());
        List<Role> roles = new ArrayList<Role>();
        roles.add(role1);
        roles.add(role2);
        when(authorizationStorage.getAllRoles()).thenReturn(roles);
        fixture.given()
                .when(new GetRolesQuery(), context)
                .expectReturnResponse(QueryResponse.of(new RolesResult(RoleResult.getRoleResults(roles))));
    }

}
