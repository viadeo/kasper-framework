// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.CommandResponse.Status;
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
import javax.ws.rs.core.Response;
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
        final CommandResponse response = client().send(unknownCommand);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertNotNull(response.getReason().getMessages().toArray()[0]);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSuccessfulCommand() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";

        // When
        final CommandResponse response = client().send(command);

        // Then
        assertEquals(Status.OK, response.getStatus());
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
        final CommandResponse response = client().send(command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
    }

    @Test
    public void testCommandResponseWithStatusCode() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.code = CoreReasonCode.CONFLICT.toString();
        command.messages = ImmutableList.of("ignored");

        // When
        final CommandResponse response = client().send(command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertEquals(command.getCode(), response.getReason().getCode());
        assertEquals(Response.Status.CONFLICT, response.asHttp().getHTTPStatus());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandResponseWithListOfErrors() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.code = "code";
        command.messages = ImmutableList.of("a", "aa", "aaa");

        // When
        final CommandResponse response = client().send(command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertEquals(command.getCode(), response.getReason().getCode());
        final String[] responseMessages = response.getReason().getMessages().toArray(new String[0]);
        for (int i = 0; i < command.getMessages().size(); i++) {
            assertEquals(command.getMessages().get(i), responseMessages[i]);
        }
    }

    @Test public void testJSR303Validation() {
        // Given
        final NeedValidationCommand command = new NeedValidationCommand();
        command.str = "";
        command.innerObject = new InnerObject();

        // When
        final CommandResponse response = client().send(command);

        // Then
        assertFalse(response.isOK());
        final List<String> errorStrings = new ArrayList<String>() {{
            add("innerObject.age : must be greater than or equal to 2");
            add("str : size must be between 1 and 2147483647");
        }};
        for (final String errorMessage : response.getReason().getMessages()) {
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
        public CommandResponse handle(final CreateAccountCommand command) throws Exception {
            if (command.isThrowException())
                throw new KasperException("Something bad happened!");
            if (command.getCode() != null)
                return CommandResponse.error(new KasperReason(command.getCode(), command.getMessages()));
            createAccountCommandName = command.getName();
            return CommandResponse.ok();
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















