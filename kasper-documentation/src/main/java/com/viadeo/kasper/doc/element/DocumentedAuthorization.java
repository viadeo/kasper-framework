// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

public class DocumentedAuthorization {

    private AuthorizationElement permissions;
    private AuthorizationElement roles;

    public AuthorizationElement getPermissions() {
        return permissions;
    }

    public void setPermissions(final AuthorizationElement permissions) {
        this.permissions = permissions;
    }

    public AuthorizationElement getRoles() {
        return roles;
    }

    public void setRoles(final AuthorizationElement roles) {
        this.roles = roles;
    }
}
