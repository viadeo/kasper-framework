// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Remove a User from a Group for security authorization")
public class RemoveUserFromGroupCommand implements Command {

    @NotNull
    private KasperID userId;

    @NotNull
    private KasperID groupId;

    public RemoveUserFromGroupCommand(final KasperID groupId, final KasperID userId) {
        this.groupId = checkNotNull(groupId);
        this.userId = checkNotNull(userId);
    }

    public KasperID getUserId() {
        return userId;
    }

    public KasperID getGroupId() {
        return groupId;
    }

}
