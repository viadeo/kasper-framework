package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class PermissionRepository extends Repository<WildcardPermission> {

    private AuthorizationStorage authorizationStorage;

    public PermissionRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<WildcardPermission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return Optional.of(authorizationStorage.getPermission(aggregateIdentifier));
    }

    @Override
    protected void doSave(final WildcardPermission aggregate) {
        authorizationStorage.createPermission(aggregate);
    }

    @Override
    protected void doUpdate(final WildcardPermission permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(final WildcardPermission aggregate) {
        authorizationStorage.deletePermission(aggregate);
    }

    public List<WildcardPermission> getAllPermissions(){
        return authorizationStorage.getAllPermissions();
    }
}
