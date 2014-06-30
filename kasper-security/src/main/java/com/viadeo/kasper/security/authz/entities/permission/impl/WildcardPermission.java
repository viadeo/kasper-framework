// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// copied from apache shiro

package com.viadeo.kasper.security.authz.entities.permission.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.events.permission.PermissionCreatedEvent;
import com.viadeo.kasper.security.authz.events.permission.PermissionDeletedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.io.Serializable;
import java.util.*;

@XKasperConcept(domain = Authorization.class, description = "", label = "Permission")
public class WildcardPermission extends Concept implements Permission, Serializable {

    protected static final String WILDCARD_TOKEN = "*";
    protected static final String PART_DIVIDER_TOKEN = ":";
    protected static final String SUBPART_DIVIDER_TOKEN = ",";
    protected static final boolean DEFAULT_CASE_SENSITIVE = false;

    private List<Set<String>> parts;

    // ------------------------------------------------------------------------


    public WildcardPermission() {
        apply(new PermissionCreatedEvent(new DefaultKasperId()));
    }

    protected WildcardPermission(final KasperID kasperId) {
        apply(new PermissionCreatedEvent(kasperId));
    }

    public WildcardPermission(final String wildcardString) {
        apply(new PermissionCreatedEvent(new DefaultKasperId(), wildcardString, DEFAULT_CASE_SENSITIVE));
    }

    public WildcardPermission(final KasperID kasperId, final String wildcardString) {
        apply(new PermissionCreatedEvent(kasperId, wildcardString, DEFAULT_CASE_SENSITIVE));
    }

    public WildcardPermission(final String wildcardString, final boolean caseSensitive) {
        apply(new PermissionCreatedEvent(new DefaultKasperId(), wildcardString, caseSensitive));
    }

    public WildcardPermission(final KasperID kasperId, final String wildcardString, final boolean caseSensitive) {
        apply(new PermissionCreatedEvent(kasperId, wildcardString, caseSensitive));
    }

    @EventHandler
    public void onCreated(PermissionCreatedEvent event) {
        setId(event.getEntityId());
        setParts(event.getWildcardString(), event.isCaseSensitive());
    }

    public WildcardPermission delete() {
        apply(new PermissionDeletedEvent(getEntityId()));
        return this;
    }

    @EventHandler
    public void onDeleted(final PermissionDeletedEvent e) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    protected void setParts(final String wildcardString) {
        setParts(wildcardString, DEFAULT_CASE_SENSITIVE);
    }

    protected void setParts(final String wildcardString, final boolean caseSensitive) {
        if ((null == wildcardString) || (0 == wildcardString.trim().length())) {
            throw new IllegalArgumentException("Wildcard string cannot be null or empty.");
        }

        final List<String> parts = Arrays.asList(wildcardString.trim().split(PART_DIVIDER_TOKEN));

        this.parts = new ArrayList<Set<String>>();
        for (final String part : parts) {
            Set<String> subparts = new HashSet<String>(Arrays.asList(part.split(SUBPART_DIVIDER_TOKEN)));
            if (!caseSensitive) {
                subparts = lowercase(subparts);
            }
            if (subparts.isEmpty()) {
                throw new IllegalArgumentException(
                        "Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted."
                );
            }
            this.parts.add(subparts);
        }

        if (this.parts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted."
            );
        }
    }

    private Set<String> lowercase(final Set<String> subparts) {
        final Set<String> lowerCasedSubparts = new LinkedHashSet<String>(subparts.size());
        for (final String subpart : subparts) {
            lowerCasedSubparts.add(subpart.toLowerCase());
        }
        return lowerCasedSubparts;
    }

    // ------------------------------------------------------------------------


    protected List<Set<String>> getParts() {
        return this.parts;
    }

    // ------------------------------------------------------------------------

    public boolean implies(final Permission p) {

        // By default only supports comparisons with other WildcardPermissions
        if (!(p instanceof WildcardPermission)) {
            return false;
        }

        final WildcardPermission wp = (WildcardPermission) p;
        final List<Set<String>> otherParts = wp.getParts();

        int i = 0;
        for (final Set<String> otherPart : otherParts) {
            // If this permission has less parts than the other permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if ((getParts().size() - 1) < i) {
                return true;
            } else {
                final Set<String> part = getParts().get(i);
                if ((!part.contains(WILDCARD_TOKEN)) && (!part.containsAll(otherPart))) {
                    return false;
                }
                i++;
            }
        }

        // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
        for (; i < getParts().size(); i++) {
            final Set<String> part = getParts().get(i);
            if (!part.contains(WILDCARD_TOKEN)) {
                return false;
            }
        }

        return true;
    }

    // ------------------------------------------------------------------------

    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        for (final Set<String> part : parts) {
            if (buffer.length() > 0) {
                buffer.append(":");
            }
            buffer.append(part);
        }
        return buffer.toString();
    }

    public boolean equals(final Object o) {
        if (o instanceof WildcardPermission) {
            final WildcardPermission wp = (WildcardPermission) o;
            return parts.equals(wp.parts);
        }
        return false;
    }

    public int hashCode() {
        return parts.hashCode();
    }

}
