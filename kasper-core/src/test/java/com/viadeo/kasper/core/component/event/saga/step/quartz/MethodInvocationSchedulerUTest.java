// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static com.viadeo.kasper.core.component.event.saga.step.quartz.MethodInvocationScheduler.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MethodInvocationSchedulerUTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SagaManager sagaManager;

    private MethodInvocationScheduler stepScheduler;

    @Before
    public void setup() throws SchedulerException {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        when(applicationContext.getBean(SagaManager.class)).thenReturn(sagaManager);

        stepScheduler = new MethodInvocationScheduler(ObjectMapperProvider.INSTANCE.mapper(), scheduler, applicationContext);
        stepScheduler.initialize();
    }

    @After
    public void shutdownScheduler() throws Exception {
        stepScheduler.shutdown();
    }

    @Test
    public void buildJobIdentifier_withGoodEntries_shouldReturnIdentifier() throws NoSuchMethodException {
        // Given
        String identifier = UUID.randomUUID().toString();
        Class<TestFixture.TestSagaA> sagaClass = TestFixture.TestSagaA.class;

        // when
        String jobIdentifier = stepScheduler.buildJobIdentifier(sagaClass, "buildJobIdentifier_withGoodEntries_shouldReturnIdentifier", identifier);

        // Then
        assertNotNull(jobIdentifier);
        assertTrue(jobIdentifier.startsWith(JOB_NAME_PREFIX));
    }

    @Test
    public void buildJobDetail_withGoodEntries_shouldReturnJobDetail() throws NoSuchMethodException, JsonProcessingException {
        // Given
        String methodName = "buildJobDetail_withGoodEntries_shouldReturnJobDetail";
        Class<TestFixture.TestSagaA> sagaClass = TestFixture.TestSagaA.class;
        String identifier = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(stepScheduler.buildJobIdentifier(sagaClass, methodName, identifier), DEFAULT_GROUP_NAME);
        boolean endAfterExecution = true;

        // When
        JobDetail jobDetail = stepScheduler.buildJobDetail(sagaClass, methodName, identifier, jobKey, endAfterExecution);

        // Then
        assertNotNull(jobDetail);
        assertEquals(jobKey, jobDetail.getKey());
        assertEquals(methodName, jobDetail.getJobDataMap().getString(METHOD_KEY));
        assertEquals("\"" + identifier + "\"", jobDetail.getJobDataMap().getString(IDENTIFIER_KEY));
        assertEquals(sagaClass.getName(), jobDetail.getJobDataMap().getString(SAGA_CLASS_KEY), sagaClass.getName());
        assertEquals(endAfterExecution, jobDetail.getJobDataMap().getBoolean(SHOULD_END_SAGA_KEY));
    }

    @Test
    public void buildTrigger_withGoodEntries_shouldReturnTrigger() throws NoSuchMethodException {
        // Given
        DateTime dateTime = DateTime.now().plus(5000);
        String methodName = "buildTrigger_withGoodEntries_shouldReturnTrigger";
        Class<TestFixture.TestSagaA> sagaClass = TestFixture.TestSagaA.class;
        String identifier = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(stepScheduler.buildJobIdentifier(sagaClass, methodName, identifier), DEFAULT_GROUP_NAME);

        // When
        Trigger trigger = stepScheduler.buildTrigger(dateTime, jobKey);

        // Then
        assertNotNull(trigger);
        assertEquals(trigger.getStartTime(), dateTime.toDate());
        assertEquals(trigger.getJobKey(), jobKey);
    }

    @Test
    public void schedule_whitGoodEntries_shouldScheduleJob() throws NoSuchMethodException, SchedulerException {
        // Given
        DateTime dateTime = DateTime.now().plus(5000);
        String methodName = "schedule_whitGoodEntries_shouldScheduleJob";
        Class<TestFixture.TestSagaA> sagaClass = TestFixture.TestSagaA.class;
        String identifier = UUID.randomUUID().toString();

        // When
        String jobIdentifier = stepScheduler.schedule(sagaClass, methodName, identifier, dateTime, false);

        // Then
        assertNotNull(jobIdentifier);
    }

    @Test
    public void cancelSchedule_whitGoodEntries_shouldCancelScheduledJob() throws NoSuchMethodException, SchedulerException {
        // Given
        DateTime dateTime = DateTime.now().plus(5000);
        String methodName = "cancelSchedule_whitGoodEntries_shouldCancelScheduledJob";
        Class<TestFixture.TestSagaA> sagaClass = TestFixture.TestSagaA.class;
        String identifier = UUID.randomUUID().toString();
        String jobIdentifier = stepScheduler.schedule(sagaClass, methodName, identifier, dateTime, false);

        // When
        stepScheduler.cancelSchedule(sagaClass, methodName, identifier, DEFAULT_GROUP_NAME);

        // Then
        assertNotNull(jobIdentifier);
    }

}
