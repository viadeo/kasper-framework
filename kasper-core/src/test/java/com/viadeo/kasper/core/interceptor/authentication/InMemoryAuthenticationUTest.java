package com.viadeo.kasper.core.interceptor.authentication;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.id.TestFormats;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class InMemoryAuthenticationUTest {

    private InMemoryAuthentication inMemoryAuthentication;

    @Before
    public void setup(){
        inMemoryAuthentication = new InMemoryAuthentication();
    }

    @Test
    public void isAuthenticated_withTokenInContext_shouldBe(){
        // Given
        String token = UUID.randomUUID().toString();
        Context context =  Contexts.builder()
                .withAuthenticationToken(token)
                .build();
        inMemoryAuthentication.addToken(token, new ID("kasper","subject", TestFormats.UUID, UUID.randomUUID()));

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticated_withUserIDInContext_shouldBe(){
        // Given
        Context context =  Contexts.builder()
                .withUserID(new ID("viadeo", "member", TestFormats.ID, 42))
                .build();

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
        inMemoryAuthentication.addToken(token2, new ID("kasper","subject", TestFormats.UUID, UUID.randomUUID()));

        // When
        boolean isAuthenticated = inMemoryAuthentication.isAuthenticated(context);

        //Then
        assertFalse(isAuthenticated);
    }

    @Test
    public void removeToken_withTokenInMemory_shouldRemove(){
        // given
        String token = UUID.randomUUID().toString();
        inMemoryAuthentication.addToken(token, new ID("kasper","subject", TestFormats.UUID, UUID.randomUUID()));
        Context context =  Contexts.builder()
                .withAuthenticationToken(token)
                .build();

        // When
        inMemoryAuthentication.revoke(token);

        // Then
        assertFalse(inMemoryAuthentication.isAuthenticated(context));
    }

    @Test
    public void getSubject_withNoToken_shouldReturnAbsent(){
        // given
        Context context =  Contexts.builder().build();

        // When
        Optional<ID> subject = inMemoryAuthentication.getSubject(context);

        // Then
        assertFalse(subject.isPresent());
    }

    @Test
    public void getSubject_withNoTokenStored_shouldReturnAbsent(){
        // given
        String token = UUID.randomUUID().toString();
        Context context =  Contexts.builder().withAuthenticationToken(token).build();

        // When
        Optional<ID> subject = inMemoryAuthentication.getSubject(context);

        // Then
        assertFalse(subject.isPresent());
    }

    @Test
    public void getSubject_withTokenStored_shouldReturnSubject(){
        // given
        String token = UUID.randomUUID().toString();
        ID subjectID = new ID("kasper","subject", TestFormats.UUID, UUID.randomUUID());
        inMemoryAuthentication.addToken(token, subjectID);
        Context context =  Contexts.builder()
                .withAuthenticationToken(token)
                .build();

        // When
        Optional<ID> subject = inMemoryAuthentication.getSubject(context);

        // Then
        assertTrue(subject.isPresent());
        assertEquals(subjectID, subject.get());
    }

}