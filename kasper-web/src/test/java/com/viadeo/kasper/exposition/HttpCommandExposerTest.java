package com.viadeo.kasper.exposition;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.ICommandResult.Status;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.event.exceptions.KasperEventException;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.platform.IPlatform;

public class HttpCommandExposerTest extends BaseHttpExposerTest<HttpCommandExposer> {
	
	public HttpCommandExposerTest() {
	}
	
	@Override
	protected HttpCommandExposer createExposer(ApplicationContext ctx) {
	    return new HttpCommandExposer(ctx.getBean(IPlatform.class), ctx.getBean(IDomainLocator.class));
	}
	
	@Test
	public void testCommandNotFound() throws Exception {
		// Given an unknown command
		@SuppressWarnings("serial")
		final ICommand unknownCommand = new ICommand() {
		};

		// When
		final ICommandResult result = client().send(unknownCommand);

		// Then
		assertEquals(Status.ERROR, result.getStatus());
		assertNotNull(result.asError().getErrorMessage().orNull());
	}

	@Test
	public void testSuccessfulCommand() throws IOException, Exception {
		// Given valid input
		final CreateAccountCommand command = new CreateAccountCommand();
		command.name = "foo bar";

		// When
		final ICommandResult result = client().send(command);

		// Then
		assertEquals(Status.OK, result.getStatus());
		assertEquals(command.name,
				CreateAccountCommandHandler.createAccountCommandName);
	}

	public static class CreateAccountCommand implements ICommand {
		private static final long serialVersionUID = 674842094873929150L;
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@XKasperCommandHandler(domain = AccountDomain.class)
	public static class CreateAccountCommandHandler extends
			AbstractCommandHandler<CreateAccountCommand> {
		static String createAccountCommandName = null;

		@Override
		public ICommandResult handle(CreateAccountCommand command)
				throws KasperEventException {
			createAccountCommandName = command.getName();
			return new KasperCommandResult(Status.OK);
		}
	}

	public static class AccountDomain extends AbstractDomain {

	}
}
