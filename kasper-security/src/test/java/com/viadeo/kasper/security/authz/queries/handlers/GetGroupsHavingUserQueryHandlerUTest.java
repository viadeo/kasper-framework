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
import com.viadeo.kasper.security.authz.queries.GetGroupsHavingUserQuery;
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
public class GetGroupsHavingUserQueryHandlerUTest {

    private KasperPlatformFixture fixture;

    @Mock
    private AuthorizationStorage authorizationStorage;

    private Context context;

    @Before
    public void setUp() {
        context = new DefaultContext();
        GetGroupsHavingUserQueryHandler queryHandler = new GetGroupsHavingUserQueryHandler(authorizationStorage);
        this.fixture = new KasperPlatformFixture().register(
                new DefaultDomainBundle.Builder(new Authorization())
                        .with(queryHandler)
                        .build()
        );

    }

    @Test
    public void getGroupsHavingPassedUserWithGoodId_shouldReturnResponseOk() {
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), Arrays.asList(user));
        List<Group> groups = Arrays.asList(group);
        when(authorizationStorage.getGroupsHavingUser(user.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingUserQuery(user.getEntityId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups))));
    }

    @Test
    public void getGroupsHavingPassedUserWithGoodIdButEmptyResults_shouldReturnResponseOk() {
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        List<Group> groups = new ArrayList<>();
        when(authorizationStorage.getGroupsHavingUser(user.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingUserQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups))));
    }

    @Test
    public void getGroupsHavingPassedUserWithWrongId_shouldReturnResponseOk() {
        User user = User.build(new DefaultKasperId(), "Robert", "Truc", new ArrayList<Role>(), new ArrayList<WildcardPermission>());
        Group group = Group.build(new DefaultKasperId(), "group", new ArrayList<Role>(), new ArrayList<WildcardPermission>(), Arrays.asList(user));
        List<Group> groups = Arrays.asList(group);
        when(authorizationStorage.getGroupsHavingUser(user.getIdentifier())).thenReturn(groups);
        fixture.given()
                .when(new GetGroupsHavingUserQuery(new DefaultKasperId()), context)
                .expectReturnResponse(QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(new ArrayList<Group>()))));
    }

}
