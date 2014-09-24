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
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetGroupsHavingPermissionQuery;
import com.viadeo.kasper.security.authz.queries.results.GroupResult;
import com.viadeo.kasper.security.authz.queries.results.GroupsResult;
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
public class GetGroupsHavingPermissionQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetGroupsHavingPermissionQueryHandler queryHandler = new GetGroupsHavingPermissionQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getGroupsHavingPassedPermissionWithGoodId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), Arrays.asList(permission), new ArrayList<User>());
        List<Group> groups = Arrays.asList(group);
        when(authorizationStorage.getGroupsHavingPermission(permission.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingPermissionQuery(permission.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups))));
    }

    @Test
    public void getGroupsHavingPassedPermissionWithGoodIdButEmptyResults_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        List<Group> groups = new ArrayList<>();
        when(authorizationStorage.getGroupsHavingPermission(permission.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingPermissionQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups))));
    }

    @Test
    public void getGroupsHavingPassedPermissionWithWrongId_shouldReturnResponseOk() {
        WildcardPermission permission = WildcardPermission.build(new DefaultKasperId(), "permission");
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), Arrays.asList(permission), new ArrayList<User>());
        List<Group> groups = Arrays.asList(group);
        when(authorizationStorage.getGroupsHavingPermission(permission.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingPermissionQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(new ArrayList<Group>()))));
    }

}
