package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Contexts;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.interceptor.CommandValidationInterceptor;
import junit.framework.TestCase;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Validation;
import javax.validation.constraints.Min;
import java.util.Locale;

public class BaseValidationInterceptorUTest extends TestCase {

    private class CommandToValidate implements Command {
        @Min(10)
        private int size;

        @NotEmpty
        private String company;

        public CommandToValidate(int size, String company) {
            this.size = size;
            this.company = company;
        }
    }


    public void testBuildExceptionMessage_whenThereAreSeveralConstraintsViolations_shouldListThemInTheExceptionMessage() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final CommandValidationInterceptor<CommandToValidate> actor = new CommandValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                    new CommandToValidate(0, ""),
                    Contexts.empty(),
                    InterceptorChain.<CommandToValidate, CommandResponse>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then
            assertEquals(true, e.getMessage().contains("One or more JSR303 constraints were violated."));
            assertEquals(true, e.getMessage().contains("Field company = []: may not be empty"));
            assertEquals(true, e.getMessage().contains("Field size = [0]: must be greater than or equal to 10"));
        }
    }

    public void testBuildExceptionMessage_whenThereIsOnlyOneConstraintViolation_shouldListItExceptionMessage() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final CommandValidationInterceptor<CommandToValidate> actor = new CommandValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                    new CommandToValidate(0, "companyName"),
                    Contexts.empty(),
                    InterceptorChain.<CommandToValidate, CommandResponse>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then
            assertEquals(true, e.getMessage().contains("One or more JSR303 constraints were violated."));
            assertEquals(true, e.getMessage().contains("Field size = [0]: must be greater than or equal to 10"));
        }
    }
}