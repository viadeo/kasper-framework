package com.viadeo.kasper.core.component.eventbus;

public class QueueInfo {

    private final String queueName;
    private final String exchangeName;
    private final String eventListenerClassName;
    private final Boolean deadLetter;

    public QueueInfo(String queueName, String exchangeName, String eventListenerClassName, boolean isDeadLetter) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.eventListenerClassName = eventListenerClassName;
        this.deadLetter = isDeadLetter;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getEventListenerClassName() {
        return eventListenerClassName;
    }

    public String getQueueName() {
        return queueName;
    }

    public Boolean isDeadLetter() {
        return deadLetter;
    }
}
