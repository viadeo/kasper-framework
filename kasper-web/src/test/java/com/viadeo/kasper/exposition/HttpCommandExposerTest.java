package com.viadeo.kasper.exposition;

import java.util.Arrays;
import java.util.List;

import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResult.Status;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.platform.Platform;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpCommandExposerTest extends BaseHttpExposerTest<HttpCommandExposer> {

    public HttpCommandExposerTest() {
    }

    @Override
    protected HttpCommandExposer createExposer(ApplicationContext ctx) {
        return new HttpCommandExposer(ctx.getBean(Platform.class), ctx.getBean(DomainLocator.class));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandNotFound() throws Exception {
        // Given an unknown command
        @SuppressWarnings("serial")
        final Command unknownCommand = new Command() {};

        // When
        final CommandResult result = client().send(unknownCommand);

        // Then
        assertEquals(Status.ERROR, result.getStatus());
        assertNotNull(result.getErrors().get().get(0).getMessage());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSuccessfulCommand() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";

        // When
        final CommandResult result = client().send(command);

        // Then
        assertEquals(Status.OK, result.getStatus());
        assertEquals(command.name, CreateAccountCommandHandler.createAccountCommandName);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testExceptionCommand() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";
        command.throwException = true;

        // When
        final CommandResult result = client().send(command);

        // Then
        assertEquals(Status.ERROR, result.getStatus());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandResultWithListOfErrors() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.errors = Arrays.asList(new KasperError("a", "aa", "aaa"), new KasperError("c", "cc"));

        // When
        final CommandResult result = client().send(command);

        // Then
        assertEquals(Status.ERROR, result.getStatus());
        for (int i = 0; i < command.getErrors().size(); i++)
            assertEquals(command.getErrors().get(i), result.getErrors().get().get(i));
    }

    // ------------------------------------------------------------------------

    public static class CreateAccountCommand implements Command {
        private static final long serialVersionUID = 674842094873929150L;

        private String name;
        private boolean throwException;
        private List<KasperError> errors;

        public String getName() {
            return this.name;
        }

        public boolean isThrowException() {
            return throwException;
        }

        public List<KasperError> getErrors() {
            return errors;
        }

        public void setErrors(List<KasperError> errors) {
            this.errors = errors;
        }

    }

    // ------------------------------------------------------------------------

    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class CreateAccountCommandHandler extends AbstractCommandHandler<CreateAccountCommand> {
        static String createAccountCommandName = null;

        @Override
        public CommandResult handle(final CreateAccountCommand command) throws Exception {
            if (command.isThrowException())
                throw new KasperException("Something bad happened!");
            if (command.getErrors() != null)
                return CommandResult.error().addErrors(command.getErrors()).create();
            createAccountCommandName = command.getName();
            return CommandResult.ok();
        }
    }

}















