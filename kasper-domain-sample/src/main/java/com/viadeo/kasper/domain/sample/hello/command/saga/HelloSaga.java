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
package com.viadeo.kasper.domain.sample.hello.command.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.event.BuddyChangedForHelloMessageEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloCreatedEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloDeletedEvent;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@XKasperSaga(domain = HelloDomain.class, description = "hello saga" )
public class HelloSaga implements Saga {

    private static final Logger LOGGER = getLogger(HelloSaga.class);
    private String buddy;
    private KasperID id;

    @Override
    public Optional<SagaIdReconciler> getIdReconciler() {
        return Optional.absent();
    }

    @XKasperSaga.Start(getter = "getEntityId")
    public void start(final HelloCreatedEvent event){
        this.id = event.getEntityId();
        this.buddy = event.getForBuddy();
        LOGGER.info("A Hello saga has started : "+event.getEntityId());
    }

    @XKasperSaga.Step(getter = "getEntityId")
    @XKasperSaga.Schedule(delay = 1, unit = TimeUnit.MINUTES, methodName = "sayCoucou")
    public void stepHelloBuddyChanged(final BuddyChangedForHelloMessageEvent event){
        this.buddy = event.getNewForBuddy();
        LOGGER.info("A Hello step has been triggers : "+event.getEntityId());
    }

    @XKasperSaga.End(getter = "getEntityId")
    public void end(final HelloDeletedEvent event){
        LOGGER.info("A Hello saga has ended : "+event.getEntityId());
    }

    public String getBuddy() {
        return buddy;
    }

    private void sayCoucou(){
        LOGGER.info("A Hello schedule method call has been triggered by scheduler : " + this.id);
    }
}
