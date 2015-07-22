package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.springframework.amqp.core.Binding;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import rabbitmq.mgmt.RabbitMgmtService;
import rabbitmq.mgmt.model.Queue;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueueFinder {

    private final RabbitMgmtService rabbitMgmtService;
    private final String vhost;
    private final AMQPComponentNameFormatter amqpComponentNameFormatter;
    private final Environment environment;
    private final String currentExchangeName;
    private final String currentFallbackDeadLetterQueueName;

    public QueueFinder(
            AMQPComponentNameFormatter amqpComponentNameFormatter,
            RabbitMgmtService rabbitMgmtService,
            String vhost,
            Environment environment,
            String currentExchangeName,
            String currentFallbackDeadLetterQueueName
    ) {
        this.environment = checkNotNull(environment);
        this.amqpComponentNameFormatter = checkNotNull(amqpComponentNameFormatter);
        this.rabbitMgmtService = checkNotNull(rabbitMgmtService);
        this.vhost = checkNotNull(vhost);
        this.currentExchangeName = checkNotNull(currentExchangeName);
        this.currentFallbackDeadLetterQueueName = checkNotNull(currentFallbackDeadLetterQueueName);
    }

    protected Collection<Binding> getQueueBindings(String queueName) {
        Optional<QueueInfo> optionalQueueInfo = amqpComponentNameFormatter.extractQueueInfo(queueName);
        if (optionalQueueInfo.isPresent()) {
            return getQueueBindings(optionalQueueInfo.get());
        }
        return Lists.newArrayList();
    }

    protected Collection<Binding> getQueueBindings(QueueInfo queueInfo) {
        List<Binding> bindings = Lists.newArrayList();
        Optional<Collection<rabbitmq.mgmt.model.Binding>> optionalBindings = rabbitMgmtService.bindings().get(
                vhost,
                queueInfo.isDeadLetter() ? queueInfo.getExchangeName() + AMQPComponentNameFormatter.DEAD_LETTER_SUFFIX : queueInfo.getExchangeName(),
                queueInfo.getQueueName(),
                false
        );

        if (optionalBindings.isPresent()) {
            for (rabbitmq.mgmt.model.Binding binding : optionalBindings.get()) {
                bindings.add(
                        new Binding(
                                binding.getDestination(),
                                Binding.DestinationType.valueOf(binding.getDestinationType().toUpperCase()),
                                binding.getSource(),
                                binding.getRoutingKey(),
                                binding.getArguments()
                        )
                );
            }
        }

        return bindings;
    }

    protected Collection<QueueInfo> getObsoleteQueueNames() {
        final List<QueueInfo> obsoleteQueues = Lists.newArrayList();
        final Optional<Collection<Queue>> optionalQueues = rabbitMgmtService.queues().allOnVHost(vhost);

        if (optionalQueues.isPresent()) {
            final Collection<Queue> queues = optionalQueues.get();

            for (final Queue queue:queues) {
                final String name = queue.getName();

                if ( currentFallbackDeadLetterQueueName.equals(name)) {
                    continue;
                }

                final Optional<QueueInfo> optionalQueueInfo = amqpComponentNameFormatter.extractQueueInfo(name);

                if ( ! optionalQueueInfo.isPresent()) {
                    continue;
                }

                final QueueInfo queueInfo = optionalQueueInfo.get();

                if( ! currentExchangeName.equals(queueInfo.getExchangeName())) {
                    obsoleteQueues.add(queueInfo);
                    continue;
                }

                try {
                    Class<?> clazz = Class.forName(queueInfo.getEventListenerClassName());
                    Profile annotation = clazz.getAnnotation(Profile.class);
                    if (annotation != null) {
                        if ( ! environment.acceptsProfiles(annotation.value())) {
                            obsoleteQueues.add(queueInfo);
                        }

                    }
                } catch (ClassNotFoundException e2) {
                    obsoleteQueues.add(queueInfo);
                }
            }
        }

        return obsoleteQueues;
    }
}
