// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step.quartz;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaExecutor;
import com.viadeo.kasper.event.saga.SagaManager;
import com.viadeo.kasper.event.saga.step.StepInvocationException;
import org.axonframework.common.Assert;
import org.axonframework.eventhandling.scheduling.SchedulingException;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.quartz.JobKey.jobKey;

/**
 * MethodInvocationScheduler implementation that delegates scheduling and triggering to a Quartz Scheduler.
 */
public class MethodInvocationScheduler implements com.viadeo.kasper.event.saga.step.Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationScheduler.class);

    /**
     * The prefix of the job name
     */
    public static final String JOB_NAME_PREFIX = "Method-call";

    /**
     * The key used to locate the saga in the JobExecutionContext.
     */
    public static final String SAGA_CLASS_KEY = "Saga";

    /**
     * The key used to locate the saga in the JobExecutionContext.
     */
    public static final String SAGA_MANAGER_KEY = "SagaManager";

    /**
     * The key used to locate the method invoked in the JobExecutionContext.
     */
    public static final String METHOD_KEY = "Method";

    /**
     * The key used to locate the identifier invoked in the JobExecutionContext.
     */
    public static final String IDENTIFIER_KEY = "Identifier";

    public static final String DEFAULT_GROUP_NAME = "Kasper-Scheduled-Saga";

    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;
    private final String groupIdentifier;

    private boolean initialized;

    // ------------------------------------------------------------------------

    public MethodInvocationScheduler(final Scheduler scheduler, final ApplicationContext applicationContext) {
        this(scheduler, applicationContext, DEFAULT_GROUP_NAME);
    }

    public MethodInvocationScheduler(final Scheduler scheduler, final ApplicationContext applicationContext, String groupIdentifier) {
        this.scheduler = checkNotNull(scheduler);
        this.applicationContext = checkNotNull(applicationContext);
        this.groupIdentifier = checkNotNull(groupIdentifier);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize() {
        try {
            this.scheduler.getContext().put(SAGA_MANAGER_KEY, applicationContext.getBean(SagaManager.class));
            this.scheduler.start();
            initialized = true;
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to initialize the scheduler", e);
        }
    }

    public void shutdown() throws SchedulerException {
        this.scheduler.shutdown(true);
    }

    // ------------------------------------------------------------------------

    @Override
    public String schedule(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final Duration triggerDuration) {
        return schedule(sagaClass, methodName, identifier, new DateTime().plus(triggerDuration));
    }

    @Override
    public String schedule(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final DateTime triggerDateTime) {
        Assert.state(initialized, "Scheduler is not yet initialized");

        checkNotNull(identifier);
        checkNotNull(methodName);
        checkNotNull(sagaClass);
        checkNotNull(triggerDateTime);

        final String jobIdentifier = buildJobIdentifier(sagaClass, methodName, identifier);

        try {
            JobDetail jobDetail = buildJobDetail(sagaClass, methodName, identifier, jobKey(jobIdentifier, groupIdentifier));
            scheduler.scheduleJob(jobDetail, buildTrigger(triggerDateTime, jobDetail.getKey()));
        } catch (final SchedulerException e) {
            throw new SchedulingException("An error occurred while setting a timer for a saga", e);
        }

        return jobIdentifier;
    }

    // ------------------------------------------------------------------------

    @Override
    public void cancelSchedule(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier) {
        cancelSchedule(sagaClass, methodName, identifier, groupIdentifier);
    }

    public void cancelSchedule(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final String groupIdentifier) {
        Assert.state(initialized, "Scheduler is not yet initialized");

        checkNotNull(identifier);
        checkNotNull(methodName);
        checkNotNull(groupIdentifier);
        checkNotNull(sagaClass);

        try {
            if (!scheduler.deleteJob(jobKey(buildJobIdentifier(sagaClass, methodName, identifier), groupIdentifier))) {
                LOGGER.warn("The job belonging to this token could not be deleted : " + identifier);
            }
        } catch (SchedulerException e) {
            throw new SchedulingException("An error occurred while cancelling a timer for a saga", e);
        }
    }

    // ------------------------------------------------------------------------

    public boolean isScheduled(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier) {
        try {
            return scheduler.checkExists(jobKey(buildJobIdentifier(sagaClass, methodName, identifier), groupIdentifier));
        } catch (SchedulerException e) {
            LOGGER.error(
                    "Failed to known if a job is scheduled, <saga={}> <identifier={}> <methodInvocationName={}>",
                    sagaClass.getName(), identifier, methodName
            );
        }
        return Boolean.FALSE;
    }

    // ------------------------------------------------------------------------

    protected JobDetail buildJobDetail(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final JobKey jobKey) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SAGA_CLASS_KEY, sagaClass.getName());
        jobDataMap.put(METHOD_KEY, methodName);
        jobDataMap.put(IDENTIFIER_KEY, identifier);

        return JobBuilder.newJob(MethodInvocationJob.class)
                .withDescription(sagaClass.getName())
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


    protected String buildJobIdentifier(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier) {
        checkNotNull(sagaClass);
        checkNotNull(methodName);
        checkNotNull(identifier);
        return JOB_NAME_PREFIX + "_" + sagaClass.getName() + "_" + methodName + "_" + identifier.toString();
    }

    // ------------------------------------------------------------------------

    public static class MethodInvocationJob implements Job {

        private static final Logger logger = LoggerFactory.getLogger(MethodInvocationJob.class);

        @Override
        public void execute(final JobExecutionContext context) throws JobExecutionException {
            logger.debug("Starting job to invoke scheduled saga step");

            final String sagaMethodName = (String) context.getJobDetail().getJobDataMap().get(METHOD_KEY);
            final String sagaClassName = (String) context.getJobDetail().getJobDataMap().get(SAGA_CLASS_KEY);
            final Object sagaIdentifier = context.getJobDetail().getJobDataMap().get(IDENTIFIER_KEY);
            final SagaManager sagaManager = getFromSchedulerContext(context, SAGA_MANAGER_KEY);

            checkNotNull(sagaMethodName);
            checkNotNull(sagaClassName);
            checkNotNull(sagaIdentifier);
            checkNotNull(sagaManager);

            try {
                final Class<Saga> sagaClass = (Class<Saga>) Class.forName(sagaClassName);
                final Optional<SagaExecutor> sagaExecutor = sagaManager.get(sagaClass);

                if (sagaExecutor.isPresent()){
                    sagaExecutor.get().execute(sagaIdentifier, sagaMethodName);
                } else {
                    throw new StepInvocationException(
                        String.format(
                            "Error in invoking scheduled saga method: no saga executor, <saga=%s> <method=%s> <identifier=%s>",
                            sagaClassName, sagaMethodName, sagaIdentifier
                        )
                    );
                }

            } catch (final Exception e) {
                throw new StepInvocationException(
                        String.format(
                                "Error in invoking scheduled saga method, <saga=%s> <method=%s> <identifier=%s>",
                                sagaClassName, sagaMethodName, sagaIdentifier
                        ),
                        e
                );
            }
        }

        private <E> E getFromSchedulerContext(final JobExecutionContext context, final String key) {
            try {
                return (E) context.getScheduler().getContext().get(key);
            } catch (final SchedulerException e) {
                throw new RuntimeException(String.format("Error in executing method of a saga : a value is expected for the key '%s'", key), e);
            } catch (final ClassCastException e1) {
                throw new RuntimeException("Error in executing method of a saga", e1);
            }
        }
    }

}
