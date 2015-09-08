// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.quartz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaExecutor;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.step.StepInvocationException;
import org.axonframework.common.Assert;
import org.axonframework.eventhandling.scheduling.SchedulingException;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.quartz.JobKey.jobKey;

/**
 * MethodInvocationScheduler implementation that delegates scheduling and triggering to a Quartz Scheduler.
 */
public class MethodInvocationScheduler implements com.viadeo.kasper.core.component.event.saga.step.Scheduler, SmartLifecycle {

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

    public static final String OBJECT_MAPPER_KEY = "ObjectMapper";

    /**
     * The key used to locate the method invoked in the JobExecutionContext.
     */
    public static final String METHOD_KEY = "Method";

    /**
     * The key used to locate the identifier invoked in the JobExecutionContext.
     */
    public static final String IDENTIFIER_KEY = "Identifier";

    public static final String IDENTIFIER_CLASS_KEY = "Identifier_class";

    public static final String SHOULD_END_SAGA_KEY = "Should_end_saga";

    public static final String DEFAULT_GROUP_NAME = "Kasper-Scheduled-Saga";

    private final ObjectMapper mapper;
    private final Scheduler scheduler;
    private final String groupIdentifier;
    private final SagaManager sagaManager;

    private boolean initialized;

    // ------------------------------------------------------------------------

    public MethodInvocationScheduler(final ObjectMapper mapper, final Scheduler scheduler, final SagaManager sagaManager) {
        this(mapper, scheduler, DEFAULT_GROUP_NAME, sagaManager);
    }

    public MethodInvocationScheduler(
            final ObjectMapper mapper,
            final Scheduler scheduler,
            final String groupIdentifier,
            final SagaManager sagaManager
    ) {
        this.mapper = mapper;
        this.scheduler = checkNotNull(scheduler);
        this.groupIdentifier = checkNotNull(groupIdentifier);
        this.sagaManager = checkNotNull(sagaManager);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize() {
        try {
            this.scheduler.getContext().put(OBJECT_MAPPER_KEY, mapper);
            this.scheduler.getContext().put(SAGA_MANAGER_KEY, sagaManager);
            this.scheduler.start();
            initialized = true;
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to initialize the scheduler", e);
        }
    }

    @Override
    public void shutdown() throws SchedulerException {
        this.scheduler.shutdown(true);
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    // ------------------------------------------------------------------------

    @Override
    public String schedule(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final Duration triggerDuration, final boolean endAfterExecution) {
        return schedule(sagaClass, methodName, identifier, new DateTime().plus(triggerDuration), endAfterExecution);
    }

    @Override
    public String schedule(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final DateTime triggerDateTime, final boolean endAfterExecution) {
        checkState(initialized, "Scheduler is not yet initialized");

        checkNotNull(identifier);
        checkNotNull(methodName);
        checkNotNull(sagaClass);
        checkNotNull(triggerDateTime);

        final String jobIdentifier = buildJobIdentifier(sagaClass, methodName, identifier);

        try {
            JobDetail jobDetail = buildJobDetail(sagaClass, methodName, identifier, jobKey(jobIdentifier, groupIdentifier), endAfterExecution);
            scheduler.scheduleJob(jobDetail, buildTrigger(triggerDateTime, jobDetail.getKey()));
        } catch (final SchedulerException e) {
            throw new SchedulingException("An error occurred while setting a timer for a saga", e);
        } catch (final JsonProcessingException e) {
            throw new SchedulingException("An error occurred while building the job detail for a saga", e);
        } catch (final Exception e) {
            throw new SchedulingException("An unexpected error occurred while setting a timer for a saga", e);
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
        } catch (final Exception e) {
            throw new SchedulingException("An unexpected error occurred while cancelling a timer for a saga", e);
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

    protected JobDetail buildJobDetail(final Class<? extends Saga> sagaClass, final String methodName, final Object identifier, final JobKey jobKey, final boolean endAfterExecution) throws JsonProcessingException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SAGA_CLASS_KEY, sagaClass.getName());
        jobDataMap.put(METHOD_KEY, methodName);
        jobDataMap.put(IDENTIFIER_KEY, mapper.writeValueAsString(identifier));
        jobDataMap.put(IDENTIFIER_CLASS_KEY, identifier.getClass().getName());
        jobDataMap.put(SHOULD_END_SAGA_KEY, endAfterExecution);

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

    @Override
    public void start() {
        this.initialize();
    }

    @Override
    public void stop() {
        try {
            this.shutdown();
        } catch (SchedulerException e) {
            LOGGER.error("Failed to shutdown the scheduler", e);
        }
    }

    @Override
    public boolean isRunning() {
        return isInitialized();
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

    // ------------------------------------------------------------------------

    public static class MethodInvocationJob implements Job {

        private static final Logger logger = LoggerFactory.getLogger(MethodInvocationJob.class);

        @Override
        public void execute(final JobExecutionContext context) throws JobExecutionException {
            logger.debug("Starting job to invoke scheduled saga step");

            final SagaManager sagaManager = getFromSchedulerContext(context, SAGA_MANAGER_KEY);
            final ObjectMapper mapper =  getFromSchedulerContext(context, OBJECT_MAPPER_KEY);

            checkNotNull(sagaManager);
            checkNotNull(mapper);

            final JobDataMap dataMap = context.getJobDetail().getJobDataMap();

            final String sagaMethodName = (String) checkNotNull(dataMap.get(METHOD_KEY));
            final String sagaClassName = (String) checkNotNull(dataMap.get(SAGA_CLASS_KEY));
            final String sagaIdentifier = (String) checkNotNull(dataMap.get(IDENTIFIER_KEY));
            final String sagaIdentifierClassName = (String) checkNotNull(dataMap.get(IDENTIFIER_CLASS_KEY));
            final boolean endAfterExecution = (boolean) checkNotNull(dataMap.get(SHOULD_END_SAGA_KEY));

            final Object identifier;

            try {
                final Class<Saga> sagaIdentifierClass = (Class<Saga>) Class.forName(sagaIdentifierClassName);
                identifier = mapper.readValue(sagaIdentifier, sagaIdentifierClass);

            } catch (final Exception e) {
                throw new StepInvocationException(
                        String.format(
                                "Error in invoking scheduled saga method: failed de deserialize identifier, <saga=%s> <method=%s> <identifier=%s>",
                                sagaClassName, sagaMethodName, sagaIdentifier
                        )
                );
            }

            try {
                final Class<Saga> sagaClass = (Class<Saga>) Class.forName(sagaClassName);
                final Optional<SagaExecutor> sagaExecutor = sagaManager.get(sagaClass);

                if (sagaExecutor.isPresent()){
                    sagaExecutor.get().execute(identifier, sagaMethodName, endAfterExecution);
                } else {
                    throw new StepInvocationException(
                        String.format(
                            "Error in invoking scheduled saga method: no saga executor, <saga=%s> <method=%s> <identifier=%s>",
                            sagaClassName, sagaMethodName, identifier
                        )
                    );
                }

            } catch (final Exception e) {
                throw new StepInvocationException(
                        String.format(
                                "Error in invoking scheduled saga method, <saga=%s> <method=%s> <identifier=%s>",
                                sagaClassName, sagaMethodName, identifier
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
