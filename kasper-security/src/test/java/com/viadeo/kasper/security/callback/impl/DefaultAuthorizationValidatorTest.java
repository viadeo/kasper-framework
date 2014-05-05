package com.viadeo.kasper.security.callback.impl;

import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

import static org.mockito.MockitoAnnotations.Mock;

public class DefaultAuthorizationValidatorTest {

    @Mock
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


    @Test
    public void test_extractRoles_withNull_shouldReturnEmpty() {
        // Given
        DefaultAuthorizationValidator defaultAuthorizationValidator = new DefaultAuthorizationValidator(authorizationSecurityManager);

        //When
        List<String> result = defaultAuthorizationValidator.extractRoles(TestRoleCommand.class);

        //Then
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals("bidule,machin,chose,truc", result.get(0));
    }

    @Test
    public void test_extractMultiplesRoles_withNull_shouldReturnEmpty() {
        // Given
        DefaultAuthorizationValidator defaultAuthorizationValidator = new DefaultAuthorizationValidator(authorizationSecurityManager);

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
        DefaultAuthorizationValidator defaultAuthorizationValidator = new DefaultAuthorizationValidator(authorizationSecurityManager);

        //When
        List<String> result = defaultAuthorizationValidator.extractPermissions(TestPermissionCommand.class);

        //Then
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals("bidule,machin,chose,truc", result.get(0));
    }

    @Test
    public void test_extractComplexePermissions_withNull_shouldReturnEmpty() {
        // Given
        DefaultAuthorizationValidator defaultAuthorizationValidator = new DefaultAuthorizationValidator(authorizationSecurityManager);

        //When
        List<String> result = defaultAuthorizationValidator.extractPermissions(TestComplexePermissionCommand.class);

        //Then
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals("groups:897452:post:newsletter", result.get(0));
    }

}
