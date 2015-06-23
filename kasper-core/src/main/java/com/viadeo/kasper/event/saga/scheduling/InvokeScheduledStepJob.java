// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.scheduling;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaExecutor;
import com.viadeo.kasper.event.saga.SagaManager;
import com.viadeo.kasper.event.saga.step.StepInvocationException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

public class InvokeScheduledStepJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(InvokeScheduledStepJob.class);

    /**
     * The key used to locate the saga in the JobExecutionContext.
     */
    public static final String SAGA_KEY = "Saga";

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


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Starting job to invoke scheduled saga step");

        String sagaMethodName = (String) context.getJobDetail().getJobDataMap().get(METHOD_KEY);
        String sagaClassName = (String) context.getJobDetail().getJobDataMap().get(SAGA_KEY);
        String sagaIdentifier = (String) context.getJobDetail().getJobDataMap().get(IDENTIFIER_KEY);
        SagaManager sagaManager = (SagaManager) context.getJobDetail().getJobDataMap().get(SAGA_MANAGER_KEY);

        checkNotNull(sagaClassName);
        checkNotNull(sagaMethodName);

        try {
            Class<Saga> sagaClass = (Class<Saga>) Class.forName(sagaClassName);
            Optional<SagaExecutor> sagaExecutor = sagaManager.get(sagaClass);

            if (sagaExecutor.isPresent()){
                sagaExecutor.get().execute(sagaMethodName, sagaIdentifier);
            } else {
                throw new StepInvocationException(
                        String.format(
                                "Error in invoking scheduled step, <step=%s> <method=%s> <identifier=%s>",
                                sagaClassName, sagaMethodName, sagaIdentifier
                        ));
            }

        } catch (Exception e) {
            throw new StepInvocationException(
                    String.format(
                            "Error in invoking scheduled step, <step=%s> <method=%s> <identifier=%s>",
                            sagaClassName, sagaMethodName, sagaIdentifier
                    ),
                    e
            );
        }
    }
}
