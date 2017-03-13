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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.google.common.collect.Sets;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.api.exception.KasperEventException;
import com.viadeo.kasper.api.exception.KasperQueryException;
import com.viadeo.kasper.api.exception.KasperSecurityException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.ConstraintViolation;

public class ResiliencePolicyUTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ResiliencePolicy policy;

    @Before
    public void setUp() throws Exception {
        policy = new ResiliencePolicy();
    }

    @Test
    public void manage_a_kasper_query_exception_is_ok() {
        policy.manage(new KasperQueryException(new RuntimeException()));
    }

    @Test
    public void manage_a_kasper_command_exception_is_ok() {
        policy.manage(new KasperCommandException(new RuntimeException()));
    }

    @Test
    public void manage_a_kasper_event_exception_is_ok() {
        policy.manage(new KasperEventException(new RuntimeException()));
    }

    @Test
    public void manage_a_kasper_security_exception_throws_a_hystrix_bad_request_exception() {
        exception.expect(HystrixBadRequestException.class);
        policy.manage(new KasperSecurityException("fake", CoreReasonCode.UNKNOWN_REASON));
    }

    @Test
    public void manage_a_jsr303_voalation_exception_throws_a_hystrix_bad_request_exception() {
        exception.expect(HystrixBadRequestException.class);
        policy.manage(new JSR303ViolationException(Sets.<ConstraintViolation<Object>>newHashSet()));
    }

}
