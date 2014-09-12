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
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetPermissionQuery;
import com.viadeo.kasper.security.authz.queries.results.PermissionResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetPermissionQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetPermissionQueryHandler queryHandler = new GetPermissionQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getPermissionWithGoodId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        when(authorizationStorage.getPermission(permission.getIdentifier())).thenReturn(Optional.of(permission));
        fixture.given()
                .when(new GetPermissionQuery(permission.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(PermissionResult.getPermissionResult(permission)));
    }

    @Test
    public void getPermissionWithGoodId_shouldReturnResponseError() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        when(authorizationStorage.getPermission(permission.getIdentifier())).thenReturn(Optional.<WildcardPermission>absent());
        fixture.given()
                .when(new GetPermissionQuery(permission.getEntityId()), context)
                .expectReturnResponse(QueryResponse.error(CoreReasonCode.NOT_FOUND));
    }
}
