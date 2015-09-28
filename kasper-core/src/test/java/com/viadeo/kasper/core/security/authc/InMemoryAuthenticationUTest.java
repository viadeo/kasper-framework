package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InMemoryAuthenticationUTest {

    private InMemoryAuthentication inMemoryAuthentication;

    @Before
    public void setup(){
        inMemoryAuthentication = new InMemoryAuthentication();
    }

    @Test
    public void isAuthenticated_withTokenInContext_shouldBe(){
        // Given
        UUID token = UUID.randomUUID();
        inMemoryAuthentication.addToken(token);
        Context context = getContext(token);

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticated_withNoTokenInContext_shouldNotBe(){
        // Given
        Context context = getContext(UUID.randomUUID());

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertFalse(isAuthenticated);
    }

    private Context getContext(UUID token) {
        return Contexts.builder().withAuthenticationToken(token).build();
    }


}