package com.viadeo.kasper.core.component.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.axonframework.eventhandling.EventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class AMQPComponentNameFormatter {

    protected static final String DEAD_LETTER_SUFFIX = "_dead-letter";

    private String exchangeNameFormat = "%exchange%-%exchange-version%";
    private String deadLetterExchangeNameFormat = "%exchange%-%exchange-version%" + DEAD_LETTER_SUFFIX;
    private String queueNameFormat = "%exchange%_%cluster%_%listener%";
    private String deadLetterQueueNameFormat = "%queue%" + DEAD_LETTER_SUFFIX;

    private Pattern queueNamePattern = Pattern.compile("([^_]+)_([^_]+)_(.+)");
    private Pattern deadLetterqueueNamePattern = Pattern.compile("([^_]+)_([^_]+)_(.+)" + DEAD_LETTER_SUFFIX);

    public String getQueueName(String exchangeName, String clusterName, EventListener eventListener) {
        return this.getQueueName(exchangeName, clusterName, eventListener.getClass().getName());
    }

    @VisibleForTesting
    protected String getQueueName(String exchangeName, String clusterName, String eventListenerClassName) {
        return queueNameFormat
                .replace("%exchange%", exchangeName)
                .replace("%cluster%", clusterName)
                .replace("%listener%", eventListenerClassName);
    }

    public Optional<QueueInfo> extractQueueInfo(String queueName) {
        checkNotNull(queueName);

        Matcher matcher = deadLetterqueueNamePattern.matcher(queueName);

        if ( ! matcher.find()) {
            matcher = queueNamePattern.matcher(queueName);

            if ( ! matcher.find()) {
                return Optional.absent();
            }
        }

        return Optional.of(
                new QueueInfo(
                        queueName,
                        matcher.group(1),
                        matcher.group(3),
                        queueName.endsWith(DEAD_LETTER_SUFFIX) ? Boolean.TRUE : Boolean.FALSE
                )
        );
    }

    public String getDeadLetterQueueName(String exchangeName, String clusterName, EventListener eventListener) {
        return deadLetterQueueNameFormat
                .replace("%queue%", getQueueName(exchangeName, clusterName, eventListener.getClass().getName()));
    }

    public String getFallbackDeadLetterQueueName(String exchangeName, String exchangeVersion, String clusterName) {
        return exchangeName + "-" + exchangeVersion + "_" + clusterName + DEAD_LETTER_SUFFIX;
    }

    public String getFullExchangeName(String name, String version) {
        return exchangeNameFormat.replace("%exchange%", name).replace("%exchange-version%", version);
    }

    public String getDeadLetterExchangeName(String name, String version) {
        return deadLetterExchangeNameFormat.replace("%exchange%", name).replace("%exchange-version%", version);
    }

    /**
     * Set the name format used to create the queues
     * this format accept 3 placeholders :
     * - %cluster%
     * - %exchange%
     * - %listener%
     *
     * @param queueNameFormat the queue name format
     */
    public void setQueueNameFormat(String queueNameFormat) {
        this.queueNameFormat = checkNotNull(queueNameFormat);
        String regex = queueNameFormat
                .replace("%exchange%", "([^_]+)")
                .replace("%cluster%", "([^_]+)")
                .replace("%listener%", "(.+)");

        this.queueNamePattern = Pattern.compile(regex);
        this.deadLetterqueueNamePattern = Pattern.compile(regex + DEAD_LETTER_SUFFIX);
    }

    /**
     * Set the dead letter exchange name format
     * this format accept 2 placeholder :
     *  - %exchange%
     *  - %exchange-version%
     *
     * @param deadLetterExchangeNameFormat the dead-letter exchange name format
     */
    public void setDeadLetterExchangeNameFormat(String deadLetterExchangeNameFormat) {
        this.deadLetterExchangeNameFormat = checkNotNull(deadLetterExchangeNameFormat);
    }

    /**
     * Set the dead letter queue name format
     * this format accept 1 placeholder :
     * - %queue%
     *
     * @param deadLetterQueueNameFormat the dead-letter queue name format
     */
    public void setDeadLetterQueueNameFormat(String deadLetterQueueNameFormat) {
        this.deadLetterQueueNameFormat = checkNotNull(deadLetterQueueNameFormat);
    }

    /**
     * Set the exchange name format
     * this format accept 2 placeholder :
     *  - %exchange%
     *  - %exchange-version%
     * @param exchangeNameFormat the exchange name format
     */
    public void setExchangeNameFormat(String exchangeNameFormat) {
        this.exchangeNameFormat = checkNotNull(exchangeNameFormat);
    }
}
