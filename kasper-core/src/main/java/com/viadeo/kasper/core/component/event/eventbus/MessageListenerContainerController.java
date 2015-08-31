package com.viadeo.kasper.core.component.event.eventbus;

public interface MessageListenerContainerController {

    /**
     *
     * @param messageListenerContainer the messageListenerContainer
     * @return true if the specified container can be started.
     */
    boolean canStart(MessageListenerContainer messageListenerContainer);

    final class NoController implements MessageListenerContainerController {
        @Override
        public boolean canStart(MessageListenerContainer messageListenerContainer) {
            return true;
        }
    }

}
