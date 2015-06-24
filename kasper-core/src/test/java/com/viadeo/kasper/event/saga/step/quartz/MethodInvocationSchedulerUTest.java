package com.viadeo.kasper.event.saga.step.quartz;

import com.viadeo.kasper.event.saga.SagaManager;
import com.viadeo.kasper.event.saga.TestFixture;
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

import static com.viadeo.kasper.event.saga.step.quartz.MethodInvocationScheduler.*;
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

        stepScheduler = new MethodInvocationScheduler(scheduler, applicationContext);
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
    public void buildJobDetail_withGoodEntries_shouldReturnJobDetail() throws NoSuchMethodException {
        // Given
        String methodName = "buildJobDetail_withGoodEntries_shouldReturnJobDetail";
        Class<TestFixture.TestSagaA> sagaClass = TestFixture.TestSagaA.class;
        String identifier = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(stepScheduler.buildJobIdentifier(sagaClass, methodName, identifier), DEFAULT_GROUP_NAME);

        // When
        JobDetail jobDetail = stepScheduler.buildJobDetail(sagaClass, methodName, identifier, jobKey);

        // Then
        assertNotNull(jobDetail);
        assertEquals(jobDetail.getKey(), jobKey);
        assertEquals(jobDetail.getJobDataMap().getString(METHOD_KEY), methodName);
        assertEquals(jobDetail.getJobDataMap().getString(IDENTIFIER_KEY), identifier);
        assertEquals(jobDetail.getJobDataMap().getString(SAGA_CLASS_KEY), sagaClass.getName());
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
        String jobIdentifier = stepScheduler.schedule(sagaClass, methodName, identifier, dateTime);

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
        String jobIdentifier = stepScheduler.schedule(sagaClass, methodName, identifier, dateTime);

        // When
        stepScheduler.cancelSchedule(sagaClass, methodName, identifier, DEFAULT_GROUP_NAME);

        // Then
        assertNotNull(jobIdentifier);
    }

}