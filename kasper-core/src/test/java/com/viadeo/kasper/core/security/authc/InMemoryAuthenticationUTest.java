package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class InMemoryAuthenticationUTest {

    private InMemoryAuthentication<String> inMemoryAuthentication;

    @Before
    public void setup(){
        inMemoryAuthentication = new InMemoryAuthentication<String>(new UUIDAuthenticationTokenGenerator());
    }

    @Test
    public void isAuthenticated_withTokenInContext_shouldBe(){
        // Given
        String token = UUID.randomUUID().toString();
        Context context =  Contexts.builder()
                .withAuthenticationToken(token)
                .build();
        inMemoryAuthentication.addToken(token);

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticated_withNoTokenInContext_shouldNotBe(){
        // Given
        Context context =  Contexts.builder().build();

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertFalse(isAuthenticated);
    }

    @Test
    public void isAuthenticated_withDifferentTokenInContext_shouldNotBe(){
        // Given
        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        Context context =  Contexts.builder()
                .withAuthenticationToken(token1)
                .build();
        inMemoryAuthentication.addToken(token2);

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertFalse(isAuthenticated);
    }

    @Test
    public void createAuthenticationToken_withUserInContext_shouldCreate(){
        // Given
        Context context =  Contexts.builder().build();

        // When
        String token = inMemoryAuthentication.createAuthenticationToken(context);

        // Then
        assertNotNull(token);
        context = Contexts.newFrom(context).withAuthenticationToken(token).build();
        assertTrue(inMemoryAuthentication.isAuthenticated(context));
    }

    @Test
    public void removeToken_withTokenInMemory_shouldRemove(){
        // given
        String token = UUID.randomUUID().toString();
        inMemoryAuthentication.addToken(token);
        Context context =  Contexts.builder()
                .withAuthenticationToken(token)
                .build();

        // When
        inMemoryAuthentication.removeToken(token);

        // Then
        assertFalse(inMemoryAuthentication.isAuthenticated(context));
    }

}