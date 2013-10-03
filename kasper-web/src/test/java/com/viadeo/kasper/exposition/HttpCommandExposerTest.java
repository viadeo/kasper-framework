// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.command.CommandResult.Status;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.exception.KasperException;
import lombok.Data;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class HttpCommandExposerTest extends BaseHttpExposerTest<HttpCommandExposer> {

    public HttpCommandExposerTest() {
        Locale.setDefault(Locale.US);
    }

    @Override
    protected HttpCommandExposer createExposer(final ApplicationContext ctx) {
        return new HttpCommandExposer(ctx.getBean(CommandGateway.class), ctx.getBean(DomainLocator.class));
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
        assertNotNull(result.getError().getMessages().get(0));
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
        command.code = "code";
        command.messages = ImmutableList.of("a", "aa", "aaa");

        // When
        final CommandResult result = client().send(command);

        // Then
        assertEquals(Status.ERROR, result.getStatus());
        assertEquals(command.getCode(), result.getError().getCode());
        for (int i = 0; i < command.getMessages().size(); i++)
            assertEquals(command.getMessages().get(i), result.getError().getMessages().get(i));
    }

    @Test public void testJSR303Validation() {
        // Given
        final NeedValidationCommand command = new NeedValidationCommand();
        command.setStr("");
        command.setInnerObject(new InnerObject());

        // When
        final CommandResult result = client().send(command);

        // Then
        assertTrue(result.isError());
        final List<String> errorStrings = new ArrayList<String>() {{
            add("innerObject.age : must be greater than or equal to 2");
            add("str : size must be between 1 and 2147483647");
        }};
        for (final String errorMessage : result.getError().getMessages()) {
            if (!errorStrings.contains(errorMessage)) {
                fail(String.format("Cannot find expected validation message : %s", errorMessage));
            }
            errorStrings.remove(errorMessage);
        }
        assertEquals(0, errorStrings.size());
    }

    // ------------------------------------------------------------------------

    public static class CreateAccountCommand implements Command {
        private static final long serialVersionUID = 674842094873929150L;

        private String name;
        private boolean throwException;
        private String code;
        private List<String> messages;

        public String getName() {
            return this.name;
        }

        public boolean isThrowException() {
            return throwException;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
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
            if (command.getCode() != null)
                return CommandResult.error(new KasperError(command.getCode(), command.getMessages()));
            createAccountCommandName = command.getName();
            return CommandResult.ok();
        }
    }

    @Data
    public static class NeedValidationCommand implements Command {
        @NotNull @Size(min = 1) private String str;
        @Valid @NotNull private InnerObject innerObject;
    }

    @Data
    public static class InnerObject {
        @Min(2) @Max(5) private int age;
    }

    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class NeedValidationCommandHandler extends AbstractCommandHandler<NeedValidationCommand> { }

}















