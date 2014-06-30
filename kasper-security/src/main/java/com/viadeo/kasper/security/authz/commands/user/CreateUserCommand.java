package com.viadeo.kasper.security.authz.commands.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CreateCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Create a User for security authorization")
public class CreateUserCommand  extends CreateCommand {

    private final String firstName;
    private final String lastName;

    public CreateUserCommand(final KasperID providedId, final String firstName,  final String lastName) {
        super(providedId);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
