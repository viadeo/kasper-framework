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