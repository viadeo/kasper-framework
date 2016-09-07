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
package com.viadeo.kasper.spring.core;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.viadeo.kasper.cqrs.command.FixtureUseCase;
import com.viadeo.kasper.cqrs.command.FixtureUseCaseSpringConfiguration;
import com.viadeo.kasper.spring.platform.SpringDomainBundle;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.SimpleDomainEventStream;
import org.axonframework.eventstore.EventStore;
import org.junit.Before;

import java.util.Collection;
import java.util.List;

public class TestFixturePlatformSpringTest {

    protected KasperPlatformFixture fixture;

    // ========================================================================

    @Before
    public void resetFixture() {
        EventStore eventStore = new EventStore() {
            private final Multimap<String, DomainEventMessage> messageByIdentifiers = Multimaps.newListMultimap(
                    Maps.<String, Collection<DomainEventMessage>>newHashMap(),
                    new Supplier<List<DomainEventMessage>>() {
                        @Override
                        public List<DomainEventMessage> get() {
                            return Lists.newArrayList();
                        }
                    }
            );

            @Override
            public void appendEvents(String type, DomainEventStream events) {
                while (events.hasNext()) {
                    final DomainEventMessage eventMessage = events.next();
                    messageByIdentifiers.put(String.valueOf(eventMessage.getAggregateIdentifier()), eventMessage);
                }
            }

            @Override
            public DomainEventStream readEvents(String type, Object identifier) {
                return new SimpleDomainEventStream(messageByIdentifiers.get(String.valueOf(identifier)));
            }
        };

        final SpringDomainBundle domainBundle = new SpringDomainBundle(
                new FixtureUseCase.TestDomain()
                , Lists.<Class>newArrayList(FixtureUseCaseSpringConfiguration.class)
                , new SpringDomainBundle.BeanDescriptor(eventStore)
        );

        this.fixture = new KasperPlatformFixture();
        this.fixture.register(domainBundle);
    }

}
