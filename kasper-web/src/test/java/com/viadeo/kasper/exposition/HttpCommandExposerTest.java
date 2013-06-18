package com.viadeo.kasper.exposition;

import com.viadeo.kasper.cqrs.command.CommandResult.Status;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.platform.IPlatform;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpCommandExposerTest extends BaseHttpExposerTest<HttpCommandExposer> {
	
	public HttpCommandExposerTest() {
	}
	
	@Override
	protected HttpCommandExposer createExposer(ApplicationContext ctx) {
	    return new HttpCommandExposer(ctx.getBean(IPlatform.class), ctx.getBean(IDomainLocator.class));
	}

    // ------------------------------------------------------------------------
	
	@Test
	public void testCommandNotFound() throws Exception {
		// Given an unknown command
		@SuppressWarnings("serial")
		final ICommand unknownCommand = new ICommand() { };

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
		assertEquals(command.name,
				CreateAccountCommandHandler.createAccountCommandName);
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
	
	public static class CreateAccountCommand implements ICommand {
		private static final long serialVersionUID = 674842094873929150L;

		private String name;
		private boolean throwException;

        public String getName() {
            return this.name;
        }

        public boolean isThrowException() {
            return throwException;
        }

	}

    // ------------------------------------------------------------------------

	@XKasperCommandHandler(domain = AccountDomain.class)
	public static class CreateAccountCommandHandler extends AbstractCommandHandler<CreateAccountCommand> {
		static String createAccountCommandName = null;

		@Override
		public CommandResult handle(final CreateAccountCommand command) throws Exception {
		    if (command.isThrowException()) throw new KasperException("Something bad happened!");
			createAccountCommandName = command.getName();
			return CommandResult.ok();
		}
	}

    // ------------------------------------------------------------------------

	public static class AccountDomain extends AbstractDomain { }

}
