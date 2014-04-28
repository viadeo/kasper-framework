package com.viadeo.kasper.security.authz;


import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class Permission {

    protected static final String WILDCARD_TOKEN = "*";
    protected static final String PART_DIVIDER_TOKEN = ":";
    protected static final String SUBPART_DIVIDER_TOKEN = ",";

    private List<List<String>> parts;

    public Permission(String permission) {
        setParts(permission);
    }

    protected void setParts(String permission) {
        if (permission == null || permission.trim().length() == 0) {
            throw new IllegalArgumentException("Permission string cannot be null or empty. Make sure permission strings are properly formatted.");
        }

        permission = permission.trim();

        List<String> parts = Arrays.asList(permission.split(PART_DIVIDER_TOKEN));

        this.parts = new ArrayList<>();
        for (String part : parts) {
            List<String> subparts = Arrays.asList(part.split(SUBPART_DIVIDER_TOKEN));
            subparts = lowercase(subparts);
            if (subparts.isEmpty()) {
                throw new IllegalArgumentException("Permission string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
            }
            this.parts.add(subparts);
        }

        if (this.parts.isEmpty()) {
            throw new IllegalArgumentException("Permission string cannot contain only dividers. Make sure permission strings are properly formatted.");
        }
    }

    private List<String> lowercase(List<String> subparts) {
        List<String> lowerCasedSubparts = new ArrayList<String>(subparts.size());
        for (String subpart : subparts) {
            lowerCasedSubparts.add(subpart.toLowerCase());
        }
        return lowerCasedSubparts;
    }

    public boolean implies(Permission permission){
        // By default only supports comparisons with other WildcardPermissions
        if (!(permission instanceof Permission)) {
            return false;
        }

        List<List<String>> otherParts = permission.getParts();

        int i = 0;
        for (List<String> otherPart : otherParts) {
            // If this permission has less parts than the other permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if (getParts().size() - 1 < i) {
                return true;
            } else {
                List<String> part = getParts().get(i);
                if (!part.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
                    return false;
                }
                i++;
            }
        }

        // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
        for (; i < getParts().size(); i++) {
            List<String> part = getParts().get(i);
            if (!part.contains(WILDCARD_TOKEN)) {
                return false;
            }
        }

        return true;
    }

    public List<List<String>> getParts() {
        return parts;
    }
}
