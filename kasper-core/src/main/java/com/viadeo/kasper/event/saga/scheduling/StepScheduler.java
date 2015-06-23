// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.scheduling;

import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaManager;
import org.axonframework.common.Assert;
import org.axonframework.eventhandling.scheduling.SchedulingException;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.quartz.JobKey.jobKey;

public class StepScheduler implements SagaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StepScheduler.class);
    protected static final String JOB_NAME_PREFIX = "Method-call";
    protected static final String DEFAULT_GROUP_NAME = "Kasper-Scheduled-Saga";

    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;

    private String groupIdentifier = DEFAULT_GROUP_NAME;

    private boolean initialized;

    public StepScheduler(final Scheduler scheduler, final ApplicationContext applicationContext) {
        this.scheduler = checkNotNull(scheduler);
        this.applicationContext = checkNotNull(applicationContext);
    }

    /**
     * Initializes the StepScheduler. Will make the configured SagaManager available to the Quartz Scheduler
     *
     * @throws SchedulerException if an error occurs preparing the Quartz Scheduler for use.
     */
    @PostConstruct
    public void initialize() throws SchedulerException {
        this.scheduler.getContext().put(InvokeScheduledStepJob.SAGA_MANAGER_KEY, applicationContext.getBean(SagaManager.class));
        this.scheduler.start();
        initialized = true;
    }

    public void shutdown() throws SchedulerException {
        this.scheduler.shutdown(true);
    }

    public String schedule(final Saga saga, final Method method, final String identifier, final Duration triggerDuration) {
        return schedule(saga, method, identifier, new DateTime().plus(triggerDuration));
    }

    public String schedule(final Saga saga, final Method method, final String identifier, final DateTime triggerDateTime) {
        Assert.state(initialized, "Scheduler is not yet initialized");
        checkNotNull(identifier);
        checkNotNull(method);
        checkNotNull(saga);
        checkNotNull(triggerDateTime);

        final String jobIdentifier = buildJobIdentifier(saga, method, identifier);
        try {
            JobDetail jobDetail = buildJobDetail(saga, method, identifier, jobKey(jobIdentifier, groupIdentifier));
            scheduler.scheduleJob(jobDetail, buildTrigger(triggerDateTime, jobDetail.getKey()));
        } catch (SchedulerException e) {
            throw new SchedulingException("An error occurred while setting a timer for a saga", e);
        }
        return jobIdentifier;
    }

    public void cancelSchedule(final Saga saga, final Method method, final String identifier, final String groupIdentifier) {
        Assert.state(initialized, "Scheduler is not yet initialized");
        checkNotNull(identifier);
        checkNotNull(method);
        checkNotNull(groupIdentifier);
        checkNotNull(saga);

        try {
            if (!scheduler.deleteJob(jobKey(buildJobIdentifier(saga, method, identifier), groupIdentifier))) {
                logger.warn("The job belonging to this token could not be deleted : " + identifier);
            }
        } catch (SchedulerException e) {
            throw new SchedulingException("An error occurred while cancelling a timer for a saga", e);
        }
    }

    /**
     * Builds the JobDetail instance for Quartz, which defines the Job that needs to be executed when the trigger
     * fires.
     * <p/>
     * The resulting JobDetail must be identified by the given <code>jobKey</code> and represent a Job that dispatches
     * the given <code>saga</code>.
     * <p/>
     * This method may be safely overridden to change behavior. Defaults to a JobDetail to fire a InvokeScheduledStepJob
     *
     * @param saga   The saga to be scheduled for dispatch
     * @param jobKey The key of the Job to schedule
     * @return a JobDetail describing the Job to be executed
     */
    protected JobDetail buildJobDetail(final Saga saga, final Method method, final String identifier, final JobKey jobKey) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(InvokeScheduledStepJob.SAGA_KEY, saga.getClass().getName());
        jobDataMap.put(InvokeScheduledStepJob.METHOD_KEY, method.getName().toString());
        jobDataMap.put(InvokeScheduledStepJob.IDENTIFIER_KEY, identifier);
        return JobBuilder.newJob(InvokeScheduledStepJob.class)
                .withDescription(saga.getClass().getName())
                .withIdentity(jobKey)
                .usingJobData(jobDataMap)
                .build();
    }

    /**
     * Builds a Trigger which fires the Job identified by <code>jobKey</code> at (or around) the given
     * <code>triggerDateTime</code>.
     *
     * @param triggerDateTime The time at which a trigger was requested
     * @param jobKey          The key of the job to be triggered
     * @return a configured Trigger for the Job with key <code>jobKey</code>
     */
    protected Trigger buildTrigger(final DateTime triggerDateTime, final JobKey jobKey) {
        return TriggerBuilder.newTrigger()
                .forJob(jobKey)
                .startAt(triggerDateTime.toDate())
                .build();
    }


    protected String buildJobIdentifier(final Saga saga, final Method method, final String identifier) {
        return JOB_NAME_PREFIX + "_" + saga.getClass().getName() + "_" + method.getName().toString() + "_" + identifier;
    }

    /**
     * Sets the group identifier to use when scheduling jobs with Quartz. Defaults to "Kasper-Scheduled-Saga".
     *
     * @param groupIdentifier the group identifier to use when scheduling jobs with Quartz
     */
    public void setGroupIdentifier(final String groupIdentifier) {
        this.groupIdentifier = checkNotNull(groupIdentifier);
    }

}
