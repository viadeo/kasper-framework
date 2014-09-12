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
import com.viadeo.kasper.security.authz.queries.GetGroupsHavingRoleQuery;
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
public class GetGroupsHavingRoleQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetGroupsHavingRoleQueryHandler queryHandler = new GetGroupsHavingRoleQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getGroupsHavingPassedRoleWithGoodId_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        Group group = Group.build(new DefaultKasperId(), "group", Arrays.asList(role), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        List<Group> groups = Arrays.asList(group);
        when(authorizationStorage.getGroupsHavingRole(role.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingRoleQuery(role.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups))));
    }

    @Test
    public void getGroupsHavingPassedRoleWithGoodIdButEmptyResults_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        List<Group> groups = new ArrayList<>();
        when(authorizationStorage.getGroupsHavingRole(role.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingRoleQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups))));
    }

    @Test
    public void getGroupsHavingPassedRoleWithWrongId_shouldReturnResponseOk() {
        Role role = Role.build(new DefaultKasperId(), "role", new ArrayList<WildcardPermission>());
        Group group = Group.build(new DefaultKasperId(), "group", Arrays.asList(role), new ArrayList<WildcardPermission>(), new ArrayList<User>());
        List<Group> groups = Arrays.asList(group);
        when(authorizationStorage.getGroupsHavingRole(role.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingRoleQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(new ArrayList<Group>()))));
    }

}
