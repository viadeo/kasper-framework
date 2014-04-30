package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.actor.Subject;
import com.viadeo.kasper.security.authz.permission.impl.WildcardPermission;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DefaultAuthorizationSecurityManagerTest {

    private DefaultAuthorizationSecurityManager defaultAuthorizationSecurityManager;


    @Before
    public void setUp() {
        this.defaultAuthorizationSecurityManager = new DefaultAuthorizationSecurityManager();
    }


    @Test
    public void test_resolvePermission_shouldReturnPermission() {
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission("coucou,machin,truc");
        System.out.println(permission.implies(permission));


        Set<String> test = new LinkedHashSet<>();
        test.add("truc");
        test.add("machin");
        test.add("truc");
        System.out.println(test.toString());
    }

    @Test
    public void test_resolvePermiertetssion_shouldReturnPermission() {
        Permission permission = defaultAuthorizationSecurityManager.resolvePermission("coucou:machin:truc");
        System.out.println(permission.implies(permission));
    }


    @Test
    public void test_isPermitted_shouldReturnTrue() {

        Permission perm = new WildcardPermission("coucou");
        Role role = new Role("Robert");
        Role role2 = new Role("coucou");
        role.add(perm);
        role2.add(perm);
        List<Role> roles = new ArrayList<>();
        List<Permission> permissions = new ArrayList<>();
        roles.add(role);
        roles.add(role2);
        permissions.add(perm);
        Subject subject = new Subject(roles, permissions);

        defaultAuthorizationSecurityManager.checkPermission("coucou", subject);
        defaultAuthorizationSecurityManager.checkRole("Robert", subject);
        List<String> rolesToCheck = new ArrayList<>();
        rolesToCheck.add("Robert");
        rolesToCheck.add("coucou");
        defaultAuthorizationSecurityManager.checkRoles(rolesToCheck, subject);
    }

}
