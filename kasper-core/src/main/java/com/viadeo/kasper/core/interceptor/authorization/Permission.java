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

//inspired from apache shior
package com.viadeo.kasper.core.interceptor.authorization;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.DefaultKasperId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A <code>Permission</code> is a very flexible permission construct supporting multiple levels of
 * permission matching. However, most people will probably follow some standard conventions as explained below.
 *
 * <h3>Simple Usage</h3>
 * <p>
 * In the simplest form, <code>Permission</code> can be used as a simple permission string. You could grant a
 * user an &quot;editNewsletter&quot; permission and then check to see if the user has the editNewsletter
 * permission by calling
 * <p>
 * <code>subject.isPermitted(&quot;editNewsletter&quot;)</code>
 * <p>
 * This is (mostly) equivalent to
 * <p>
 * <code>subject.isPermitted( new Permission(&quot;editNewsletter&quot;) )</code>
 * <p>
 * but more on that later.
 * <p>
 * The simple permission string may work for simple applications, but it requires you to have permissions like
 * <code>&quot;viewNewsletter&quot;</code>, <code>&quot;deleteNewsletter&quot;</code>,
 * <code>&quot;createNewsletter&quot;</code>, etc. You can also grant a user <code>&quot;*&quot;</code> permissions
 * using the wildcard character (giving this class its name), which means they have <em>all</em> permissions. But
 * using this approach there's no way to just say a user has &quot;all newsletter permissions&quot;.
 * <p>
 * For this reason, <code>Permission</code> supports multiple <em>levels</em> of permissioning.
 *
 * <h3>Multiple Levels</h3>
 * <p>
 * <code>Permission</code> also supports the concept of multiple <em>levels</em>.  For example, you could
 * restructure the previous simple example by granting a user the permission <code>&quot;newsletter:edit&quot;</code>.
 * The colon in this example is a special character used by the <code>Permission</code> that delimits the
 * next token in the permission.
 * <p>
 * In this example, the first token is the <em>domain</em> that is being operated on
 * and the second token is the <em>action</em> being performed. Each level can contain multiple values.  So you
 * could simply grant a user the permission <code>&quot;newsletter:view,edit,create&quot;</code> which gives them
 * access to perform <code>view</code>, <code>edit</code>, and <code>create</code> actions in the <code>newsletter</code>
 * <em>domain</em>. Then you could check to see if the user has the <code>&quot;newsletter:create&quot;</code>
 * permission by calling
 * <p>
 * <code>subject.isPermitted(&quot;newsletter:create&quot;)</code>
 * <p>
 * (which would return true).
 * <p>
 * In addition to granting multiple permissions via a single string, you can grant all permission for a particular
 * level. So if you wanted to grant a user all actions in the <code>newsletter</code> domain, you could simply give
 * them <code>&quot;newsletter:*&quot;</code>. Now, any permission check for <code>&quot;newsletter:XXX&quot;</code>
 * will return <code>true</code>. It is also possible to use the wildcard token at the domain level (or both): so you
 * could grant a user the <code>&quot;view&quot;</code> action across all domains <code>&quot;*:view&quot;</code>.
 *
 * <h3>Instance-level Access Control</h3>
 * <p>
 * Another common usage of the <code>Permission</code> is to model instance-level Access Control Lists.
 * In this scenario you use three tokens - the first is the <em>domain</em>, the second is the <em>action</em>, and
 * the third is the <em>instance</em> you are acting on.
 * <p>
 * So for example you could grant a user <code>&quot;newsletter:edit:12,13,18&quot;</code>.  In this example, assume
 * that the third token is the system's ID of the newsletter. That would allow the user to edit newsletters
 * <code>12</code>, <code>13</code>, and <code>18</code>. This is an extremely powerful way to express permissions,
 * since you can now say things like <code>&quot;newsletter:*:13&quot;</code> (grant a user all actions for newsletter
 * <code>13</code>), <code>&quot;newsletter:view,create,edit:*&quot;</code> (allow the user to
 * <code>view</code>, <code>create</code>, or <code>edit</code> <em>any</em> newsletter), or
 * <code>&quot;newsletter:*:*</code> (allow the user to perform <em>any</em> action on <em>any</em> newsletter).
 * <p>
 * To perform checks against these instance-level permissions, the application should include the instance ID in the
 * permission check like so:
 * <p>
 * <code>subject.isPermitted( &quot;newsletter:edit:13&quot; )</code>
 * <p>
 * There is no limit to the number of tokens that can be used, so it is up to your imagination in terms of ways that
 * this could be used in your application.  However, the Shiro team likes to standardize some common usages shown
 * above to help people get started and provide consistency in the Shiro community.
 *
 * @since 0.9
 */

