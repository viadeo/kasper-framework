package com.viadeo.kasper.event.saga.scheduling;

import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaManager;
import com.viadeo.kasper.event.saga.TestFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepSchedulerUTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SagaManager sagaManager;

    private StepScheduler stepScheduler;

    @Before
    public void setup(){
        when(applicationContext.getBean(SagaManager.class)).thenReturn(sagaManager);
        stepScheduler = new StepScheduler(scheduler, applicationContext);
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
        JobDetail jobDetail = stepScheduler.buildJobDetail(method, saga, identifier, jobKey);

        // Then

    }

    @Test
    public void buildTrigger(){

    }

}