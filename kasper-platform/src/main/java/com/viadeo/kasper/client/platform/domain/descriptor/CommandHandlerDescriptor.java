package com.viadeo.kasper.client.platform.domain.descriptor;

public class CommandHandlerDescriptor {
    private final Class commandHandlerClass;
    private final Class commandClass;

    public CommandHandlerDescriptor(Class commandHandlerClass, Class commandClass) {
        this.commandHandlerClass = commandHandlerClass;
        this.commandClass = commandClass;
    }

    public Class getReferenceClass() {
        return commandHandlerClass;
    }

    public Class getCommandClass() {
        return commandClass;
    }
}