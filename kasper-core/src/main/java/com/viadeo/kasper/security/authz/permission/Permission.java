package com.viadeo.kasper.security.authz.permission;


import java.util.List;

public interface Permission {

    public boolean implies(Permission permission);

    public List<List<String>> getParts();

}
