package com.viadeo.kasper.security.authz.permission.impl;

import com.viadeo.kasper.security.authz.permission.Permission;
import junit.framework.Assert;
import org.junit.Test;

public class RoleTest {

    @Test
    public void test_isPermitted_withPermissions_shoudlAssert() {
        //Given
        Role role = new Role("role");
        Permission perm = new WildcardPermission("permission");
        role.add(perm);

        //When
        boolean resultForGoodPerm = role.isPermitted(perm);
        boolean resultForWrongPerm = role.isPermitted(new WildcardPermission("wrongPermission"));

        //Then
        Assert.assertTrue(resultForGoodPerm);
        Assert.assertFalse(resultForWrongPerm);
    }

    @Test
    public void test_removePermission_shouldRemove() {
        //Given
        Role role = new Role("role");
        Permission perm = new WildcardPermission("permission");
        role.add(perm);

        //When
        Permission perm2 = new WildcardPermission("permission");
        role.remove(perm2);

        //Then
        Assert.assertEquals(role.getPermissions().size(), 0);
    }
}
