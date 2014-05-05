package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.security.authz.actor.User;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.authz.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DefaultAuthorizationSecurityManagerTest {

    private DefaultAuthorizationSecurityManager defaultAuthorizationSecurityManager;

    private Subject subject = new User();

    @Before
    public void setUp() {
        this.defaultAuthorizationSecurityManager = new DefaultAuthorizationSecurityManager();
    }


    @Test
    public void test_resolvePermission_shouldReturnPermission() {
        //Given
        String perm = "coucou,machin,truc";

        //When
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);

        //Then
        Assert.assertNotNull(permission);
    }

    @Test
    public void test_chekPermission_withGoodPermission_shouldReturnTrue() {
        //Given
        String perm = "coucou";
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        List<Permission> permissions = new ArrayList<>();
        permissions.add(permission);
        subject.setPermissions(permissions);

        //When
        defaultAuthorizationSecurityManager.checkPermission(perm, subject);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekPermission_withWrongPermission_shouldReturnThrowException() {
        //Given
        String perm = "coucou";
        String wrongPerm = "pascoucou";
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        List<Permission> permissions = new ArrayList<>();
        permissions.add(permission);
        subject.setPermissions(permissions);

        //When
        defaultAuthorizationSecurityManager.checkPermission(wrongPerm, subject);
    }

    @Test()
    public void test_chekRole_withGoodRole_shouldReturnTrue() {
        //Given
        String roleStr = "Robert";
        Role role = new Role(roleStr);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        subject.setRoles(roles);

        //When
        defaultAuthorizationSecurityManager.checkRole(roleStr, subject);
    }

    @Test()
    public void test_chekPermission_withGoodPermissionInRole_shouldReturnTrue() {
        //Given
        String roleStr = "Robert";
        String perm = "coucou";
        Role role = new Role(roleStr);
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        role.add(permission);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        subject.addRoles(roles);

        //When
        defaultAuthorizationSecurityManager.checkPermission(perm, subject);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekRole_withWrongRole_shouldReturnThrowException() {
        //Given
        String roleStr = "Robert";
        String wrongRoleStr = "PasRobert";
        Role role = new Role(roleStr);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        subject.setRoles(roles);

        //When
        defaultAuthorizationSecurityManager.checkRole(wrongRoleStr, subject);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekPermission_withWrongPermissionInRole_shouldReturnThrowException() {
        //Given
        String roleStr = "Robert";
        String perm = "coucou";
        String wrongPerm = "pascoucou";
        Role role = new Role(roleStr);
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        role.add(permission);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        subject.addRoles(roles);

        //When
        defaultAuthorizationSecurityManager.checkPermission(wrongPerm, subject);
    }




}