// inspired from apache shiro

public class Permission implements Serializable {

    /*--------------------------------------------
    |             C O N S T A N T S             |
    ============================================*/
    protected static final String WILDCARD_TOKEN = "*";
    protected static final String PART_DIVIDER_TOKEN = ":";
    protected static final String SUBPART_DIVIDER_TOKEN = ",";
    protected static final boolean DEFAULT_CASE_SENSITIVE = false;

    /*--------------------------------------------
    |    I N S T A N C E   V A R I A B L E S    |
    ============================================*/
    private DefaultKasperId kasperId;
    private List<Set<String>> parts;
    private Optional targetId;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/


    protected Permission() {
        this.kasperId = new DefaultKasperId();
        this.targetId = Optional.absent();
    }

    public Permission(final DefaultKasperId kasperId, final String wildcardString, final Optional targetId) {
        this(kasperId, wildcardString, DEFAULT_CASE_SENSITIVE, targetId);
    }

    public Permission(final String wildcardString) {
        this(new DefaultKasperId(), wildcardString, DEFAULT_CASE_SENSITIVE, Optional.absent());
    }

    public Permission(final String wildcardString, final Optional targetId) {
        this(new DefaultKasperId(), wildcardString, DEFAULT_CASE_SENSITIVE, targetId);
    }

    public Permission(final String wildcardString, final boolean caseSensitive) {
        this(new DefaultKasperId(), wildcardString, caseSensitive, Optional.absent());
    }

    public Permission(final DefaultKasperId kasperId, final String wildcardString, final boolean caseSensitive, final Optional targetId) {
        this.kasperId = checkNotNull(kasperId);
        this.targetId = checkNotNull(targetId);
        setParts(wildcardString, caseSensitive);
    }

    protected void setParts(String wildcardString) {
        setParts(wildcardString, DEFAULT_CASE_SENSITIVE);
    }

    protected void setParts(String wildcardString, boolean caseSensitive) {
        wildcardString = checkNotNull(wildcardString).trim();

        if (wildcardString.length() == 0) {
            throw new IllegalArgumentException("Wildcard string cannot be empty. Make sure permission strings are properly formatted.");
        }

        List<String> parts = Lists.newArrayList(wildcardString.split(PART_DIVIDER_TOKEN));

        this.parts = new ArrayList<Set<String>>();
        for (String part : parts) {
            Set<String> subparts = Sets.newHashSet(part.split(SUBPART_DIVIDER_TOKEN));
            if (!caseSensitive) {
                subparts = lowercase(subparts);
            }
            if (subparts.isEmpty()) {
                throw new IllegalArgumentException("Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
            }
            this.parts.add(subparts);
        }

        if (this.parts.isEmpty()) {
            throw new IllegalArgumentException("Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
        }
    }

    private Set<String> lowercase(Set<String> subparts) {
        Set<String> lowerCasedSubparts = new LinkedHashSet<String>(subparts.size());
        for (String subpart : subparts) {
            lowerCasedSubparts.add(subpart.toLowerCase());
        }
        return lowerCasedSubparts;
    }

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/
    public List<Set<String>> getParts() {
        return this.parts;
    }

    public DefaultKasperId getKasperId() {
        return kasperId;
    }

    public void setKasperId(DefaultKasperId kasperId) {
        this.kasperId = kasperId;
    }

    public Optional getTargetId() {
        return targetId;
    }

    public void setTargetId(Optional targetId) {
        this.targetId = targetId;
    }

    /*--------------------------------------------
    |               M E T H O D S               |
    ============================================*/

    public boolean implies(Permission p) {
        // By default only supports comparisons with other Permissions
        if (!(p instanceof Permission)) {
            return false;
        }

        Permission wp = (Permission) p;

        if(this.targetId.isPresent() && (!wp.targetId.isPresent() || !this.targetId.equals(wp.targetId))){
            return false;
        }

        List<Set<String>> otherParts = wp.getParts();

        int i = 0;
        for (Set<String> otherPart : otherParts) {
            // If this permission has less parts than the other permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if (getParts().size() - 1 < i) {
                return true;
            } else {
                Set<String> part = getParts().get(i);
                if (!part.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
                    return false;
                }
                i++;
            }
        }

        // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
        for (; i < getParts().size(); i++) {
            Set<String> part = getParts().get(i);
            if (!part.contains(WILDCARD_TOKEN)) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (Set<String> part : parts) {
            if (buffer.length() > 0) {
                buffer.append(":");
            }
            buffer.append(part);
        }
        return buffer.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof Permission) {
            Permission wp = (Permission) o;
            return parts.equals(wp.parts);
        }
        return false;
    }

    public int hashCode() {
        return parts.hashCode();
    }

}

