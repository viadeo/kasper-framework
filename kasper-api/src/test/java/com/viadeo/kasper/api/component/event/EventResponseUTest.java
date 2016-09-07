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
package com.viadeo.kasper.api.component.event;

import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventResponseUTest {

    @Test
    public void success_isNotAnError_isNotAFailure_isNotTemporary() {
        EventResponse response = EventResponse.success();
        assertNotNull(response);
        assertTrue(response.isOK());
        assertFalse(response.isAnError());
        assertFalse(response.isAFailure());
        assertFalse(response.isTemporary());
    }

    @Test
    public void failure_isNotATemporary() {
        EventResponse response = EventResponse.failure(new KasperReason(CoreReasonCode.CONFLICT, "Fake message"));
        assertNotNull(response);
        assertFalse(response.isOK());
        assertFalse(response.isAnError());
        assertTrue(response.isAFailure());
        assertFalse(response.isTemporary());
    }

    @Test
    public void temporarilyUnavailable_isATemporaryFailure() {
        EventResponse response = EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.CONFLICT, "Fake message"));
        assertNotNull(response);
        assertFalse(response.isOK());
        assertFalse(response.isAnError());
        assertTrue(response.isAFailure());
        assertTrue(response.isTemporary());
    }

    @Test
    public void error_isNotTemporary() {
        EventResponse response = EventResponse.error(new KasperReason(CoreReasonCode.CONFLICT, "Fake message"));
        assertNotNull(response);
        assertFalse(response.isOK());
        assertTrue(response.isAnError());
        assertFalse(response.isAFailure());
        assertFalse(response.isTemporary());
    }
}
