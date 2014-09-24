// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.handlers;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetRoleQuery;
import com.viadeo.kasper.security.authz.queries.results.RoleResult;
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
public class GetRoleQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetRoleQueryHandler queryHandler = new GetRoleQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getRoleWithGoodId_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        when(authorizationStorage.getRole(role.getIdentifier())).thenReturn(Optional.of(role));
        fixture.given()
                .when(new GetRoleQuery(role.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new RoleResult(role)));
    }

    @Test
    public void getRoleWithGoodId_shouldReturnResponseError() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        when(authorizationStorage.getRole(role.getIdentifier())).thenReturn(Optional.<Role>absent());
        fixture.given()
                .when(new GetRoleQuery(role.getEntityId()), context)
                .expectReturnResponse(QueryResponse.error(CoreReasonCode.NOT_FOUND));
    }
}
