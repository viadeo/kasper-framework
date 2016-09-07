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

import java.util.UUID;

import static com.viadeo.kasper.core.component.event.saga.step.quartz.MethodInvocationScheduler.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MethodInvocationSchedulerUTest {

    @Mock
    private SagaManager sagaManager;

    private MethodInvocationScheduler stepScheduler;

    @Before
    public void setup() throws SchedulerException {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        stepScheduler = new MethodInvocationScheduler(
                ObjectMapperProvider.INSTANCE.mapper(),
                scheduler,
                sagaManager
        );
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
