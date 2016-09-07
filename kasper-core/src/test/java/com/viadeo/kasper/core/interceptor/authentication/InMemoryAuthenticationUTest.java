// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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