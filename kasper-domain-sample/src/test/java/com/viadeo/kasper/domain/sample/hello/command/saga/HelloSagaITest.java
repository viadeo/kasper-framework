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

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(
//        classes = {
//                ConfigTestConfiguration.class,
//                SagaConfiguration.class,
//                HelloSagaITest.TestConfiguration.class,
//                DynamoDBConfiguration.class,
//                MetricConfiguration.class,
//                QuartzSchedulerTestConfiguration.class
//        }
//)
public class HelloSagaITest {

//    @Inject
//    public SagaManager sagaManager;
//
//    @Inject
//    public SagaRepository sagaRepository;
//
//    private HelloSaga saga;
//    private SagaExecutor sagaExecutor;
//
//    @Before
//    public void setup(){
//        saga = new HelloSaga();
//        if(!sagaManager.get(saga.getClass()).isPresent()) {
//            sagaExecutor = sagaManager.register(saga);
//        } else {
//            sagaExecutor = sagaManager.get(saga.getClass()).get();
//        }
//    }
//
//    @Test
//    public void startHelloSaga_should_startAndStore() throws SagaPersistenceException {
//        // Given
//        DefaultKasperId id = new DefaultKasperId();
//
//        // When
//        sagaExecutor.execute(Contexts.empty(), new HelloCreatedEvent(id, "message", "forBuddy"));
//
//        // Then
//        Optional<Saga> saga = sagaRepository.load(id);
//        assertTrue(saga.isPresent());
//        assertEquals("forBuddy", ((HelloSaga) saga.get()).getBuddy());
//
//    }
//
//    @Test
//    public void doStepOnHelloSaga_should_loadAndChangeAndStore() throws SagaPersistenceException {
//        // Given
//        DefaultKasperId id = new DefaultKasperId();
//        sagaExecutor.execute(Contexts.empty(), new HelloCreatedEvent(id, "message", "originalForBuddy"));
//        // Then
//        Optional<Saga> saga = sagaRepository.load(id);
//        assertTrue(saga.isPresent());
//        assertEquals("originalForBuddy", ((HelloSaga) saga.get()).getBuddy());
//
//        // When
//        sagaExecutor.execute(Contexts.empty(), new BuddyChangedForHelloMessageEvent(id, "originalForBuddy", "newForBuddy"));
//
//        // Then
//        saga = sagaRepository.load(id);
//        assertTrue(saga.isPresent());
//        assertEquals("newForBuddy", ((HelloSaga) saga.get()).getBuddy());
//
//    }
//
//    @Test
//    public void doEndOnHelloSaga_should_endSagaAndDeleteOnStore() throws SagaPersistenceException {
//        // Given
//        DefaultKasperId id = new DefaultKasperId();
//        sagaExecutor.execute(Contexts.empty(), new HelloCreatedEvent(id, "message", "originalForBuddy"));
//        // Then
//        Optional<Saga> saga = sagaRepository.load(id);
//        assertTrue(saga.isPresent());
//        assertEquals("originalForBuddy", ((HelloSaga) saga.get()).getBuddy());
//
//        // When
//        sagaExecutor.execute(Contexts.empty(), new HelloDeletedEvent(id, "originalForBuddy"));
//
//        // Then
//        saga = sagaRepository.load(id);
//        assertFalse(saga.isPresent());
//    }
//
//    @Configuration
//    public static class TestConfiguration {
//
//        @Bean
//        public MetricRegistry metricRegistry() {
//            return new MetricRegistry();
//        }
//
//        @Bean
//        public ObjectMapper objectMapper() {
//            return ObjectMapperProvider.INSTANCE.mapper();
//        }
//    }

}