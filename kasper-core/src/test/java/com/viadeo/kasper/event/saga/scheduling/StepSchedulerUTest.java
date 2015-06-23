package com.viadeo.kasper.event.saga.scheduling;

import com.viadeo.kasper.event.saga.Saga;
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

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepSchedulerUTest {

    private Scheduler scheduler;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SagaManager sagaManager;

    private StepScheduler stepScheduler;

    @Before
    public void setup() throws SchedulerException {
        StdSchedulerFactory schedFact = new StdSchedulerFactory();
        scheduler = schedFact.getScheduler();

        when(applicationContext.getBean(SagaManager.class)).thenReturn(sagaManager);

        stepScheduler = new StepScheduler(scheduler, applicationContext);
        stepScheduler.initialize();
    }

    @After
    public void shutdownScheduler() throws Exception {
        stepScheduler.shutdown();
    }

    @Test
    public void buildJobIdentifier_withGoodEntries_shouldReturnIdentifier() throws NoSuchMethodException {
        // Given
        Method method = this.getClass().getDeclaredMethod("buildJobIdentifier_withGoodEntries_shouldReturnIdentifier");
        String identifier = UUID.randomUUID().toString();
        Saga saga = new TestFixture.TestSagaA();

        // when
        String jobIdentifier = stepScheduler.buildJobIdentifier(saga, method, identifier);

        // Then
        assertNotNull(jobIdentifier);
        assertTrue(jobIdentifier.startsWith(StepScheduler.JOB_NAME_PREFIX));
    }

    @Test
    public void buildJobDetail_withGoodEntries_shouldReturnJobDetail() throws NoSuchMethodException {
        // Given
        Method method = this.getClass().getDeclaredMethod("buildJobDetail_withGoodEntries_shouldReturnJobDetail");
        Saga saga = new TestFixture.TestSagaA();
        String identifier = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(stepScheduler.buildJobIdentifier(saga, method, identifier), StepScheduler.DEFAULT_GROUP_NAME);

        // When
        JobDetail jobDetail = stepScheduler.buildJobDetail(saga, method, identifier, jobKey);

        // Then
        assertNotNull(jobDetail);
        assertEquals(jobDetail.getKey(), jobKey);
        assertEquals(jobDetail.getJobDataMap().getString(InvokeScheduledStepJob.METHOD_KEY), method.getName());
        assertEquals(jobDetail.getJobDataMap().getString(InvokeScheduledStepJob.IDENTIFIER_KEY), identifier);
        assertEquals(jobDetail.getJobDataMap().getString(InvokeScheduledStepJob.SAGA_KEY), saga.getClass().getName());
    }

    @Test
    public void buildTrigger_withGoodEntries_shouldReturnTrigger() throws NoSuchMethodException {
        // Given
        DateTime dateTime = DateTime.now().plus(5000);
        Method method = this.getClass().getDeclaredMethod("buildTrigger_withGoodEntries_shouldReturnTrigger");
        Saga saga = new TestFixture.TestSagaA();
        String identifier = UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(stepScheduler.buildJobIdentifier(saga, method, identifier), StepScheduler.DEFAULT_GROUP_NAME);

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
        Method method = this.getClass().getDeclaredMethod("schedule_whitGoodEntries_shouldScheduleJob");
        Saga saga = new TestFixture.TestSagaA();
        String identifier = UUID.randomUUID().toString();

        // When
        String jobIdentifier = stepScheduler.schedule(saga, method, identifier, dateTime);

        // Then
        assertNotNull(jobIdentifier);
    }

    @Test
    public void cancelSchedule_whitGoodEntries_shouldCancelScheduledJob() throws NoSuchMethodException, SchedulerException {
        // Given
        DateTime dateTime = DateTime.now().plus(5000);
        Method method = this.getClass().getDeclaredMethod("cancelSchedule_whitGoodEntries_shouldCancelScheduledJob");
        Saga saga = new TestFixture.TestSagaA();
        String identifier = UUID.randomUUID().toString();
        String jobIdentifier = stepScheduler.schedule(saga, method, identifier, dateTime);

        // When
        stepScheduler.cancelSchedule(saga, method, identifier, StepScheduler.DEFAULT_GROUP_NAME);

        // Then
        assertNotNull(jobIdentifier);
    }

}