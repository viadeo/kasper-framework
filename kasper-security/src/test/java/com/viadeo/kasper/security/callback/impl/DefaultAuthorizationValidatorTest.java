package com.viadeo.kasper.security.callback.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.mgt.impl.DefaultAuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

public class DefaultAuthorizationValidatorTest {

    AuthorizationSecurityManager authorizationSecurityManager;

    @XKasperRequireRoles("bidule,machin,chose,truc")
    @XKasperCommand
    private static class TestRoleCommand implements Command {
    }

    @XKasperRequireRoles({"bidule,machin", "chose,truc"})
    @XKasperCommand
    private static class TestMultiplesRolesCommand implements Command {
    }

    @XKasperRequirePermissions("bidule,machin,chose,truc")
    @XKasperCommand
    private static class TestPermissionCommand implements Command {
    }

    @XKasperRequirePermissions("groups:897452:post:newsletter")
    @XKasperCommand
    private static class TestComplexePermissionCommand implements Command {
    }

    @XKasperRequireRoles("Robert")
    @XKasperCommand
    private static class TestSimpleRoleCommand implements Command {
    }

    @XKasperRequirePermissions("coucou")
    @XKasperCommand
    private static class TestSimplePermissionCommand implements Command {
    }

    DefaultAuthorizationValidator defaultAuthorizationValidator;

    @Before
    public void setUp() {
        authorizationSecurityManager = spy(new DefaultAuthorizationSecurityManager());
        defaultAuthorizationValidator = new DefaultAuthorizationValidator(authorizationSecurityManager);
    }

    @Test
    public void test_extractRoles_withNull_shouldReturnEmpty() {
        // Given

        //When
        List<String> result = defaultAuthorizationValidator.extractRoles(TestRoleCommand.class);

        //Then
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals("bidule,machin,chose,truc", result.get(0));
    }

    @Test
    public void test_extractMultiplesRoles_withNull_shouldReturnEmpty() {
        // Given

        //When
        List<String> result = defaultAuthorizationValidator.extractRoles(TestMultiplesRolesCommand.class);

        //Then
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals("bidule,machin", result.get(0));
        Assert.assertEquals("chose,truc", result.get(1));
    }

    @Test
    public void test_extractPermissions_withNull_shouldReturnEmpty() {
        // Given

        //When
        List<String> result = defaultAuthorizationValidator.extractPermissions(TestPermissionCommand.class);

        //Then
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals("bidule,machin,chose,truc", result.get(0));
    }

    @Test
    public void test_extractComplexePermissions_withNull_shouldReturnEmpty() {
        // Given

        //When
        List<String> result = defaultAuthorizationValidator.extractPermissions(TestComplexePermissionCommand.class);

        //Then
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals("groups:897452:post:newsletter", result.get(0));
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodRole_shouldGoThrough(){
        // Given
        Subject subject = initTestSubject();
        Context context = new DefaultContext();
        when(authorizationSecurityManager.getSubject(context)).thenReturn(subject);

        //When
        defaultAuthorizationValidator.validate(context, TestSimpleRoleCommand.class);
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodPermissionFromRole_shouldGoThrough(){
        // Given
        Subject subject = initTestSubject();
        Context context = new DefaultContext();
        when(authorizationSecurityManager.getSubject(context)).thenReturn(subject);

        //When
        defaultAuthorizationValidator.validate(context, TestSimplePermissionCommand.class);
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodPermission_shouldGoThrough(){
        // Given
        Subject subject = initTestSubject();
        subject.setPermissions(subject.getRoles().get(0).getPermissions());
        subject.setRoles(new ArrayList<Role>());
        Context context = new DefaultContext();
        when(authorizationSecurityManager.getSubject(context)).thenReturn(subject);

        //When
        defaultAuthorizationValidator.validate(context, TestSimplePermissionCommand.class);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_validate_withGoodSubjectAndWrongRole_shouldThrowException(){
        // Given
        Subject subject = initTestSubject();
        Context context = new DefaultContext();
        when(authorizationSecurityManager.getSubject(context)).thenReturn(subject);

        //When
        defaultAuthorizationValidator.validate(context, TestRoleCommand.class);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_validate_withGoodSubjectAndWrongPermission_shouldThrowException(){
        // Given
        Subject subject = initTestSubject();
        Context context = new DefaultContext();
        when(authorizationSecurityManager.getSubject(context)).thenReturn(subject);

        //When
        defaultAuthorizationValidator.validate(context, TestPermissionCommand.class);
    }

    private Subject initTestSubject(){
        Subject subject = new Subject();
        String roleStr = "Robert";
        String perm = "coucou";
        Role role = new Role(roleStr);
        Permission permission = new WildcardPermission(perm);
        role.add(permission);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        subject.addRoles(roles);
        return subject;
    }

}
